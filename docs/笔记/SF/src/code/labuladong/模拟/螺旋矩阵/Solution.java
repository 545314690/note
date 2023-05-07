package code.labuladong.模拟.螺旋矩阵;

import java.util.ArrayList;
public class Solution {
    public static void main(String[] args) {
        int[][] matrix = new int[][]{{1,2,3},{4,5,6},{7,8,9}};
        ArrayList<Integer> integers = new Solution().spiralOrder(matrix);
        System.out.println(integers);
    }
    public ArrayList<Integer> spiralOrder(int[][] matrix) {

        ArrayList<Integer> list = new ArrayList<Integer>();
        if(matrix.length == 0){
            return list;
        }
        int m = matrix.length;
        int n = matrix[0].length;
        int i = 0,j=0;
        int minLeft = 0;
        int maxRight = n-1;
        int minTop = 0;
        int maxButtom = m-1;
        boolean left2right = true;
        boolean top2buttom = false;
        boolean right2left = false;
        boolean buttom2top = false;
//         [1,2,3],
//         [4,5,6],
//         [7,8,9]

//         [1,2,3,10],
//         [4,5,6,11],
//         [7,8,9,12]
        while(j >= minLeft && j<=maxRight && i>=minTop && i<=maxButtom){
            list.add(matrix[i][j]);
            if(left2right){
                j++;
                if(j==maxRight){
                    minTop++ ;
                    i++;
                    top2buttom = true;
                    
                    left2right = false;
                     right2left = false;
                     buttom2top = false;
                }
            }else if(top2buttom){
                i++;
                if(i==maxButtom){
                    right2left = true;
                     
                    left2right = false;
                     top2buttom = false;
                     buttom2top = false;
                    maxRight -- ;
                }
            }else  if(right2left){
                j--;
                if(j==minLeft){
                    maxButtom --;
                    buttom2top = true;
                    
                     left2right = false;
                     top2buttom = false;
                     right2left = false;
                }
            }else if(buttom2top){
                i++;
                if(i==minTop){
                    minLeft ++;
                    left2right = true;
                     
                     top2buttom = false;
                     right2left = false;
                     buttom2top = false;
                }
            }

        }
           return list;
        
            
    }
}