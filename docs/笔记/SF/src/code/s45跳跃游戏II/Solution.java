package code.s45跳跃游戏II;

/**
 * @Author lisenmiao
 * @Date 2021/3/3 19:06
 *
 * 给定一个非负整数数组，你最初位于数组的第一个位置。
 *
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 *
 * 你的目标是使用最少的跳跃次数到达数组的最后一个位置。
 *
 * 示例:
 *
 *
 *
 *
 * 输入: [2,3,1,1,4]
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 *     从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 */
public class Solution {
    public int jump(int[] nums) {
        if(nums.length<=1){
            return nums.length;
        }
        int cur = 0;
        int right = nums.length-1;
        int step = 0;
        int end = 0;

//        [2,3,1,1,4]
        while (end<right){
            int tmp = 0;
            for (int i = cur; i <= end; i++) {
                Math.max(nums[i]+1,tmp);
            }
            cur = end+1;
            end = tmp;
            step++;
        }
        return step;
    }

    public static void main(String[] args) {
        int [] a = new int[]{2,3,1,1,4};
        Solution solution = new Solution();
        int jump = solution.jump(a);
        System.out.println(jump);
    }


}
