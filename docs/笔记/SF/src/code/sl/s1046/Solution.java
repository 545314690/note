package code.sl.s1046;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Author lisenmiao
 * @Date 2020/12/30 10:30
 */
class Solution {
    public int lastStoneWeight(int[] stones) {

        if(stones.length==1){
            return stones[0];
        }
        int cha = 0 ;
        for (int i = 0; i < stones.length - 1; i++) {
            Arrays.sort(stones);
            cha = stones[stones.length - 1] - stones[stones.length-2];
            stones[stones.length - 2]=cha;
            stones[stones.length - 1]=0;
        }
        return cha;
    }

    public int lastStoneWeight2(int[] stones) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
        for (int i : stones)
            pq.offer(i);
        while (pq.size() >= 2) {
            int x = pq.poll();
            int y = pq.poll();
            if (x > y)
                pq.offer(x - y);
        }
        return pq.size() == 1 ? pq.peek() : 0;

    }

    public int lastStoneWeight3(int[] stones) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.reverseOrder());
        for (int s:stones
             ) {
            queue.offer(s);
        }
        while (queue.size()>1){
            Integer a = queue.poll();
            Integer b = queue.poll();
            if(a-b>0){
                queue.offer(a-b);
            }
        }
        return queue.peek()==null?0:queue.peek();
    }
    public static void main(String[] args) {
        int[] s = new int[]{2,7,4,1,8,1};
//        int[] s = new int[]{3,1};
        System.out.println(new Solution().lastStoneWeight3(s));
    }
}