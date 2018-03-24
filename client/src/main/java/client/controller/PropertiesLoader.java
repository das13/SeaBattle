package client.controller;

import client.MainLauncher;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private final static Logger logger = Logger.getLogger(PropertiesLoader.class);

    public PropertiesLoader() {
    }

    public Properties loadProperty(String fileName) {
        Properties properties = new Properties();
        InputStream in = MainLauncher.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Can not load " + fileName, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Can not close InputStream in loadProperty(String fileName)", e);
                }
            }
        }
        return properties;
    }
}
