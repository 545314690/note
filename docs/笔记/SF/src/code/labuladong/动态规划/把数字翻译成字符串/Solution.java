package code.labuladong.动态规划.把数字翻译成字符串;

import java.util.*;


public class Solution {
    public static void main(String[] args) {
        System.out.println(new Solution().solve("31717126241541717"));
    }
    /**
     * 解码
     * @param nums string字符串 数字串
     * @return int整型
     */
    LinkedHashMap<Integer,Integer> cache = new LinkedHashMap();

    public int solve (String nums) {
        // write code here
        ArrayList<Integer> list2 = new ArrayList<Integer>();

        if(nums.length() == 1){
            return 1;
        }
        //当0的前面不是1或2时，无法译码，0种
        for(int i = 1; i < nums.length(); i++){
            if(nums.charAt(i) == '0')
                if(nums.charAt(i - 1) != '1' && nums.charAt(i - 1) != '2')
                    return 0;
        }
        int[] dp = new int[nums.length()+1];
        dp[0] = 1;
        dp[1] = 1;
        for(int i=2;i<=nums.length();i++){
            char c1 = nums.charAt(i-2);
            char c2 = nums.charAt(i-1);
            //在11-19，21-26之间的情况
            if((c1=='1' && c2>='1' && c2<='9') || (c1=='2' && c2>='1' && c2<='6')){
                dp[i] = dp[i-1] + dp[i-2];
            }else{
                dp[i] = dp[i-1];
            }
        }
        return dp[nums.length()];
    }
}