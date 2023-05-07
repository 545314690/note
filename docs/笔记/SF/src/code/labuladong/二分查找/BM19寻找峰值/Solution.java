package code.labuladong.二分查找.BM19寻找峰值;

import javax.swing.tree.TreeNode;
import java.util.*;


public class Solution {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * 
     * @param nums int整型一维数组 
     * @return int整型
     */
    public int findPeakElement (int[] nums) {

        if(nums.length == 1){
            return 0;
        }
        if(nums[0] > nums[1]){
            return 0;
        }
        if(nums[nums.length-1] > nums[nums.length-2]){
            return nums.length-1;
        }
        int i = (nums.length-1)/2;
        while(i<nums.length-1){
            if(nums[i]>nums[i-1] && nums[i]>nums[i+1]){
                return i;
            }
            if(nums[i]<nums[i+1]){
                i++;
            }else{
                i--;
            }
        }
        
        return -1;
    }

    public int minNumberInRotateArray(int [] array) {
//         if(array.length == 1){
//             return array[0];
//         }
//         if(array.length == 2){
//             return array[0]>array[1]?array[1]:array[0];
//         }
        int left = 0;
        int right = array.length-1;

        while(left <= right){
            if(left == right){
                return array[right];
            }
            int mid = (left + right) /2;
            int temp = array[mid];
            if(temp < array[mid-1] && temp < array[mid+1]){
                return temp;
            }
            if(temp > array[right]){
                left = mid+1;
            }else if(temp < array[right]){
                right = mid -1;
            }
        }
        return -1;
    }
    public static void main(String[] args) {
//        int []arr = new int[]{2,4,1,2,7,8,4};
        int []arr = new int[]{4,3};
        int peakElement = new Solution().findPeakElement(arr);
        int min = new Solution().minNumberInRotateArray(arr);
        System.out.println(peakElement);
        System.out.println(min);
    }
}