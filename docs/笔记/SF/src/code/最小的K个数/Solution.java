package code.最小的K个数;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 题目描述
 * 输入n个整数，找出其中最小的K个数。例如输入4,5,1,6,2,7,3,8这8个数字，则最小的4个数字是1,2,3,4。
 * 示例1
 * 输入
 * [4,5,1,6,2,7,3,8],4
 * 返回值
 * [1,2,3,4]
 */
public class Solution {
    public ArrayList<Integer> GetLeastNumbers_Solution(int [] input, int k) {
        if(input==null){
            return null;
        }
        if(input.length<=k){
            ArrayList<Integer> list= new ArrayList<>();
            Arrays.stream(input).forEach(
                   e-> list.add(e)
            );
            return new ArrayList<>();
        }
        PriorityQueue<Integer> queue = new PriorityQueue<>((a,b)->(b-a));
        for (int i = 0; i < input.length; i++) {
            if(i<k){
                queue.offer(input[i]);
            }else {
                if(queue.size()>0 && input[i]<queue.peek()){
                    queue.poll();
                    queue.offer(input[i]);
                }
            }
        }
//        Integer[] ret = new Integer[k];
//        for (int i = k-1; i >=0; i--) {
//            if(queue.size()>0){
//                ret[i] = queue.poll();
//            }
//        }
        return new ArrayList<>(queue);
//        return new ArrayList<>(Arrays.asList(ret));
    }

    public static void main(String[] args) {
        int[] a = new int[]{4,5,1,6,2,7,3,8};
        Solution solution = new Solution();
        ArrayList<Integer> list = solution.GetLeastNumbers_Solution(a, 10);
        System.out.println(list);
    }
}