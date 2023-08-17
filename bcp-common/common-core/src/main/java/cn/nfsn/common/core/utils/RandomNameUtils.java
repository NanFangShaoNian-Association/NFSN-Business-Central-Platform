package cn.nfsn.common.core.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class RandomNameUtils {

    public static String getRandomChineseCharacters() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            String str = null;
            int hightPos, lowPos;
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret.append(str); // Append to StringBuilder
        }
        //todo 数据库查询名字是否存在
        return ret.toString(); // Convert StringBuilder to String
    }

    public static String getRandomCharacters() {
        StringBuilder val = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";

            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (random.nextInt(26) + temp));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }
}
