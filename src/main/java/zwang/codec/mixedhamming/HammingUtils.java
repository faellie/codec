package zwang.codec.mixedhamming;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HammingUtils
{

    private static Logger logger = LoggerFactory.getLogger(HammingUtils.class);

    public static int DATA_LENGTH = 16;
    public static int CODE_WORD_LENGTH = 22;
    public static int PARITY_LENGTH = 6;
    public static int MAX_DATA = 65535;


    /*P0 = D0 + D1 + D3 + D4 + D6 + D8 + D10 + D11 + D13 + D15
        P1 = D0 + D2 + D3 + D5 + D6 + D9 + D10 + D12 + D13
    P2 = D1 + D2 + D3 + D7 + D8 + D9 + D10 + D14 + D15
        P3 = D4 + D5 + D6 + D7 + D8 + D9 + D10
    P4 = D11 + D12 + D13 + D14 + D15
        P5 = D0 + D1 + D2 + D3 + D4 + D5 + D6 + D7 + D8 + D9 + D10 + D11 + D12 + D13 + D14 + D15 + P0 + P1 + P2 + P3 + P4*/
    /*private static int[][] MATRIX =  {
          // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0
            {1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}

    };*/
    //matrix obtained from http://www.ecs.umass.edu/ece/koren/FaultTolerantSystems/simulator/Hamming/HammingCodes.html
   private static int[][] MATRIX =  {
          // 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1}

    };

    /*private static int[][] MATRIX =  {
           //0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1
            {1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0},
            {1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };*/


    private static final Map<String,Integer> errorCorrectionMap = new HashMap<String, Integer>()
    {
        {
            put("110101", 0);
            put("110100", 1);
            put("110011", 2);
            put("110010", 3);
            put("110001", 4);
            put("101111", 5);
            put("101110", 6);
            put("101101", 7);
            put("101100", 8);
            put("101011", 9);
            put("101010", 10);
            put("101001", 11);
            put("100111", 12);
            put("100110", 13);
            put("100101", 14);
            put("100011", 15);
            put("100000", 16);
            put("110000", 17);
            put("101000", 18);
            put("100100", 19);
            put("100010", 20);
            put("100001", 21);
        }
    };

    public static void encode(BitsData aInBits, int[] aInHammingArray) {
        int[] lBitsArray = aInBits.getBitsArray();
        for(int i = 0; i < DATA_LENGTH; i++) {
            aInHammingArray[i] = lBitsArray[i];
        }

        for(int j = DATA_LENGTH + 1 ; j < CODE_WORD_LENGTH; j++) {
            int index = j - DATA_LENGTH;
            aInHammingArray[j] = arrayTime(lBitsArray, MATRIX[index], DATA_LENGTH);
        }

        int bit16 = arrayTime(aInHammingArray, MATRIX[0], CODE_WORD_LENGTH);
        aInHammingArray[16] = bit16 %2;
    }



    public static int[] calcParityCode(HammingCoder aInCode) {
        int[] lHammingCode = new int[HammingUtils.CODE_WORD_LENGTH];
        int[] lParityArray = new int[PARITY_LENGTH];
        for(int i = 0; i < PARITY_LENGTH; i++ ) {

            lParityArray[i] = arrayTime(aInCode.getHammingArray(), MATRIX[i], CODE_WORD_LENGTH);
        }
        return lParityArray;
    }


    public static boolean haveError(HammingCoder aInCode) {
       /* int[] lHammingCode = aInCode.getHammingArray();
        int bitsSum = 0;
        for(int i = 0; i < HammingUtils.DATA_LENGTH; i++) {
            bitsSum = bitsSum + lHammingCode[i];
        }
        bitsSum = (bitsSum + lHammingCode[DATA_LENGTH])%2;
        if(bitsSum == 1) {
            return true;
        }
        */
        int[] lParityArray = calcParityCode(aInCode);
        String lParityStr = BitsData.toString(lParityArray);

        return(!(lParityStr.equals(getCorrectParityStr(aInCode))));
    }

    public static void correctError(HammingCoder aInCode) {
        int[] lParityArray = calcParityCode(aInCode);
        String lParityStr = BitsData.toString(lParityArray);
        int index = errorCorrectionMap.get(lParityStr);
        aInCode.flip(index);

    }


    public static boolean isErrorCorrectable(HammingCoder aInCode) {
        int[] lParityArray = calcParityCode(aInCode);
        String lParityStr = BitsData.toString(lParityArray);
        return errorCorrectionMap.containsKey(lParityStr);
    }


    private static int arrayTime(int[] aInArrayA, int[] aInArrayB, int aInLength) {
        int ret = 0;
        for(int i = 0; i < aInLength; i++) {

            ret = (ret + aInArrayA[i] * aInArrayB[i]) % 2;
        }
        return ret;
    }

    public static String getCorrectParityStr(HammingCoder aInCode) {
        return "000000";
    }

    public static int decode(HammingCoder aInCode) {
        if(haveError(aInCode)) {
            if(isErrorCorrectable(aInCode)) {
                correctError(aInCode);
            } else {
                return -1;
            }

        }
        return aInCode.getData();
    }

    public static Map<String, Integer> getErrorCorrectionMap() {
        return Collections.unmodifiableMap(errorCorrectionMap);
    }
}
