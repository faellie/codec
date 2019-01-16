package zwang.codec.mixedhamming;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil
{
    private static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private static Properties config = new Properties();
    private static boolean initialized = false;

    public static int DEF_START_POSITION = 6;
    public static int DEF_MAX_ERRORS = 16;

    //number of bits for the actual data
    public static int DATA_LENGTH = 16;
    public static int CODE_WORD_LENGTH = 22;
    public static int PARITY_LENGTH = 6;

    public static int MAX_INPUT = (int)Math.pow(2, DATA_LENGTH) - 1;

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
            logger.warn("Failed to get " + aInKey + ". Exception: " + e);
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
            logger.warn("Failed to get " + aInKey + ". Exception: " + e);
        }

        logger.warn("returning default: " + aInDef);
        return aInDef;

    }
}
