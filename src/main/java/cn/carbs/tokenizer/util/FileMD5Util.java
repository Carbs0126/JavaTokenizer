package cn.carbs.tokenizer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileMD5Util {

    /**
     * 计算文件的MD5值
     *
     * @param file 要计算MD5的文件对象
     * @return 文件的MD5值，若发生错误则返回null
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        MessageDigest md5 = null;
        FileInputStream fis = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);

            byte[] buffer = new byte[8192];
            int length;

            // 读取文件内容并更新MD5摘要
            while ((length = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }

            // 将MD5摘要转换为十六进制字符串
            byte[] bytes = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭文件输入流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}