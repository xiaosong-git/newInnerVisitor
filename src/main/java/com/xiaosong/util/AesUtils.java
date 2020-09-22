package com.xiaosong.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {
    public AesUtils() {
    }

    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        byte[] var2 = buf;
        int var3 = buf.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        int temp = 2;
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / temp];

            for(int i = 0; i < hexStr.length() / temp; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }

            return result;
        }
    }

    private static byte[] getAESEncrypt(byte[] content, byte[] aes) {
        if (content != null && aes != null) {
            try {
                int w = content.length % 16;
                byte[] contentTemp;
                if (w != 0) {
                    contentTemp = new byte[content.length + 16 - w];
                    System.arraycopy(content, 0, contentTemp, 0, content.length);
                } else {
                    contentTemp = content;
                }

                SecretKeySpec sks = new SecretKeySpec(aes, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(1, sks);
                return cipher.doFinal(contentTemp);
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        } else {
            System.out.println("密钥或内容为空...");
            return null;
        }
    }

    private static byte[] getAESDecrypt(byte[] content, byte[] aes) {
        if (content != null && content.length != 0 && aes != null) {
            try {
                SecretKeySpec sks = new SecretKeySpec(aes, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(2, sks);
                return cipher.doFinal(content);
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        } else {
            System.out.println("密钥或内容为空...");
            return null;
        }
    }

    public static String getAESEncrypt(String content, String aes) {
        return parseByte2HexStr((byte[])Objects.requireNonNull(getAESEncrypt(content.getBytes(StandardCharsets.UTF_8), aes.getBytes())));
    }

    public static String getAESDecrypt(String content, String aes) {
        return (new String((byte[])Objects.requireNonNull(getAESDecrypt(parseHexStr2Byte(content), aes.getBytes())), StandardCharsets.UTF_8)).replaceAll("\u0000", "");
    }
  
}