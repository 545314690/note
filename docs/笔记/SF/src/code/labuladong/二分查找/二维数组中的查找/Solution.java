package code.labuladong.二分查找.二维数组中的查找;

import java.lang.reflect.Field;


/**
 * 在一个二维数组array中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
 * [
 * [1,2,8,9],
 * [2,4,9,12],
 * [4,7,10,13],
 * [6,8,11,15]
 * ]
 * 给定 target = 7，返回 true。
 *
 * 给定 target = 3，返回 false。
 */
public class Solution {

    public static void main(String[] args) {
        int target = 7;
        int[][] array = new int[][]{new int[]{1,2,8,9},new int[]{2,4,9,12},new int[]{4,7,10,13},new int[]{6,8,11,15}};

        System.out.println(new Solution().Find(target,array));
    }

    public boolean Find(int target, int [][] array) {
        //找出规律，从左下角（或者右上角）搜索
        if(array.length ==0){
            return false;
        }
        int rows = array.length - 1;
        int cols = 0;

        while (rows>=0 && cols<=array[0].length - 1){
            int temp = array[rows][cols];
            if(temp == target){
                return true;
            }
            if(temp > target){
                rows--;
            }else{
                cols++;
            }
        }
        return false;
    }
}