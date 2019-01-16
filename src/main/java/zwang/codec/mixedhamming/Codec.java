package zwang.codec.mixedhamming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Codec
{

    private static Logger logger = LoggerFactory.getLogger(Codec.class);
    public static int decode(int [] aInCode, int aInDups){
        //todo validations
        int[] lHammingWord;
        if(aInDups > 1 ) {
            DupCoder lDupCoder = new DupCoder(aInDups, aInCode);
            lHammingWord = lDupCoder.decodeDataArray();
        } else {
            lHammingWord = aInCode;
        }
        HammingCoder lHmamingCoder = new HammingCoder(lHammingWord);
        return HammingUtils.decode(lHmamingCoder);
    }


    public static int[] encode(int aInData, int aInDups) {
        //validations
        BitsData lBits = new BitsData(aInData);
        HammingCoder lHmamingCoder = new HammingCoder(lBits);
        if(aInDups > 1) {
            DupCoder lDupCoder = new DupCoder(aInDups, lHmamingCoder);
            return lDupCoder.getCodeArray();
        } else {
            return lHmamingCoder.getHammingArray();
        }
    }

    public static void main(String[] args) {
        logger.info("test logger");
        if (args.length < 2) {
            System.out.println("Usage : decode/encode bitString/number [dups]");
            return;
        }
        ConfigUtil.init();
        String lOperation = args[0];
        int dups = 1;
        if (args.length == 3)
            dups = Integer.parseInt(args[2]);
        if(lOperation.equalsIgnoreCase("decode")) {
            int [] lCodeword = BitsData.bitsArrayFromStr(args[1]);
            if (dups > 1 && ConfigUtil.getBoolProp("TRY_FIX_MISSING_ZERO", false)) {
                int lEstimatedError = countError(lCodeword, dups);
                if(lEstimatedError >= ConfigUtil.getIntProp("MAX_ERRORS", ConfigUtil.DEF_MAX_ERRORS) && lCodeword[lCodeword.length-1] == 0) {
                    //try to fix it
                    fixMissingHoleError(lCodeword, dups);
                }
                System.out.println(decode(lCodeword, dups) + " " + countError(lCodeword, dups));
            } else {
                System.out.println(decode(lCodeword, dups));
            }

        } else if (lOperation.equalsIgnoreCase("encode")){
            int lData = Integer.parseInt(args[1]);
            System.out.println(BitsData.toString(encode(lData, dups)));
        } else if (lOperation.equalsIgnoreCase("test")){
            if(args.length == 2 && args[1].equals("sanity")) {
                SanityTest.runUnitTests();;
                return;
            }
            if (args.length != 4) {
                System.out.println("Usage : test errorRate loops log/nolog");
                return;
            }
            int errorRate = Integer.parseInt(args[1]);
            int loop = Integer.parseInt(args[2]);
            test(errorRate, loop, args[3].equalsIgnoreCase("log"));
            return;
        } else {
            System.out.println("Usage : decode/encode bitString/number");
            return;
        }
    }

    private static void fixMissingHoleError(int[] aInCodeWord, int aInDups) {
        int lLen = aInCodeWord.length;
        int lCurrentErrors = countError(aInCodeWord, aInDups);
        int[] lFixedCode = new int[aInCodeWord.length];
        boolean lFixed = false;
        if(aInCodeWord[aInCodeWord.length -1] == 0 ) {
            //                                                      * missing an zero  * added at end
            // change 000111000000111000111111000000111000000000000001110001111110000000
            //        000111000000111000111111000000111000000000000000111000111111000000
            for(int lMissingHoleIndex = ConfigUtil.getIntProp("START_POSITION", ConfigUtil.DEF_START_POSITION); lMissingHoleIndex < lLen; lMissingHoleIndex++) {
                //todo for possible missing 0 index, it must be an 1 now and it must be following 2 starting 0 (001)
                //also, the trailing 0 must be 3xK + 1
                //if this should be 0 but becames a 1 because we are missing a zero (lets not worry about missing 2 zeros for now)
                //then we must have a 001 sequence where the 1 is at 3xK -1
                if(lMissingHoleIndex % 3 == 2 && aInCodeWord[lMissingHoleIndex] == 1 && aInCodeWord[lMissingHoleIndex -1] == 0  && aInCodeWord[lMissingHoleIndex -2 ] == 0) {
                    //insert a 0 here and push everything after by 1 bits and remove the last 0
                    int[] lNewCode = new int[aInCodeWord.length];
                    for (int i = 0; i < lLen; i++) {
                        lNewCode[i] = aInCodeWord[i];
                    }
                    swapBackMissingHole(lNewCode, lMissingHoleIndex);

                    int nextErrorNum = countError(lNewCode, aInDups);
                    if (nextErrorNum < lCurrentErrors) {
                        lCurrentErrors = nextErrorNum;
                        for (int i = 0; i < lLen; i++) {
                            lFixedCode[i] = lNewCode[i];
                        }
                        lFixed = true;
                    }
                }
            }
        }
        if(lFixed) {
            logger.info("Fixed Missing zeros from : " + BitsData.toString(aInCodeWord));
            for (int i = 0; i < lLen; i++) {
                aInCodeWord[i] = lFixedCode[i];
            }
            logger.info("Fixed Missing zeros To   : " + BitsData.toString(aInCodeWord));
        }
    }

    private static void swapBackMissingHole(int[] lNewCode, int swapIndex) {
        int len = lNewCode.length;
        for(int i = len-1; i > swapIndex; i--) {
            lNewCode[i] = lNewCode[i-1];
        }
        lNewCode[swapIndex] = 0;
    }

    private static int countError(int[] lCodeword, int dups) {
        int errors = 0;
        if(dups > 1) {
            for(int i = 0; i < lCodeword.length/dups; i++) {
                int lDupTotal = lCodeword[i * 3 ] + lCodeword[i * 3 +1 ] + lCodeword[i * 3 +2];
                if(lDupTotal != 0 && lDupTotal != 3) {
                    errors = errors + (3 - lDupTotal);
                }
            }
        }
        return errors;
    }

    private static void test(int aInErrorRate, int aInLoop, boolean log) {
        int success = 0;
        int detectedError = 0;
        int undetectedError = 0;
        for(int i = 0; i < aInLoop; i ++) {
            int ret = testWholeCodeWithErrorFix(i%ConfigUtil.MAX_INPUT, aInErrorRate*100, log);
            if(ret == -2) {
                undetectedError ++;
            } else  if(ret == -1) {
                detectedError ++;
            } else {
                success ++;
            }
        }
        System.out.println("At " + aInErrorRate + "% error rate, " + aInLoop + " run gives : "
                + success + " success " + detectedError + " detected error "  + undetectedError + " undetected errors" );
    }
    private static int testWholeCodeWithErrorFix(int aInInt, int aInErrorRate, boolean log) {

        int ret = 0;

        int[] lCodeword = encode(aInInt, 3);
        if(log) {
            System.out.println(aInInt + " Without error:  \t\t" + BitsData.toString(lCodeword));
        }
        //now generate random error
        int lNumOfErrors = 0;
        for(int index = 0; index < 66 ; index ++) {
            Random rand = new Random();
            int error = rand.nextInt(10000);
            if(error < aInErrorRate && lCodeword[index] != 0) {
                lNumOfErrors++;
                lCodeword[index] = 0;
            }
        }
        if(log) {
            System.out.println("With " + lNumOfErrors + " errors : \t\t" + BitsData.toString(lCodeword));
        }


        int data = decode(lCodeword, 3);
        if(data != aInInt) {
            if(data == -1 ) {
                ret =  -1;
                if(log) {
                    System.out.println("detected error but unable to recovery data " + data + "  !=  " + aInInt);
                }
            } else {
                ret = -2;
                if(log) {
                    System.out.println("undetected error " + data + "  !=  " + aInInt);
                }
            }

        } else {
            if(log) {
                System.out.println(aInInt + " ===> " + aInInt);
            }
            ret = 1;
        }

        return  ret;
    }
}
