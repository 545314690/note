package code.最长无重复子串;

import java.util.*;


/**
 * 题目描述
 * 给定一个数组arr，返回arr的最长无的重复子串的长度(无重复指的是所有数字都不相同)。
 * 示例1
 * 输入
 * [2,3,4,5]
 * 返回值
 * 4
 * 示例2
 * 输入
 * [2,2,3,4,3]
 * 返回值
 * 3
 */
public class Solution {
    /**
     * 
     * @param arr int整型一维数组 the array
     * @return int整型
     */
//    public int maxLength (int[] arr) {
//        // write code here
//        int maxLen = 0;
//        Map<Integer,Integer> set = new HashMap<>();
//        for (int i = 0; i < arr.length; i++) {
//            set.put(arr[i],i);
//            int len = 1;
//            for (int j = i+1; j < arr.length; j++) {
//                if(set.containsKey(arr[j])){
//                    i=Math.max(set.get(arr[j])+1,i);
//                    break;
//                }else {
//                    set.put(arr[j],j);
//                    len++;
//                }
//            }
//            maxLen = Math.max(len,maxLen);
//        }
//        return maxLen;
//    }

    public int maxLength (int[] arr) {
        // write code here
        if(arr==null) return 0;
        Map<Integer,Integer> map=new HashMap<>();
        int res = -1;
        int start=0;//重复位置的下一个位置
        for(int i=0;i<arr.length;i++){
            if(map.containsKey(arr[i])){
                //当有重复的时候，需要更新start的位置，可能比当前重复的位置远也可能近
                start=Math.max(start,map.get(arr[i])+1);
            }
            //每遍历一次 就更新一次最值
            res=Math.max(res,i-start+1);
            map.put(arr[i],i);
        }
        return res;
    }

    public static void main(String[] args) {
        int [] arr = new int[]{2,3,4,5};
        int i = new Solution().maxLength(arr);
        System.out.println(i);
    }
}