package io.weichao.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NetUtil {
    private NetUtil() {
    }

    public static int streamRead(InputStream is, byte[] buffer, int offset, int size) {
        if (is == null || buffer == null || offset < 0 || size < 0) {
            return -2;
        }

        int num = 0;
        int counter = 0;

        while (counter < size) {
            int remain = size - counter;

            try {
                num = is.read(buffer, offset + counter, remain);
                if (num == -1) {
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return counter;
            }

            counter += num;
        }

        return counter;
    }

    public static int streamCopy(InputStream is, OutputStream os, int size) {
        if (is == null || os == null || size < 0) {
            return -2;
        }

        byte buffer[] = new byte[1024];
        int num = 0;
        int counter = 0;

        while (counter < size) {
            int remain = size - counter;
            int readLen = (remain < buffer.length) ? remain : buffer.length;

            try {
                num = is.read(buffer, 0, readLen);
                if (num == -1) {
                    return -1;
                }
                os.write(buffer, 0, num);
            } catch (IOException e) {
                e.printStackTrace();
                return counter;
            }

            counter += num;
        }

        return counter;
    }

    public static boolean streamCopy(InputStream is, OutputStream os) {
        if (is == null || os == null) {
            return false;
        }

        byte buffer[] = new byte[1024];
        int num = 0;

        try {
            while ((num = is.read(buffer)) != -1) {
                os.write(buffer, 0, num);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
