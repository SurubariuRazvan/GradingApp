package config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final String CONFIG_LOCATION = "./src/main/resources/config.properties";
    //Config.class.getClassLoader().getResource("config.properties").getFile();

    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(CONFIG_LOCATION));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config properties");
        }
    }
}
