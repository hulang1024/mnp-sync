package mnpsync;

import java.io.File;

public class TempDirOps {
    public static final String tempDirName;

    static {
        String baseUrl = Config.getString("base.dir");
        tempDirName = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "temp/";
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    public static void clean() {
        System.out.println("清空目录" + tempDirName);
        File tempDir = new File(tempDirName);
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
    }
}
