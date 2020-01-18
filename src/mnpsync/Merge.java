package mnpsync;

import com.jjm.file.FileMerge;

public class Merge {
    public static void merge() {
        System.out.println("合并开始");
        FileMerge.merge(TempDirOps.tempDirName);
        System.out.println("合并完成\n");
    }
}
