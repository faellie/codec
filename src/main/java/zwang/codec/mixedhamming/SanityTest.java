package zwang.codec.mixedhamming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SanityTest
{
    private static Logger logger = LoggerFactory.getLogger(SanityTest.class);

    public static void main(String[] args) {

        runUnitTests();
    }

    public static void runUnitTests(){
        //test encode/decode without error
        System.out.println("test encode/decode without error....");
        for (int i = 0; i < 65535; i++) {
            if (!testNoErrorCodeDecode(i)) {
                System.out.println("testNoErrorCodeDecode failed for " + i);
                return;
            }
        }

        //test one bit error (111 ==> 000/000==>111)
        System.out.println("test one bit error....");
        for (int i = 0; i < 65535; i++) {
            for (int j = 0; j < HammingUtils.CODE_WORD_LENGTH; j++) {
                if (!testOneErrorCodeDecode(i, j)) {
                    System.out.println("testOneErrorCodeDecode failed for " + i + " " + j);
                    return;
                }
            }
        }

        //test two bit error (111 ==> 000/000==>111)
        System.out.println("test two bit error....");
        for (int i = 0; i < 65535; i++) {
            for (int j = 0; j < HammingUtils.CODE_WORD_LENGTH; j++) {
                for (int k = 0; k < HammingUtils.CODE_WORD_LENGTH; k++) {
                    if (!testTwoErrorCodeDecode(i, j, k)) {
                        System.out.println("testTwoErrorCodeDecode failed for " + i + " " + j);
                        return;
                    }
                }
            }
        }
        System.out.println("test Error Bit Mapping....");
        for (int i = 0; i < 65535; i++) {
            if (!testErrorBitMapping(i)) {
                System.out.println("testErrorBitMapping failed for " + i);
                return;
            }
        }
        //printErrorBitsMapping(222);
    }

    private static void printErrorBitsMapping(int aInInt) {
        BitsData lTestBits = new BitsData(aInInt);
        HammingCoder lHammingCoder = new HammingCoder(lTestBits);

        //now put an error
        for(int i = 0; i < HammingUtils.CODE_WORD_LENGTH; i ++) {
            lHammingCoder = new HammingCoder(lTestBits);
            lHammingCoder.test1BitError(i);
            int [] checkingBits = HammingUtils.calcParityCode(lHammingCoder);

            //System.out.println("Checking bits for bit error at " + i + "==>  " + Bits.toString(checkingBits));
            //System.out.println("\"" + BitsData.toString(checkingBits).substring(1) + "\", " +  i + "," );
            System.out.println("\"" + BitsData.toString(checkingBits) + "\", " +  i + "," );
        }

    }



    private static boolean testNoErrorCodeDecode(int aInput) {
        int[] lDupCode = Codec.encode(aInput, 3);
        int aDupOutput = Codec.decode(lDupCode, 3);
        int[] lCode = Codec.encode(aInput, 1);
        int aOutput = Codec.decode(lCode, 1);
        return(aInput == aDupOutput && aInput == aOutput );
    }

    private static boolean testOneErrorCodeDecode(int aInput, int aInErrorIndex) {
        //with dup
        int[] lDupCode = Codec.encode(aInput, 3);
        //000 ==> 111 should not happened, but this is just to test the code/decode correctness
        lDupCode[aInErrorIndex * 3] = (lDupCode[aInErrorIndex * 3] + 1) % 2;
        lDupCode[aInErrorIndex * 3 + 1] = (lDupCode[aInErrorIndex * 3 + 1] + 1) % 2;
        lDupCode[aInErrorIndex * 3 + 2] = (lDupCode[aInErrorIndex * 3 + 2] + 1) % 2;
        int aDupOutput = Codec.decode(lDupCode, 3);

        //test without dup
        int[] lCode = Codec.encode(aInput, 1);
        lCode[aInErrorIndex] = (lCode[aInErrorIndex] + 1) % 2;
        int aOutput = Codec.decode(lCode, 1);
        return(aInput == aDupOutput && aInput == aOutput);
    }

    private static boolean testTwoErrorCodeDecode(int aInput, int aInErrorIndex1, int aInErrorIndex2) {
        //skip aInErrorIndex1 == aInErrorIndex2
        if(aInErrorIndex1 == aInErrorIndex2) {
            return true;
        }
        //test with dup
        int[] lDupCode = Codec.encode(aInput, 3);
        //000 ==> 111 should not happened, but this is just to test the code/decode correctness
        lDupCode[aInErrorIndex1 * 3] = (lDupCode[aInErrorIndex1 * 3] + 1) % 2;
        lDupCode[aInErrorIndex1 * 3 + 1] = (lDupCode[aInErrorIndex1 * 3 + 1] + 1) % 2;
        lDupCode[aInErrorIndex1 * 3 + 2] = (lDupCode[aInErrorIndex1 * 3 + 2] + 1) % 2;

        lDupCode[aInErrorIndex2 * 3] = (lDupCode[aInErrorIndex2 * 3] + 1) % 2;
        lDupCode[aInErrorIndex2 * 3 + 1] = (lDupCode[aInErrorIndex2 * 3 + 1] + 1) % 2;
        lDupCode[aInErrorIndex2 * 3 + 2] = (lDupCode[aInErrorIndex2 * 3 + 2] + 1) % 2;
        int aDupOutput = Codec.decode(lDupCode, 3);

        //test without dup
        int[] lCode = Codec.encode(aInput, 1);
        lCode[aInErrorIndex1] = (lCode[aInErrorIndex1] + 1) % 2;
        lCode[aInErrorIndex2] = (lCode[aInErrorIndex2] + 1) % 2;
        int aOutput = Codec.decode(lCode, 1);

        return(aDupOutput == -1 && aOutput == -1);
    }

    private static boolean testErrorBitMapping(int aInInt) {
        BitsData lTestBits = new BitsData(aInInt);
        HammingCoder lHammingCoder = new HammingCoder(lTestBits);

        //now put an error
        for(int i = 0; i < HammingUtils.CODE_WORD_LENGTH; i ++) {
            lHammingCoder = new HammingCoder(lTestBits);
            lHammingCoder.test1BitError(i);
            int [] checkingBits = HammingUtils.calcParityCode(lHammingCoder);
            //System.out.println("\"" + BitsData.toString(checkingBits) + "\", " +  i + "," );
            String errorBitString = BitsData.toString(checkingBits);

            Map<String, Integer> errorBitMap = HammingUtils.getErrorCorrectionMap();
            if(!HammingUtils.getErrorCorrectionMap().containsKey(errorBitString))  {
                System.out.println("Missing errorbit str : " + errorBitString);
                return false;
            } else  {
                int index = errorBitMap.get(errorBitString);
                if(index != i) {
                    System.out.println("errorbit str : " + errorBitString + " point to wrong index : " + index + " should be " + i);
                    return false;
                }
            }

        }
        return true;
    }
}
