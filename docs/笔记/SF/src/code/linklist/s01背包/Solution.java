package code.linklist.s01背包;

/**
 * @Author lisenmiao
 * @Date 2021/1/25 11:04
 */

/**
 *
 *
 *
 */
class Solution {
//    N = 3, W = 4
//    wt = [2, 1, 3]
//    val = [4, 2, 3]
    public int findLengthOfLCIS(int []wt,int []val,int W) {
        if(wt == null || wt.length==0 || W==0){
            return 0;
        }
        int length = W+1;
        int[] dp = new int[length];
        dp[0] = 0;
        for (int i = 1; i <= wt.length; i++) {
            for (int j = 1; j <= W; j++) {
                if(j>=wt[i-1]){
                    dp[j] = Math.max(dp[j-1] + val[i-1],dp[j-wt[i-1]]);
                }
            }
        }
        return dp[W];
    }

    public static int zeroOnePackOpt(int V, int[] C, int[] W) {
        // 防止无效输入
        if ((V <= 0) || (C.length != W.length)) {
            return 0;
        }
        int n = C.length;
        int[] dp = new int[V + 1];
        // 背包空的情况下，价值为 0
        dp[0] = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = V; j >= C[i]; --j) {
                dp[j] = Math.max(dp[j], dp[j - C[i]] + W[i]);
            }
        }
        return dp[V];
    }
    public static void main(String[] args) {
        Solution solution = new Solution();
        int N = 3, W = 4;
        int []wt = new int []{2, 1, 3};
        int []val = new int []{4, 2, 3};
        int n = solution.findLengthOfLCIS(wt,val,5);
        System.out.println(n);
    }
}