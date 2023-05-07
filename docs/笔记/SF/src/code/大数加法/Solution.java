package code.大数加法;

import java.math.BigInteger;
import java.util.*;
public class Solution {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     * 计算两个数之和
     * @param s string字符串 表示第一个整数
     * @param t string字符串 表示第二个整数
     * @return string字符串
     */
//     public String solve (String s, String t) {
//         // write code here
//         char [] charS = s.toCharArray();
//         char [] charT = t.toCharArray();
//         int lengthS = s.length();
//         int lengthT = t.length();
// //         int minLength = 
//     }
    
    public String solve (String s, String t) {
        BigInteger bigInteger1 = new BigInteger(s);
        BigInteger bigInteger2 = new BigInteger(t);
 
        return bigInteger1.add(bigInteger2).toString();
    }
}