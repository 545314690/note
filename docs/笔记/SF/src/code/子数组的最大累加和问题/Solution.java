package code.子数组的最大累加和问题;

import java.util.*;

/**
 * 题目描述
 * 给定一个数组arr，返回子数组的最大累加和
 * 例如，arr = [1, -2, 3, 5, -2, 6, -1]，所有子数组中，[3, 5, -2, 6]可以累加出最大的和12，所以返回12.
 * 题目保证没有全为负数的数据
 * [要求]
 * 时间复杂度为O(n) ，空间复杂度为O(1)
 *https://www.nowcoder.com/practice/554aa508dd5d4fefbf0f86e5fe953abd?tpId=117&tqId=37797&companyId=665&rp=1&ru=%2Fcompany%2Fhome%2Fcode%2F665&qru=%2Fta%2Fjob-code-high%2Fquestion-ranking&tab=answerKey
 * 示例1
 * 输入
 * [1, -2, 3, 5, -2, 6, -1]
 * 返回值
 * 12
 */
public class Solution {
    /**
     * max sum of the subarray
     * @param arr int整型一维数组 the array
     * @return int整型
     */
    public int maxsumofSubarray (int[] arr) {
        // write code here
//        arr = [1, -2, 3, 5, -2, 6, -1]
        if(arr==null){
            return 0;
        }
        if(arr.length==1){
            return arr[0];
        }
        int [] sum = new int[arr.length+1];
        sum[0]=0;
        int max = 0;
        for (int i=1;i<arr.length+1;i++){
            sum[i] = Math.max(sum[i-1],sum[i-1]+arr[i-1]);
            max = Math.max(max,sum[i]);
        }
        return max;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, -2, 3, 5, -2, 6, -1};
        Solution solution = new Solution();
        int i = solution.maxsumofSubarray(arr);
        System.out.println(i);
    }
}