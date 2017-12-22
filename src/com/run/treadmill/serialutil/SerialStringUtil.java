package com.run.treadmill.serialutil;

public class SerialStringUtil {

	 /**
     * 数组转成十六进制字符串
     * @param byte[]
     * @return HexString
     */
    public static String byteArrayToHexString(byte[] b, int length){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; ++i){
            buffer.append("0x" + toHexString(b[i]) + " ");
        }
        return buffer.toString();
    }
    public static String toHexString(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

	
	
	

	/**
	 *  字符串转换为ASCII码   
	 * @param str
	 * @return
	 */
    public static StringBuffer toASCII(String str) {
    	StringBuffer newStr = new StringBuffer();
        char[] chars = str.toCharArray(); // 把字符中转换为字符数组   
        for (int i = 0; i < chars.length; i++) {// 输出结果   
            newStr.append(chars[i]);
            //AssurLog.i(TAG, "chars[i]: " + chars[i]);
        } 
        return newStr;
    }   
    
    
    /**
     * 按照字节数截取字符串
     * @param targetString
     * @param byteIndex
     * @return
     * @throws Exception
     */
    public static String getSubString(String targetString, int byteLen) throws Exception {
        if (targetString.getBytes("UTF-8").length < byteLen) {
            throw new Exception("超过长度");
        }
        String temp = targetString;
        for (int i = 0; i < targetString.length(); i++) {
            if (temp.getBytes("UTF-8").length <= byteLen) {
                break;
            }
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }
    
	
}
