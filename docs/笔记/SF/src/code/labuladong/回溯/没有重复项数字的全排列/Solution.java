package code.labuladong.回溯.没有重复项数字的全排列;

import java.util.*;

public class Solution {
    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> lists = new Solution().permute(new int[]{1, 2, 3});
        System.out.println(lists);
    }
    public ArrayList<ArrayList<Integer>> permute(int[] num) {
        if(num.length==0){
            return new ArrayList();
        }
        
        ArrayList<ArrayList<Integer>> allList = new ArrayList();
        for(int i=0;i<num.length;i++){
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(num[i]);
            for(int j=0;j<num.length;j++){
                if(i!=j){
                    list.add(num[j]);
                }
            }
            allList.add(list);
            if(list.size()<=2){
                continue;
            }
            for(int k=2;k<list.size();k++){
                ArrayList<Integer> list2 = new ArrayList<Integer>();
                list2.addAll(list);
                int val = list2.get(k);
                list2.remove(k);
                list2.add(1,val);
                allList.add(list2);
            }
        }
        return allList;
    }
}