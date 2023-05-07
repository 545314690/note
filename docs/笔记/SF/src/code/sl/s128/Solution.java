package code.sl.s128;


import java.util.Arrays;

/**
 * 128. 最长连续序列
 * 给定一个未排序的整数数组 nums ，找出数字连续的最长序列（不要求序列元素在原数组中连续）的长度。
 *
 * 进阶：你可以设计并实现时间复杂度为 O(n) 的解决方案吗？
 *
 * 示例 1：
 *
 * 输入：nums = [100,4,200,1,3,2]
 * 输出：4
 * 解释：最长数字连续序列是 [1, 2, 3, 4]。它的长度为 4。
 * 示例 2：
 *
 * 输入：nums = [0,3,7,2,5,8,4,6,0,1]
 * 输出：9
 *
 */
class Solution {
    public int longestConsecutive(int[] nums) {
        if(nums==null){
            return 0;
        }
        if(nums.length==1){
            return 1;
        }
        Arrays.sort(nums);
        int[] dp = new int[nums.length+1];
        dp[0]=1;
        int max =0;
        for (int i = 1; i < nums.length ; i++) {
            if(nums[i]==nums[i-1]+1){
                dp[i] = dp[i-1] +1 ;
            }else if(nums[i]==nums[i-1]){
                dp[i] = dp[i-1] ;
            }else {
                dp[i] = 1;
            }
            if(dp[i]>max){
                max = dp[i];
            }
        }
        return max;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{1,2,0,1};
        System.out.println(new Solution().longestConsecutive(nums));
    }
}