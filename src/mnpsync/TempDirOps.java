package mnpsync;

import java.io.File;

public class TempDirOps {
    public static final String tempDirName = Config.c.getString("base_path") + "/temp/";

    static {
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
