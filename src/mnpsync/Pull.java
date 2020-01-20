package mnpsync;

import org.apache.commons.lang3.time.StopWatch;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Pull {
    public static int pullByDate(String date) throws Exception {
        System.out.printf("拉取日期%s文件开始\n", date);
        StopWatch watch = StopWatch.createStarted();
        TempDirOps.clean();
        int partCount = 0;
        Integer[] fileBytes;
        do {
            partCount++;
            String partFileName = date + "_" + partCount + ".tim"; // 拼接 <日期>_<部分号>.tim 作为文件名
            System.out.printf("下载%s第%d个分文件 %-14s", date, partCount, partFileName);
            fileBytes = FileApis.pullFileBytes(partFileName);
            if (fileBytes != null) {
                OutputStream out = new FileOutputStream(TempDirOps.tempDirName + partFileName);
                for (int b : fileBytes) out.write(b);
                out.close();
                System.out.println(" 完成");
            } else {
                partCount--;
                System.out.println(partCount > 0 ? " 不存在更多分文件" : " 不存在");
            }
        } while (fileBytes != null);
        if (partCount > 0) {
            System.out.printf("拉取完成 总共%d个文件,耗时%.2f秒\n\n", partCount, watch.getTime() / 1000f);
        }
        return partCount;
    }

    public static void pullAll() {
        System.out.println("拉取历史所有文件开始");
        StopWatch watch = StopWatch.createStarted();
        TempDirOps.clean();
        System.out.println("请求文件名列表");
        List<String> filenames = FileApis.pullFileNameList();
        if (filenames == null || filenames.isEmpty()) {
            return;
        }
        filenames = filterAndSortFilenames(filenames);
        System.out.println("开始下载" + filenames.size() + "个文件");
        int count = 0;
        for (String filename : filenames) {
            ++count;
            System.out.printf("下载第%2d个文件 %-14s", count, filename);
            Integer[] fileBytes = FileApis.pullFileBytes(filename);
            if (fileBytes == null) {
                break;
            }
            try {
                OutputStream out = new FileOutputStream(TempDirOps.tempDirName + filename);
                for (int b : fileBytes) out.write(b);
            } catch (Exception e) {
                System.out.println("写文件发生错误");
                break;
            }
            System.out.println(" 完成");
        }
        System.out.printf("全部完成 耗时%.2f秒\n\n", watch.getTime() / 1000f);
    }

    public static List<String> filterAndSortFilenames(List<String> filenames) {
        //如果有月份文件，只保留月份文件
        List<String> result = new ArrayList<>();
        List<String> months = new ArrayList<>();
        for (String filename : filenames) {
            if (filename.indexOf("_") < 7) {
                result.add(filename);
                months.add(filename.substring(0, 6));
            }
        }
        for (String filename : filenames) {
            if (!months.contains(filename.substring(0, 6))) {
                result.add(filename);
            }
        }
        // 按日期排序
        Function<String, Integer> toOrderNum = filename -> {
            int partStartIndex = filename.indexOf("_");
            String date = filename.substring(0, partStartIndex);
            int num = Integer.parseInt(date.length() < 7 ? date + "01" : date);
            num += Integer.parseInt(filename.substring(partStartIndex + 1, filename.length() - 4));
            return num;
        };
        result.sort((x, y) -> (int)toOrderNum.apply(x) - toOrderNum.apply(y));

        return result;
    }
}
