package mnpsync;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import java.io.File;

public class Config {
    public static PropertiesConfiguration c;

    public static void init() {
        try {
            Parameters params = new Parameters();
            c = new FileBasedConfigurationBuilder<>(
                PropertiesConfiguration.class).configure(
                params.properties().setFile(new File(getProgramPath(),"config.properties"))).getConfiguration();

            c.addProperty("base_path", getProgramPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProgramPath() {
        String path = Config.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        return path.substring(0, path.lastIndexOf("/"));
    }
}
