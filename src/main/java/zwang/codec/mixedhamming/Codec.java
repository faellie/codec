package zwang.codec.mixedhamming;


import java.util.Random;

public class Codec
{
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
        if (args.length < 2) {
            System.out.println("Usage : decode/encode bitString/number [dups]");
            return;
        }
        //todo validation
        String lOperation = args[0];
        int dups = 1;
        if (args.length == 3)
            dups = Integer.parseInt(args[2]);
        if(lOperation.equalsIgnoreCase("decode")) {
            int lCodelength = args[1].length();
            int [] lCodeword = BitsData.bitsArrayFromStr(args[1]);

            System.out.println(decode(lCodeword, dups));
        } else if (lOperation.equalsIgnoreCase("encode")){
            int lData = Integer.parseInt(args[1]);
            System.out.println(BitsData.toString(encode(lData, dups)));
        } else if (lOperation.equalsIgnoreCase("test")){
            if(args.length == 2 && args[1].equals("sanity")) {
                UnitTest.runUnitTests();;
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

    private static void test(int aInErrorRate, int aInLoop, boolean log) {
        int success = 0;
        int detectedError = 0;
        int undetectedError = 0;
        for(int i = 0; i < aInLoop; i ++) {
            int ret = testWholeCodeWithErrorFix(i%65535, aInErrorRate*100, log);
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
