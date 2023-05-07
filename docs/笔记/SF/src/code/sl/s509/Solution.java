package code.sl.s509;

class Solution {
    public int fib1(int n) {
        if(n<=0){
            return 0;
        }
        if(n==1){
            return 1;
        }
        int[] dp = new int[n+1];
        dp[0]=0;
        dp[1]=1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i-1] + dp[i-2];
        }
        return dp[n];
    }
    public int fib(int n) {
        if(n<=0){
            return 0;
        }
        if(n<=2){
            return 1;
        }
        int a = 0;
        int b = 1;
        int temp = 0;
        for (int i = 2; i <= n; i++) {
            temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
    public static void main(String[] args) {
        System.out.println(new Solution().fib(5));
    }
}