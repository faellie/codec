package zwang.codec.mixedhamming;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil
{
    private static Properties config = new Properties();
    private static boolean initialized = false;

    public static int DEF_START_POSITION = 6;
    public static int DEF_MAX_ERRORS = 16;
    public static void init() {
        FileInputStream lIn = null;
        try {
            lIn = new FileInputStream("config.properties");
            config.load(lIn);
        } catch (FileNotFoundException e) {
            //System.out.println("missing config.properties");
        } catch (IOException e) {
           // System.out.println("Failed to load config.properties");
        }
        initialized = true;
    }

    public static int getIntProp(String aInKey, int aInDef) {
        if(! initialized ) {
            init();
        }
        try {
            return Integer.parseInt(config.getProperty(aInKey));
        } catch (Exception e) {
            System.out.println("Failed to get " + aInKey + ". Exception: " + e);
        }

        System.out.println("returning default: " + aInDef);
        return aInDef;

    }

    public static boolean getBoolProp(String aInKey, boolean aInDef) {
        if(! initialized ) {
            init();
        }
        try {
            return Boolean.parseBoolean(config.getProperty(aInKey));
        } catch (Exception e) {
            System.out.println("Failed to get " + aInKey + ". Exception: " + e);
        }

        System.out.println("returning default: " + aInDef);
        return aInDef;

    }
}
