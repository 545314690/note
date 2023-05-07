package code.labuladong.lecode剑指Offer2_008和大于等于target的最短子数组;


/**
 * 给定一个含有 n 个正整数的数组和一个正整数 target 。
 * <p>
 * 找出该数组中满足其和 ≥ target 的长度最小的 连续子数组 [numsl, numsl+1, ..., numsr-1, numsr] ，并返回其长度。如果不存在符合条件的子数组，返回 0 。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：target = 7, nums = [2,3,1,2,4,3]
 * 输出：2
 * 解释：子数组 [4,3] 是该条件下的长度最小的子数组。
 * 示例 2：
 * <p>
 * 输入：target = 4, nums = [1,4,4]
 * 输出：1
 * 示例 3：
 * <p>
 * 输入：target = 11, nums = [1,1,1,1,1,1,1,1]
 * 输出：0
 * <p>
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/2VG8Kg
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
class Solution {
    public int minSubArrayLen(int target, int[] nums) {

        int left = 0;
        int right = 0;
        int res = Integer.MAX_VALUE;
        int sum = 0;

        for (; right < nums.length; right++) {
            sum += nums[right];
            //如果大于等于目标值，左指针右移，总和减去左边值，对比最小长度
            while (sum >= target) {
                sum -= nums[left];
                res = Math.min(res, (right - left + 1));
                left++;
            }
        }
        return res == Integer.MAX_VALUE ? 0 : res;
    }

    public static void main(String[] args) {
        int target = 7;
        int nums[] = new int[]{2, 3, 1, 2, 4, 3};
        int len = new Solution().minSubArrayLen(target, nums);
        System.out.println(len);
    }
}