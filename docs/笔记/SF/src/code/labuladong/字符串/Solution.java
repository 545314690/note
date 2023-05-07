package code.labuladong.字符串;

import java.util.*;


public class Solution {

    public static void main(String[] args) {
        new Solution().solve("2001:0db8:85a3:0:0:8A2E:0370:7334:");
    }
    /**
     * 验证IP地址
     * @param IP string字符串 一个IP地址字符串
     * @return string字符串
     */
    public String solve (String IP) {
        // write code here
        if(IP == null || IP == ""){
            return "Neither";
        }
        String [] split = IP.split("\\.");
        String [] split2 = IP.split(":");
        if(split.length == 4){
            for(String str : split){
                if(str.equals("") || str.startsWith("0")){
                    return "Neither";
                }
                for(int i=0;i<str.length();i++){
                    if(str.charAt(i) < '0' || str.charAt(i) > '9'){
                        return "Neither";
                    }
                }
                if(Integer.parseInt(str) > 255){
                    return "Neither";
                }
            }
            return "IPv4";
        }
        if(split2.length == 8){
            for(String str : split2){
                if(str.length() !=4 && !"0".equals(str)){
                    return "Neither";
                }
                for(int i=0;i<str.length();i++){
                    char c = str.charAt(i);
                    boolean expr = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') ;
                    if(!expr){
                        return "Neither";
                    }
                }


            }
            return "IPv6";
        }
        return "Neither";
    }
}