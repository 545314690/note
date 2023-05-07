package code.labuladong.字符串.合并区间;

import java.util.*;
/**
 * Definition for an interval.
 * public class Interval {
 *     int start;
 *     int end;
 *     Interval() { start = 0; end = 0; }
 *     Interval(int s, int e) { start = s; end = e; }
 * }
 */
 class Interval {
      int start;
      int end;
      Interval() { start = 0; end = 0; }
      Interval(int s, int e) { start = s; end = e; }

    @Override
    public String toString() {
        return "Interval{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
public class Solution {

    public static void main(String[] args) {
        ArrayList<Interval> intervals = new ArrayList<Interval>(){{
//            [[2,3],[2,2],[3,3],[1,3],[5,7],[2,2],[4,6]]
            add(new Interval(1,1));
            add(new Interval(2,2));
            add(new Interval(2,2));
            add(new Interval(4,4));
            add(new Interval(5,5));
        }};
        intervals = new Solution().merge(intervals);
        System.out.println(intervals);
    }
    public ArrayList<Interval> merge(ArrayList<Interval> intervals) {
        if(intervals.size() <2){
            return intervals;
        }
        Collections.sort(intervals, new Comparator<Interval>(){
            public int compare(Interval a, Interval b){
                return a.start - b.start;
            }
        });
        
        ArrayList<Interval> list = new ArrayList();
        Interval a = null;
        boolean merged = false;
        for(int i=0;i<intervals.size()-1;i++){
            if(a == null){
                a = intervals.get(i);
            }
            Interval b = intervals.get(i+1);
            if(a.end >= b.start){
               a = new Interval(a.start,Math.max(b.end,a.end)); 
                if(list.size() > 0){
                    list.remove(list.size()-1);
                }
               list.add(a);
               merged = true;
            } else{
                if(!merged){
                   list.add(new Interval(a.start,a.end)); 
                }
                list.add(b);
                a = b;
            }
        }
        return list;
    }
}