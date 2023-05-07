package code.在转动过的有序数组中寻找目标值;

import java.util.*;


public class Solution {
    /**
     * 
     * @param A int整型一维数组 
     * @param target int整型 
     * @return int整型
     */
    public int search (int[] A, int target) {
        // write code here
        Arrays.sort(A);
        int max = A[A.length-1],min=A[0];
        int left  = 0;
        int right =A.length-1;
        if(target>max || target<min){
            return -1;
        }
        int mid = left+ ((right-left)>>1);
        while(left<=right){
            if(A[mid]>target){
                right = mid;
                mid = left+ ((right-left)>>1);
            }else if(A[mid]<target){
                left = mid;
                mid = left+ ((right-left)>>1);
            }else{
                return mid;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int search = new Solution().search(new int[]{3,2,1}, 1);
        System.out.println(search);
    }
}