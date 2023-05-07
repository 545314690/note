package code.labuladong.字符串.最小覆盖子串;

import java.util.*;


public class Solution {

    public static void main(String[] args) {
        String S = "lhibsbrpxssyuibsdicrucaeg";
        String T = "ebsdslcacpib";
        String minWindow = new Solution().minWindow(S, T);
        System.out.println(minWindow);
//        System.out.println(new Solution().contain(S,T));
    }
    /**
     * 
     * @param S string字符串 
     * @param T string字符串 
     * @return string字符串
     */
    public String minWindow (String S, String T) {
        // write code here
        int left=0; //左指针
        int right=1; //右指针
        int minLen = Integer.MAX_VALUE; // 最小长度
        String minWindow = ""; //最小window字符串
        while(left<S.length()){
            //window 包含T则，左指针右移
            while(left<=right && contain(S.substring(left,right),T)){
                //找出最小window
                if(minLen > right-left){
                    minWindow = S.substring(left,right);
                    minLen = right-left;
                }
                left++;//左指针右移

            }
            //window 不包含T则，右指针右移，右指针不能超过S长度
            if(right<S.length()){
                right++;
            } else{
                break;
            }
        }
        return minWindow;
    }

    /**
     * 判断 window 是否包含T
     * @param window
     * @param T
     * @return
     */
    boolean contain(String window, String T){
        if(window.length() < T.length()){
            return false;
        }
        int[] map1=new int[128];        //记录 字符串window的每个字符出现次数

        int[] map2=new int[128];        //记录 字符串T的每个字符出现次数

        for(int i=0;i<window.length();i++){
            map1[window.charAt(i)] ++ ;
        }
        for(int i=0;i<T.length();i++){
            map2[T.charAt(i)] ++ ;
        }

        for(int i=0;i<128;i++){
            //T的字符出现次数不能大于window的字符出现次数，比如T：aas ，window ： as
            if(map1[i] < map2[i]){
                return false;
            }
        }
        return true;

    }
}