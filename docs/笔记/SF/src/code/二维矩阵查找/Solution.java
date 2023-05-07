package code.二维矩阵查找;

/**
 * @Author lisenmiao
 * @Date 2021/3/1 15:20
 *
 * [
 * 	  [1,   4,  7, 11, 15],
 * 	  [2,   5,  8, 12, 19],
 * 	  [3,   6,  9, 16, 22],
 * 	  [10, 13, 14, 17, 24],
 * 	  [18, 21, 23, 26, 30]
 * ]
 *
 * 思想，先从左下角开始找，如果大于目标值，去上一行找，如果小于目标值，去下一列找。
 * 循环条件为 行和列下标不能越界
 */
public class Solution {

    //https://blog.csdn.net/sunshine2285/article/details/104866910?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3.control&dist_request_id=259ad19a-717d-43c6-9172-bd219762ff4c&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3.control
    public boolean findNumberIn2DArray(int[][] matrix, int target) {

        int rows = matrix.length;
        if(rows == 0) return false;
        int cols = matrix[0].length;
        if(cols == 0) return false;

        int row = rows - 1;
        int col = 0;
        while (row>=0&& col<cols){
            if(matrix[row][col]==target){
                return true;
            }else if(matrix[row][col]<target) {
                col++;
            }else {
                row--;
            }
        }
        return false;
    }
}
