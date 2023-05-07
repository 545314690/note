package code.sl.ncoder.通配符匹配;

/**
 * 题目描述
 * 请实现支持'?'and'*'.的通配符模式匹配
 * '?' 可以匹配任何单个字符。
 * '*' 可以匹配任何字符序列（包括空序列）。
 * 返回两个字符串是否匹配
 * 函数声明为：
 * bool isMatch(const char *s, const char *p)
 * 下面给出一些样例：
 * isMatch("aa","a") → false
 * isMatch("aa","aa") → true
 * isMatch("aaa","aa") → false
 * isMatch("aa", "*") → true
 * isMatch("aa", "a*") → true
 * isMatch("ab", "?*") → true
 * isMatch("aab", "d*a*b") → false
 * 示例1
 * 输入
 * 复制
 * "ab","?*"
 * 返回值
 * 复制
 * true
 * @Date 2021/1/20 13:05
 */


public class Solution {
    public boolean isMatch(String s, String p) {
        int len1 = p.length(), len2 = s.length();
        //dp[i][j] 表示 p 的前 i 个字符和 s 的前 j 个字符是否匹配
        boolean[][] dp = new boolean[len1 + 1] [len2 + 1];
        dp[0][0] = true;
//            dp[0][0] = true 表示空串是匹配的。
//            处理一下匹配串 p 以若干个星号开头的情况。因为星号是可以匹配空串的：
        for (int i = 1; i <= len1; i++) {
            if (p.charAt(i - 1) != '*') {
                break;
            }
            dp[i][0] = true;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (p.charAt(i - 1) == s.charAt(j - 1) || p.charAt(i - 1) == '?') {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (p.charAt(i - 1) == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                }
            }
        }
        return dp[len1][len2];
    }

    public static void main(String[] args) {
        String str1 = "";
        String str2 = "*";
        boolean match = new Solution().isMatch(str1, str2);
        System.out.println(match);
    }
}