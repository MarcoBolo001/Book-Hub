package it.unipi.lsmsd.Utils;


import java.io.FileInputStream;
import java.util.Properties;

public class Utils {
    public static Properties readConfigurationParameters(){
        try{
            FileInputStream fileInputStream=new FileInputStream(Utils.class.getResource("/config/config.properties").toURI().getPath());
            Properties properties=new Properties();
            properties.load(fileInputStream);
            return properties;
        }catch (Exception e){
            System.out.println("problems with property file reading");
            e.printStackTrace();
        }
        return null;
    }
}
