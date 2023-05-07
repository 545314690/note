package code.sl.s581;

import java.util.Arrays;

/**
 * 581. 最短无序连续子数组
 * 给定一个整数数组，你需要寻找一个连续的子数组，如果对这个子数组进行升序排序，那么整个数组都会变为升序排序。
 *
 * 你找到的子数组应是最短的，请输出它的长度。
 *
 * 示例 1:
 *
 * 输入: [2, 6, 4, 8, 10, 9, 15] -> [2,4,6,8,9,10,15]
 * 输出: 5
 * 解释: 你只需要对 [6, 4, 8, 10, 9] 进行升序排序，那么整个表都会变为升序排序。
 * 说明 :
 *
 * 输入的数组长度范围在 [1, 10,000]。
 * 输入的数组可能包含重复元素 ，所以升序的意思是<=。
 */
class Solution {
//    输入: [2, 6, 4, 8, 10, 9, 15]
//    输入: [2, 10, 4, 8, 10, 10, 15]
//    输入: [2, 12, 4, 8, 10, 10, 15]

    /**
     * 排序，从两端找第一个不一致的字符
     * 结果为：right - left+1
     * @param nums
     * @return
     */
    public int findUnsortedSubarray(int[] nums) {
        if(nums ==null || nums.length==1){
            return 0;
        }
        int [] newArr = new int[nums.length];
        System.arraycopy(nums,0,newArr,0,nums.length);
        Arrays.sort(nums);
        int left = 0;
        int right = nums.length-1;
        while (left<nums.length && nums[left] == newArr[left]){
            left++;
        }
        while (right>=0 && nums[right] == newArr[right]){
            right--;
        }
        //left遍历到底了，未找到不一样字符串，说明原本就有序
        if(left == nums.length){
            return 0;
        }
        return right - left+1;
    }

    public static void main(String[] args) {
//        int [] arr = new int[]{2,5,3,2,4};
        int [] arr = new int[]{1,2,3,4};
        System.out.println(new Solution().findUnsortedSubarray(arr));
    }
}