package client.controller;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private final static Logger logger = Logger.getLogger(PropertiesLoader.class);

    public PropertiesLoader() {
    }

    /**
     * Method for loading properties from file to object and return this object
     * @param fileName path/file with properties
     * @return object of class Properties with load of properties from fileName
     */
    public Properties loadProperty(String fileName) {
        Properties properties = new Properties();
        InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
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
