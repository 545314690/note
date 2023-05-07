package code.labuladong.lfu;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;


public class Solution {
    /**
     * lfu design
     * @param operators int整型二维数组 ops
     * @param k int整型 the k
     * @return int整型一维数组
     */

    public static void main(String[] args) {
//        [[1,1,1],[1,2,2],[1,3,3],[1,4,4],[2,4],[2,3],[2,2],[2,1],[1,5,5],[2,4]],4

                int k = 4;
        int[][] operators = new int[11][];
        operators[0] = new int[]{1,1,1};
        operators[1] = new int[]{1,2,2};
        operators[2] = new int[]{1,3,3};
        operators[3] = new int[]{1,4,4};
        operators[4] = new int[]{1};
        operators[5] = new int[]{2,4};
        operators[6] = new int[]{2,3};
        operators[7] = new int[]{2,2};
        operators[8] = new int[]{2,1};
        operators[9] = new int[]{1,5,5};
        operators[10] = new int[]{2,4};


        int[] ints = new Solution().LFU(operators,k);

        for (int i:ints) {
            System.out.println(i);
        }

    }

    public  static class Node{
         Integer key;
         Integer value;
         Integer times = 0;
         Long ts;
        //...
        public Node(Integer value,Integer times){
            this.value = value;
            this.times = times;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    ", times=" + times +
                    ", ts=" + ts +
                    '}';
        }
    }
    public int[] LFU (int[][] operators, int k) {
        // write code here
        List<Integer> result = new ArrayList();
        Map<Integer,Node> map = new HashMap();
        PriorityBlockingQueue<Node> queue = new PriorityBlockingQueue<Node>(k,new Comparator<Node>(){
            public int compare(Node a, Node b){
                if(a.times == b.times){
                    return Long.compare(a.ts,b.ts);
                }else{
                    return a.times-b.times;
                }
            }
        });
        
        for(int i=0;i< operators.length;i++){
            int[] arr = operators[i];
            if(arr.length<2){
                continue;
            }
            
            Node node = map.get(arr[1]);
            if(arr[0] == 1){
                int key = arr[1];
                int value = arr[2];
                if(queue.size()<k && node != null){

                    node.times = node.times+1;
                    node.value = value;
                    node.key = key;
                    node.ts = System.currentTimeMillis();
                    map.put(key,node); 
                    queue.offer(node);
                }else{
                    if(queue.size()>=k){
                        Node oldNode = queue.poll();
                        if( oldNode != null){
                            map.remove(oldNode.key);
                        } 
                    }
                    node = new Node(value,1);
                    node.key = key;
                    node.ts = System.currentTimeMillis();
                    map.put(key,node); 
                    queue.offer(node);
                }
                
            } else {
                int key = arr[1];
                if(node == null){
                    result.add(-1);
                }else{
                    queue.remove(node);

                    node.times = node.times+1;
                    node.ts = System.currentTimeMillis();

                    queue.offer(node);
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                    map.put(key,node);
                    result.add(map.get(key).value);

//                   Node a =  queue.peek();
//                    System.out.println(a);
                }
                //1,2,3,4
                
            }
            
            
        }
         int[] resultArr = new int [result.size()];
 
        for(int j=0;j<result.size();j++){
            resultArr[j] = result.get(j);
        }
        return resultArr;
    }
    
    
}