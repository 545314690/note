package code.剑指Offer03数组中重复的数字;

/**
 * 找出数组中重复的数字。
 *
 *
 * 在一个长度为 n 的数组 nums 里的所有数字都在 0～n-1 的范围内。数组中某些数字是重复的，但不知道有几个数字重复了，也不知道每个数字重复了几次。请找出数组中任意一个重复的数字。
 *
 * 示例 1：
 *
 * 输入：
 * [2, 3, 1, 0, 2, 5, 3]
 * 输出：2 或 3
 * 限制：
 * 2 <= n <= 100000
 * 链接：https://leetcode-cn.com/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof
 */
class Solution {
    public int findRepeatNumber(int[] nums) {
        int[] arr = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if(arr[nums[i]]==0){
                arr[nums[i]] = 1;
            }else {
                return nums[i];
            }
        }
        return 0;

//        for (int i = 0; i < nums.length; i++) {
//            int num = nums[i];
//            arr[num] = arr[num]+1;
//            if(arr[num]>1){
//                return num;
//            }
//        }
//        return 0;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 1, 0, 2, 5, 3};
        Solution solution = new Solution();
        int repeatNumber = solution.findRepeatNumber(nums);
        System.out.println(repeatNumber);
    }
}