package code.跳台阶;


import java.math.BigInteger;

public class Solution {
    //f(n) = f(n-1) + f(n-2)
    public int JumpFloor(int target) {
        if(target==0){
            return 0;
        }
        int[] dp = new int[target+1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 2;
        int sum = 1;
        for(int i=3;i<=target;i++){
            dp[i] = dp[i-1] + dp[i-2]; 
        }
        return dp[target];
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.JumpFloor(4));
    }
}