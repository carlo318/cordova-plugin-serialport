package tp.xmaihh.serialport.stick;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import tp.xmaihh.serialport.utils.ByteUtil;

public class ParserDelimiterStickPackageHelper implements AbsStickPackageHelper {

    private String delimiter;

    public ParserDelimiterStickPackageHelper(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public byte[] execute(InputStream is) {
        int len = -1;
        byte temp;
        byte[] tempBytes = new byte[128];
        int count = 0;

        try {
            while ((len = is.read()) != -1) {
                temp = (byte) len;
                tempBytes[count] = temp;
                count++;
                if (ByteUtil.ByteArrToHex(tempBytes).contains(delimiter)) {
                    break;
                }
            }
            if (len == -1) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Arrays.copyOf(tempBytes, count);
    }
}
