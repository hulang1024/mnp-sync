package mnpsync;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import java.io.File;

public class Config {
    private static PropertiesConfiguration config;

    public static void init() {
        if (System.getProperty("base.dir") == null) {
            System.setProperty("base.dir", System.getProperty("user.dir"));
        }
        try {
            config = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                    .setFile(new File(System.getProperty("base.dir"),"config.properties")))
                .getConfiguration();

            config.addProperty("base.dir", System.getProperty("base.dir"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(String key) {
        return config.getString(key);
    }
}
