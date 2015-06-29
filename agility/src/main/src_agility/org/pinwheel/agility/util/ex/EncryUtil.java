package org.pinwheel.agility.util.ex;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 加密方式工具类
 */
public class EncryUtil {
    /**
     * 加密Map集合中的数据，先将参数中Map集合改成TreeMap在拼成key=value形式后加密
     *
     * @param map
     * @return
     */
    public static Map<String, String> getSignStr(Map<String, String> map) {
        if (map != null) {
            StringBuffer sb = new StringBuffer();
            TreeMap<String, String> treeMap = new TreeMap<String, String>();
            Set<String> map_set = map.keySet();
            Iterator<String> iterator = map_set.iterator();
            while (iterator.hasNext()) {
                String keyString = (String) iterator.next();
                String valueString = (String) map.get(keyString);
                treeMap.put(keyString, valueString);
            }
            Set<Map.Entry<String, String>> tree_set = treeMap.entrySet();
            Iterator<Map.Entry<String, String>> it = tree_set.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                String k = (String) entry.getKey();
                String v = (String) entry.getValue();
                sb.append(k + "=" + v + "&");
            }
            try {
                String signString = MD5(sb.toString(), "utf-8");
                map.put("sign", signString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        } else {
            return null;
        }

    }


    /**
     * MD5加密
     *
     * @param message 要进行MD5加密的字符串
     * @return 加密结果为32位字符串
     */
    public static String MD5(String message) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(message.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i])
                        .length() == 1)
                    md5StrBuff.append("0")
                            .append(Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer
                            .toHexString(0xFF & byteArray[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     * MD5加密
     *
     * @param message     要加密的内容
     * @param charsetName 编码通畅直接输入("utf-8","gbk")
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException   不支持编码异常
     */
    public static String MD5(String message, String charsetName) {
        String resultString = null;
        resultString = new String(message);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (TextUtils.isEmpty(charsetName)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultString;
    }

    /**
     * 支持md5加密的方法
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexStringMd5(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 支持md5加密的方法
     */
    private static String byteToHexStringMd5(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigitsMd5[d1] + hexDigitsMd5[d2];
    }

    /**
     * 支持md5加密的数组
     */
    private static final String hexDigitsMd5[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**************************************************** AES加密 **************************************************/
    /**
     * aes加密的内容
     */
    private static final String AESTYPE = "AES/ECB/PKCS5Padding";// AES/ECB/PKCS5Padding

    /**
     * AES加密
     *
     * @param keyStr    密钥(16位长度或32位)
     * @param plainText 要加密的内容
     * @return 返回加密成功的string
     */
    public static String AES(String keyStr, String plainText) {
        byte[] encrypt = null;
        try {
            Key key = generateKeyAES(keyStr);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypt = cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(encrypt));
    }

    /**
     * AES解密
     *
     * @param keyStr      密钥
     * @param encryptData 需要解密的内容
     * @return 返回解密成功的string
     */
    public static String AESdecrypt(String keyStr, String encryptData) {
        if (encryptData != null) {
            byte[] decrypt = null;
            try {
                Key key = generateKeyAES(keyStr);
                Cipher cipher = Cipher.getInstance(AESTYPE);
                cipher.init(Cipher.DECRYPT_MODE, key);
                decrypt = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encryptData.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new String(decrypt).trim();
        } else {
            return null;
        }
    }

    /**
     * AES加密获取key
     *
     * @param key 密钥
     * @return
     * @throws Exception
     */
    private static Key generateKeyAES(String key) throws Exception {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    key.getBytes(), "AES");
            return keySpec;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * ********************************************************************* SHA1值加密 **************************************
     */
    public static String sha1(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ******************************************************** RSA私钥加密 ******************************************
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static String RSA(String content, String privateKey) {
        String charset = "utf-8";
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final class Base64 {
        static private final int BASELENGTH = 128;
        static private final int LOOKUPLENGTH = 64;
        static private final int TWENTYFOURBITGROUP = 24;
        static private final int EIGHTBIT = 8;
        static private final int SIXTEENBIT = 16;
        static private final int FOURBYTE = 4;
        static private final int SIGN = -128;
        static private final char PAD = '=';
        static private final boolean fDebug = false;
        static final private byte[] base64Alphabet = new byte[BASELENGTH];
        static final private char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

        static {
            for (int i = 0; i < BASELENGTH; ++i) {
                base64Alphabet[i] = -1;
            }
            for (int i = 'Z'; i >= 'A'; i--) {
                base64Alphabet[i] = (byte) (i - 'A');
            }
            for (int i = 'z'; i >= 'a'; i--) {
                base64Alphabet[i] = (byte) (i - 'a' + 26);
            }
            for (int i = '9'; i >= '0'; i--) {
                base64Alphabet[i] = (byte) (i - '0' + 52);
            }
            base64Alphabet['+'] = 62;
            base64Alphabet['/'] = 63;
            for (int i = 0; i <= 25; i++) {
                lookUpBase64Alphabet[i] = (char) ('A' + i);
            }
            for (int i = 26, j = 0; i <= 51; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('a' + j);
            }
            for (int i = 52, j = 0; i <= 61; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('0' + j);
            }
            lookUpBase64Alphabet[62] = (char) '+';
            lookUpBase64Alphabet[63] = (char) '/';
        }

        private static boolean isWhiteSpace(char octect) {
            return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
        }

        private static boolean isPad(char octect) {
            return (octect == PAD);
        }

        private static boolean isData(char octect) {
            return (octect < BASELENGTH && base64Alphabet[octect] != -1);
        }

        public static String encode(byte[] binaryData) {
            if (binaryData == null) {
                return null;
            }
            int lengthDataBits = binaryData.length * EIGHTBIT;
            if (lengthDataBits == 0) {
                return "";
            }
            int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
            int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
            int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets;
            char encodedData[] = null;
            encodedData = new char[numberQuartet * 4];
            byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;
            int encodedIndex = 0;
            int dataIndex = 0;
            if (fDebug) {
                System.out.println("number of triplets = " + numberTriplets);
            }
            for (int i = 0; i < numberTriplets; i++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];
                if (fDebug) {
                    System.out.println("b1= " + b1 + ", b2= " + b2 + ", b3= " + b3);
                }
                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);
                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
                byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);
                if (fDebug) {
                    System.out.println("val2 = " + val2);
                    System.out.println("k4   = " + (k << 4));
                    System.out.println("vak  = " + (val2 | (k << 4)));
                }
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
            }
            // form integral number of 6-bit groups
            if (fewerThan24bits == EIGHTBIT) {
                b1 = binaryData[dataIndex];
                k = (byte) (b1 & 0x03);
                if (fDebug) {
                    System.out.println("b1=" + b1);
                    System.out.println("b1<<2 = " + (b1 >> 2));
                }
                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4];
                encodedData[encodedIndex++] = PAD;
                encodedData[encodedIndex++] = PAD;
            } else if (fewerThan24bits == SIXTEENBIT) {
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);
                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2];
                encodedData[encodedIndex++] = PAD;
            }
            return new String(encodedData);
        }

        public static byte[] decode(String encoded) {
            if (encoded == null) {
                return null;
            }
            char[] base64Data = encoded.toCharArray();
            // remove white spaces
            int len = removeWhiteSpace(base64Data);
            if (len % FOURBYTE != 0) {
                return null;// should be divisible by four
            }
            int numberQuadruple = (len / FOURBYTE);
            if (numberQuadruple == 0) {
                return new byte[0];
            }
            byte decodedData[] = null;
            byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
            char d1 = 0, d2 = 0, d3 = 0, d4 = 0;
            int i = 0;
            int encodedIndex = 0;
            int dataIndex = 0;
            decodedData = new byte[(numberQuadruple) * 3];
            for (; i < numberQuadruple - 1; i++) {
                if (!isData((d1 = base64Data[dataIndex++]))
                        || !isData((d2 = base64Data[dataIndex++]))
                        || !isData((d3 = base64Data[dataIndex++]))
                        || !isData((d4 = base64Data[dataIndex++]))) {
                    return null;
                }// if found "no data" just return null
                b1 = base64Alphabet[d1];
                b2 = base64Alphabet[d2];
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }
            if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))) {
                return null;// if found "no data" just return null
            }
            b1 = base64Alphabet[d1];
            b2 = base64Alphabet[d2];
            d3 = base64Data[dataIndex++];
            d4 = base64Data[dataIndex++];
            if (!isData((d3)) || !isData((d4))) {// Check if they are PAD
                // characters
                if (isPad(d3) && isPad(d4)) {
                    if ((b2 & 0xf) != 0)// last 4 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 1];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                    return tmp;
                } else if (!isPad(d3) && isPad(d4)) {
                    b3 = base64Alphabet[d3];
                    if ((b3 & 0x3) != 0)// last 2 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 2];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                    tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                    return tmp;
                } else {
                    return null;
                }
            } else { // No PAD e.g 3cQl
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }
            return decodedData;
        }

        private static int removeWhiteSpace(char[] data) {
            if (data == null) {
                return 0;
            }
            // count characters that's not whitespace
            int newSize = 0;
            int len = data.length;
            for (int i = 0; i < len; i++) {
                if (!isWhiteSpace(data[i])) {
                    data[newSize++] = data[i];
                }
            }
            return newSize;
        }
    }
}
