package code.sl.s474;

class Solution {

    public int[] getNumsCount(String str) {
        int zeros = 0;
        int ones = 0;
            char[] chars = str.toCharArray();
            for (char c:chars
                 ) {
                if(c == '0'){
                    zeros++;
                }else {
                    ones++;
                }
            }
        return new int[]{zeros,ones};
    }

//    dp(i, j) = max(1 + dp(i - cost_zero[k], j - cost_one[k]))
//    if i >= cost_zero[k] and j >= cost_one[k]
//
    public int findMaxForm(String[] strs, int m, int n) {
        int [][] dp = new int[m+1][n+1];
        dp[0][0] = 0;
        for (String s:strs ) {
            int[] numsCount = getNumsCount(s);
            for (int i = m; i >=numsCount[0]; i--) {
                for (int j = n; j >=numsCount[1]; j--) {
                        dp[i][j] = Math.max(dp[i-numsCount[0]][j-numsCount[1]]+1,dp[i][j]);
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String [] strs = {"10", "0001", "111001", "1", "0"};
        System.out.println(new Solution().findMaxForm(strs,5,3));
    }
}