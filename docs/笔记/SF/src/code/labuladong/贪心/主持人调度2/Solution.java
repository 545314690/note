package code.labuladong.贪心.主持人调度2;

import java.util.*;


/**
 * 输入：
 * 2,[[1,2],[2,3]]
 * 返回值：
 * 1
 * 说明：
 * 只需要一个主持人就能成功举办这两个活动
 *
 *
 * 输入：
 * 2,[[1,3],[2,4]]
 * 返回值：
 * 2
 * 说明：
 * 需要两个主持人才能成功举办这两个活动
 */

public class Solution {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     * 计算成功举办活动需要多少名主持人
     * @param n int整型 有n个活动
     * @param startEnd int整型二维数组 startEnd[i][0]用于表示第i个活动的开始时间，startEnd[i][1]表示第i个活动的结束时间
     * @return int整型
     */
    public int minmumNumberOfHost (int n, int[][] startEnd) {
        // write code here
        if(n < 2){
            return n;
        }
        int cnt = 1;
        return cnt;
    }

    public static void main(String[] args) {
        int n = 10;
        int [][]startEnd =  new int[][]{{2147483646,2147483647},{-2147483648,-2147483647},{2147483646,2147483647},{-2147483648,-2147483647},{2147483646,2147483647},{-2147483648,-2147483647},{2147483646,2147483647},{-2147483648,-2147483647},{2147483646,2147483647},{-2147483648,-2147483647}};

//        for (int i = 0; i < startEnd.length; i++) {
//            startEnd[i][0] -=2147483646;
//            startEnd[i][1] -=2147483646;
//        }
        System.out.println(new Solution().minmumNumberOfHost(n,startEnd));
    }
}