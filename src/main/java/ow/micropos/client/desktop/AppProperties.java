package ow.micropos.client.desktop;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class AppProperties {

    /*=======================================================================*
     =                                                                       =
     = Factory methods
     =                                                                       =
     *=======================================================================*/

    public static AppProperties fromResource(String resource) {
        InputStream in = AppProperties.class.getResourceAsStream("/" + resource);
        return new AppProperties(in);
    }

    public static AppProperties fromPath(String path) {
        try {
            InputStream in = new FileInputStream(path);
            return new AppProperties(in);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static AppProperties fromFile(File file) {
        try {
            InputStream in = new FileInputStream(file);
            return new AppProperties(in);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*=======================================================================*
     =                                                                       =
     = Properties wrapper for type casting
     =                                                                       =
     *=======================================================================*/

    private final Properties properties;

    public AppProperties() {
        properties = new Properties();
    }

    public AppProperties(InputStream in) {
        properties = new Properties();

        try {
            properties.load(in);

        } catch (IOException e) {
            System.out.println("Error loading properties.");
            throw new RuntimeException(e.getMessage());

        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void save(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.store(fos, "AUTO GENERATED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getProperties() {
        Enumeration<?> keyEnum = properties.propertyNames();
        List<String> keyList = new ArrayList<>();
        while (keyEnum.hasMoreElements())
            keyList.add(keyEnum.nextElement().toString());
        return keyList;
    }

    public void add(String property, String value) {
        properties.put(property, value);
    }

    public boolean has(String property) {
        return properties.getProperty(property) != null;
    }

    public String getStr(String property) {
        return properties.getProperty(property);
    }

    public Integer getInt(String property) {
        return Integer.parseInt(properties.getProperty(property));
    }

    public Double getDbl(String property) {
        return Double.parseDouble(properties.getProperty(property));
    }

    public Long getLng(String property) {
        return Long.parseLong(properties.getProperty(property));
    }

    public BigDecimal getBd(String property) {
        return new BigDecimal(properties.getProperty(property));
    }

}
