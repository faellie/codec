package zwang.codec.mixedhamming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HammingCoder
{
    private static Logger logger = LoggerFactory.getLogger(HammingCoder.class);

    private static int tripleLenth = 66;
    private int[] hammingArray = new int[HammingUtils.CODE_WORD_LENGTH];
    //for now its always 22/6
    private int length = HammingUtils.CODE_WORD_LENGTH;

    /*private int[] dataArray = new int[HammingEncode.DATA_LENGTH];
    private int[] parityArray = new int[HammingEncode.PARITY_LENGTH];
*/
    public HammingCoder(BitsData aInBits) {
        HammingUtils.encode(aInBits, hammingArray);
        /*for(int i = 0; i < HammingEncode.CODE_WORD_LENGTH; i ++) {
            if(i < HammingEncode.DATA_LENGTH) {
                dataArray[i] = hammingArray[i];
            } else{
                parityArray[i-HammingEncode.DATA_LENGTH] = hammingArray[i];
            }
        }*/

    }


    public HammingCoder(int[]  aInHammingArray) {
        for(int i = 0; i < HammingUtils.CODE_WORD_LENGTH; i ++) {
            hammingArray[i] = aInHammingArray[i];
        }
    }


    public int[] getDataArray() {
        int[] lDataArray = new int[HammingUtils.DATA_LENGTH];
        for(int i = 0; i < HammingUtils.DATA_LENGTH; i ++) {
            lDataArray[i] = hammingArray[i];
        }
        return lDataArray;
    }


    public int[] getParityrray() {
        int[] lParityArray = new int[HammingUtils.PARITY_LENGTH];
        for(int i = HammingUtils.DATA_LENGTH; i < HammingUtils.CODE_WORD_LENGTH; i ++) {
            lParityArray[i- HammingUtils.DATA_LENGTH] = hammingArray[i];
        }
        return lParityArray;
    }
    public String toHammingString() {
        return BitsData.toString(getDataArray()) + "---" + BitsData.toString(getParityrray());
    }

    public int[] getHammingArray() {
        return hammingArray;
    }

    public void test1BitError(int i) {
        flip(i);
    }

    public void flip(int index) {
        hammingArray[index] = (hammingArray[index] + 1) % 2;
    }

    public int getData() {
        int ret = 0;
        for(int i = 0; i < HammingUtils.DATA_LENGTH; i ++) {
           ret = ret + (hammingArray[i] << i) ;
        }

        return ret;
    }

    public int getLength() {
        return length;
    }




}
