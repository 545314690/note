package code.sl.s44;

/**
 *
 * 44. 通配符匹配
 * 给定一个字符串 (s) 和一个字符模式 (p) ，实现一个支持 '?' 和 '*' 的通配符匹配。
 *
 * '?' 可以匹配任何单个字符。
 * '*' 可以匹配任意字符串（包括空字符串）。
 * 两个字符串完全匹配才算匹配成功。
 *
 * 说明:
 *
 * s 可能为空，且只包含从 a-z 的小写字母。
 * p 可能为空，且只包含从 a-z 的小写字母，以及字符 ? 和 *。
 * 示例 1:
 *
 * 输入:
 * s = "aa"
 * p = "a"
 * 输出: false
 * 解释: "a" 无法匹配 "aa" 整个字符串。
 * 示例 2:
 *
 * 输入:
 * s = "aa"
 * p = "*"
 * 输出: true
 * 解释: '*' 可以匹配任意字符串。
 * 示例 3:
 *
 * 输入:
 * s = "cb"
 * p = "?a"
 * 输出: false
 * 解释: '?' 可以匹配 'c', 但第二个 'a' 无法匹配 'b'。
 * 示例 4:
 *
 * 输入:
 * s = "adceb"
 * p = "*a*b"
 * 输出: true
 * 解释: 第一个 '*' 可以匹配空字符串, 第二个 '*' 可以匹配字符串 "dce".
 * 示例 5:
 *
 * 输入:
 * s = "acdcb"
 * p = "a*c?b"
 * 输出: false
 * @Author lisenmiao
 * @Date 2021/1/12 16:28
 */
class Solution {
    // * '?' 可以匹配任何单个字符。
    // * '*' 可以匹配任意字符串（包括空字符串）。
//     * 输入:
// * s = "adceb"
// * p = "*a*b"
// * 输出: true
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
        String s = "";
        String p = "*";
        boolean match = new Solution().isMatch(s, p);
        System.out.println(match);
    }
}