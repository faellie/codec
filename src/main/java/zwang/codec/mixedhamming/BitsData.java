package zwang.codec.mixedhamming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitsData
{
    private static Logger logger = LoggerFactory.getLogger(BitsData.class);
    private int[] dataArray = new int[ConfigUtil.DATA_LENGTH];

    public BitsData(int input) {
        for(int i = 0; i < dataArray.length; i ++)
            dataArray[i] = 0;
        if(input > ConfigUtil.MAX_INPUT) {
            logger.error("Cannot encode " + input + ".Maximum input is " + ConfigUtil.MAX_INPUT);
            return;
        }
        else {
            // we put the lowest bit on the first index and pad the rest of dataArray as 0
            // ex 13 ==> 1101 => dataArray = [1011000000000000]
            for(int j = 0; j < dataArray.length; j++) {
                dataArray[j] = (input >> j) & 1;
            }
        }
    }

    public int[] getDataArray() {
        return dataArray;
    }

    public int toInt() {
        int ret = 0;
        for(int i = 0; i < ConfigUtil.DATA_LENGTH; i++) {
            ret = ret + dataArray[i] * (1<<i);
        }
        return ret;
    }

    public static int[] bitsArrayFromStr(String aInBitsStr){
        int length = aInBitsStr.length();
        int[] ret = new int[length];
        for(int i = 0; i < length; i++) {
            ret[i] = Integer.parseInt(aInBitsStr.substring(i, i+1));
        }
        return ret;
    }
    public String toString() {
        return toString(dataArray);
    }

    public static String toString(int [] aInArray) {
        return toString(aInArray, false);
    }

    public static String toString(int [] aInArray, boolean revise) {
        String ret = "";

        if(revise) {
            for (int i = aInArray.length - 1; i >= 0; i--) {
                ret = ret + aInArray[i];
            }
        } else {
            for (int i = 0; i < aInArray.length; i++) {
                ret = ret + aInArray[i];
            }
        }
        return ret;
    }
}
