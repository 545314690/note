package code.寻找第K大;

import java.util.*;

/**
 * 题目描述
 * 有一个整数数组，请你根据快速排序的思路，找出数组中第K大的数。
 *
 * 给定一个整数数组a,同时给定它的大小n和要找的K(K在1到n之间)，请返回第K大的数，保证答案存在。
 * 示例1
 * 输入
 * [1,3,5,2,2],5,3
 * 返回值
 * 2
 */
public class Solution {
    public int findKth(int[] a, int n, int K) {
        // write code here
        int target = n-K;
        int left = 0;
        int right = n-1;
        while (true){
            int index = part(a, left, right);
            if(target==index){
                return a[target];
            }else if(index<target){
                left = index+1;
            }else {
                right = index -1;
            }
        }
    }

    public int part(int[] a, int left, int right) {
        // write code here
        int base = a[left];
        int i = left;
        int j = right;
        while (i<j){
            while (i<j && a[right]>=base){
                j--;
            }
            while (i<j && a[left]>=base){
                i++;
            }
            if(i<j){
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }
        a[left] = a[i];
        a[i] = base;
        return i;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int [] a = new int[]{1,3,5,2,2};
        int kth = solution.findKth(a, 5, 3);
        System.out.println(kth);
    }
}