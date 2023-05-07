package code.sl.ncoder;

/**
 * 题目描述
 * 给定两个字符串str1和str2,输出两个字符串的最长公共子串，如果最长公共子串为空，输出-1。
 * 示例1
 * 输入
 * 复制
 * "1AB2345CD","12345EF"
 * 返回值
 * 复制
 * "2345"
 * @Date 2021/1/20 13:05
 */


public class Solution {
    /**
     * longest common substring
     * @param str1 string字符串 the string
     * @param str2 string字符串 the string
     * @return string字符串
     */
    public String LCS (String str1, String str2) {
        // write code here
        int L1 = str1.length();
        int L2 = str2.length();
        int[][] dp = new int[L1+1][L2+1];
        int maxLen = 0;
        int maxLenEnd = 0;
        for (int i = 0; i < L1; i++) {
            for (int j =0; j < L2; j++) {
                if(str1.charAt(i) == str2.charAt(j)){
                    if(i==0||j==0){
                        dp[i][j] = 1;
                    }else {
                        dp[i][j] = dp[i-1][j-1]+1;
                    }
                }else {
                    dp[i][j] = 0;
                }
                if(maxLen<dp[i][j]){
                    maxLen = dp[i][j];
                    maxLenEnd = i;
                }
            }
        }
        if(maxLen==0){
            return "-1";
        }else {
            return str1.substring(maxLenEnd-maxLen+1,maxLenEnd+1);
        }
    }

    public static void main(String[] args) {
        String str1 = "A012345CD";
        String str2 = "012345EF";
        String lcs = new Solution().LCS(str1, str2);
        System.out.println(lcs);
    }
}