package code.sl.s390;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int eraseOverlapIntervals(int[][] intvs) {
        int minCount=0;
        Arrays.sort(intvs, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if(o1[1] == o2[1]){
                    return o1[0] - o2[0];
                }
                return o1[1] - o2[1];
            }
        });
        Set<Integer> removedRows  = new HashSet<>();
        for (int i = 0; i < intvs.length-1; i++) {
            for (int j = i+1; j < intvs.length; j++) {
                if(removedRows.contains(i) || removedRows.contains(j)){
                    continue;
                }
                if(intvs[i][1]>intvs[j][0]){
                    minCount++;
                    removedRows.add(j);
                }
            }
        }
        return minCount;
    }

    public static void main(String[] args) {
        int[][] aa = new int[2][2];
        aa[0] = new int[]{1,2};
        aa[1] = new int[]{2,3};
        System.out.println(new Solution().eraseOverlapIntervals(aa));
    }
}