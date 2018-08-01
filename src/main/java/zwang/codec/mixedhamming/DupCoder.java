package zwang.codec.mixedhamming;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DupCoder
{

    private static Logger logger = LoggerFactory.getLogger(DupCoder.class);

    private int dups ;
    private int dataLength;
    private int[] codeArray;


    public DupCoder(int aInDups, HammingCoder aInHammingCode) {
        dups = aInDups;
        int [] lHammingArray = aInHammingCode.getHammingArray();
        dataLength = lHammingArray.length;
        codeArray = new int[aInDups * dataLength];
        for(int i = 0; i < dataLength; i ++) {
            for(int j = 0; j < dups; j ++) {
                codeArray[i * dups + j] = lHammingArray[i];
            }
        }
    }


    public DupCoder(int aInDups, int [] aInCodeArray ) {
        dups = aInDups;
        dataLength = aInCodeArray.length/dups;
        codeArray = new int[dataLength * dups];
        for(int i = 0; i < dataLength * dups; i ++) {
                codeArray[i] = aInCodeArray[i];
        }
    }



    public int[] decodeDataArray() {
        int[] lDecodedData = new int[dataLength];
        for (int i = 0; i < dataLength; i++) {
            lDecodedData[i] = 0;
            for (int j = 0; j < dups; j++) {
                if(codeArray[i * dups + j] == 1) {
                    lDecodedData[i] = 1;
                    break;
                }
            }

        }
        return lDecodedData;
    }

    public void test1BitError(int i) {
        flip(i);
    }

    public void flip(int index) {
        codeArray[index] = (codeArray[index] + 1) % 2;
    }

    public int getDups() {
        return dups;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int[] getCodeArray() {
        return codeArray;
    }
}
