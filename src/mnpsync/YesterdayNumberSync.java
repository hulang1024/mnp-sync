package mnpsync;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class YesterdayNumberSync {
    private static final Logger logger = Logger.getLogger(YesterdayNumberSync.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static void execute() throws Exception {
        System.out.println("处理开始\n");
        long startTime = System.currentTimeMillis();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        final String dateString = dateFormat.format(yesterday.getTime());

        int partCount = Pull.pullByDate(dateString);
        if (partCount == 0) {
            logger.info("获取文件或文件内容失败");
            return;
        }

        Merge.merge();

        Sync.Result result = Sync.sync();
        if (result.success) {
            System.out.printf("处理完成 总共耗时%.2f秒\n", (System.currentTimeMillis() - startTime) / 1000f);
            logger.info("已处理" + dateString + "的" + result.numberTotal + "个号码记录");
        } else {
            logger.info("失败");
        }
        System.out.println();
    }
}
