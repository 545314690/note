package code.sl.s905;

import java.util.Arrays;

/**
 * @Author lisenmiao
 * @Date 2021/1/19 15:00
 */
class Solution {
    public int[] sortArrayByParity(int[] A) {
        int i = 0,j=A.length-1;
        while (i<j){
            if(!isOdd(A[i]) && isOdd(A[j])){
                swap(A,i,j);
            }
            if(isOdd(A[i])){
                i++;
            }
            if(!isOdd(A[j])){
                j--;
            }
        }
        return A;
    }
    public boolean isOdd(int a){
        return (a&1)==0;
    }
    public void swap(int[]arr ,int i,int j ){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    public static void main(String[] args) {
        int[] arr = new int[]{9,0,1,2,4,6,3,5,8};
        arr = new Solution().sortArrayByParity(arr);
        Arrays.stream(arr).forEach(System.out::print);
        System.out.println();
        System.out.println(3&1);
        System.out.println(4&1);
    }
}