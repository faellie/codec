package zwang.codec.mixedhamming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitsData
{
    private static Logger logger = LoggerFactory.getLogger(BitsData.class);
    static int MAX_INPUT = 65535;
    private static int length = 16;
    private int[] bitsArray = new int[length];

    public BitsData(int input) {
        for(int i = 0; i < bitsArray.length; i ++)
            bitsArray[i] = 0;
        if(input > MAX_INPUT) {
            System.out.println("Cannot encode " + input + ".Maximum input is " + MAX_INPUT);
            return;
        }
        else {
            // we put the lowest bit on the first index and pad the rest of bitsArray as 0
            // ex 13 ==> 1101 => bitsArray = [1011000000000000]
            for(int j = 0; j < bitsArray.length; j++) {
                bitsArray[j] = (input >> j) & 1;
            }
        }
    }

    public int[] getBitsArray() {
        return bitsArray;
    }

    public int toInt() {
        int ret = 0;
        for(int i = 0; i < length; i++) {
            ret = ret + bitsArray[i] * (1<<i);
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
        return toString(bitsArray);
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
