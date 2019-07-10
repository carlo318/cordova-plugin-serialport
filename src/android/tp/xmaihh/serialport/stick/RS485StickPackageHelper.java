package tp.xmaihh.serialport.stick;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RS485StickPackageHelper implements AbsStickPackageHelper {

    private List<Byte> mBytes = new ArrayList<>();

    @Override
    public byte[] execute(InputStream is) {
        mBytes.clear();
        int count = 0;
        int len = -1;
        byte temp;
        byte[] result;
        int msgLen = -1;
        try {
            while ((len = is.read()) != -1) {
                temp = (byte) len;
                count++;
                mBytes.add(temp);
                if (count == 3) {
                    msgLen = (5 + temp) * 2;
                }
                if (msgLen != -1 && msgLen == count) {
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
        result = new byte[mBytes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = mBytes.get(i);
        }
        return result;
    }
}
