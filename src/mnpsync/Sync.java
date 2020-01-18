package mnpsync;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Sync {
    public static class Result {
        public boolean success;
        public int numberTotal;
    }
    
    public static Sync.Result sync() throws Exception {
        System.out.println("同步开始");
        long startTime = System.currentTimeMillis();
        Sync.Result result = new Sync.Result();
        result.success = true;
        File tempDir = new File(TempDirOps.tempDirName);
        int count = 1;
        List<File> files = new ArrayList<>();
        for (File file : tempDir.listFiles()) {
            if (file.getName().endsWith(".zip")) {
                files.add(file);
            }
        }
        System.out.println("发现" + files.size() + "个合并文件");
        for (File file : files) {
            System.out.printf("处理第%2d个文件 %-14s", count++, file.getName());
            Sync.Result ret = readSingleMergeFilesAndSync(file);
            System.out.printf(" 完成 %7d个号码记录\n", ret.numberTotal);
            result.numberTotal += ret.numberTotal;
            if (!ret.success) {
                result.success = false;
                break;
            }
        }
        System.out.printf("同步完成 总共%d个号码记录,耗时%.2f秒\n\n",
            result.numberTotal, (System.currentTimeMillis() - startTime) / 1000f);
        return result;
    }

    public static Sync.Result readSingleMergeFilesAndSync(File file) throws Exception {
        Sync.Result result = new Sync.Result();
        String fileName = file.getName();
        if (fileName.startsWith("\\")) {
            fileName = fileName.substring(1, fileName.indexOf("."));
        }
        File redisCommandsFile = new File(TempDirOps.tempDirName, fileName + "-redis-commands.txt");
        final FileWriter redisCommandsFileWriter = new FileWriter(redisCommandsFile);

        result.numberTotal = readZipAndForeachRecords(file.getAbsolutePath(),
            (csvLine) -> {
                String[] expr = csvLine.split(",");
                try {
                    String cmdType, key;
                    if (expr.length == 3) {
                        cmdType = expr[0];
                        key = "mnp" + expr[1];
                    } else if (expr.length == 2) {
                        cmdType = "I";
                        key = "mnp" + expr[0];
                    } else {
                        cmdType = "null";
                        key = null;
                    }
                    switch (cmdType) {
                        case "I":
                            int val = 0;
                            switch (expr.length == 3 ? expr[2] : expr[1]) {
                                case "CMCC": val = 1; break;
                                case "CU":   val = 2; break;
                                case "CT":   val = 4; break;
                            }
                            redisCommandsFileWriter.write(new StringBuilder("set ")
                                .append(key).append(" ").append(val).append("\r\n").toString());
                            break;
                        case "D":
                            redisCommandsFileWriter.write(new StringBuilder("del ")
                                .append(key).append("\r\n").toString());
                            break;
                    }
                } catch (IOException e) {}
            });
        redisCommandsFileWriter.flush();
        redisCommandsFileWriter.close();

        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c",
            "cat " + redisCommandsFile.getAbsolutePath() +
            " | " + Config.getString("redis_cli") + " -h " + Config.getString("redis_server.host") +
            " -p " + Config.getString("redis_server.port") +
            " --pipe"});

        result.success = process.waitFor() == 0;

        return result;
    }

    public static int readZipAndForeachRecords(String zipFileName, Consumer<String> csvLineConsumer) {
        try {
            ZipFile zipFile = new ZipFile(zipFileName);
            InputStream in = new BufferedInputStream(new FileInputStream(zipFileName));
            ZipInputStream zipIn = new ZipInputStream(in);
            ZipEntry zipEntry;

            int total = 0;

            // 读 每个zip项
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                // 读 号码记录条数
                int numberCount = Integer.parseInt(reader.readLine());
                total += numberCount;

                // 读 每一行CSV格式记录行
                for (String line; (line = reader.readLine()) != null; ) {
                    csvLineConsumer.accept(line);
                }
                reader.close();
            }
            zipIn.close();
            return total;
        } catch (Exception e) {
            return 0;
        }
    }
}
