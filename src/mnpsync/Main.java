package mnpsync;

import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) throws Exception {
        if (!Charset.defaultCharset().toString().equals("UTF-8")) {
            System.out.println("The default character set must be UTF-8");
            return;
        }

        Config.init();
        FileApis.init();

        if (args.length == 0) {
            YesterdayNumberSync.execute();
        } else {
            if ("pull".equals(args[0]) && args.length == 2) {
                if ("all".equals(args[1])) {
                    Pull.pullAll();
                } else {
                    Pull.pullByDate(args[1]);
                }
            } else if ("merge".equals(args[0])) {
                Merge.merge();
            } else if ("sync".equals(args[0])) {
                Sync.sync();
            }
        }
    }
}
