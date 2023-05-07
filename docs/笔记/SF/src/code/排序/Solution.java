package code.排序;

import java.util.*;


public class Solution {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     * 将给定数组排序
     * @param arr int整型一维数组 待排序的数组
     * @return int整型一维数组
     */
    public int[] MySort (int[] arr) {
        // write code here
        if(arr==null || arr.length==1){
            return arr;
        }
        quickSort(arr,0,arr.length-1);
        return arr;
    }
    
    public void quickSort (int[] arr,int left,int right) {
        if(left>right){
            return;
        }
        // write code here
        int base = arr[left];
        int i = left;
        int j = right;
        while(i!=j){
            while(i<j && arr[j]>=base){
                j--;
            }
            while(i<j && arr[i]<=base){
                i++;
            }
            if(i<j){
                swap(arr,i,j);
            }
        }
        arr[left] = arr[i];
        arr[i] = base;

        quickSort(arr,left,i-1);
        quickSort(arr,i+1,right);
    }
    public void swap(int[] arr, int i,int j){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int arr [] = new int[]{5,2,3,1,4};
        int[] ints = solution.MySort(arr);
        System.out.println(ints);
    }
}