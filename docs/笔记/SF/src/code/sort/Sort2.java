package code.sort;

import java.util.Arrays;

/**
 * @Author lisenmiao
 * @Date 2021/1/19 13:32
 */
public class Sort2 {

    public static void sort(int [] arr){
        int i=0;int j = arr.length-1;
        while (i<j){
            if(arr[i]%2!=0){
                i++;
            }
            if(arr[j]%2!=1){
                j--;
            }
            if(arr[i]%2!=1 && arr[j]%2!=0){
                swap(arr,i,j);
            }
        }
        System.out.println(i);
        System.out.println(j);
        int bound = 0;
        if(arr[j]%2==0){
            bound = j-1;
        }else {
            bound = j;
        }
    }
//    public static void mergeSort(int [] arr){
//        mergeSortProcess(arr, 0,arr.length-1);
//    }
    public  static  void swap(int[]arr ,int i,int j ){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
     }
    public static void main(String[] args) {
        int[] arr = new int[]{9,0,1,2,4,6,3,5,8};
        sort(arr);
        Arrays.stream(arr).forEach(System.out::print);
    }
}
