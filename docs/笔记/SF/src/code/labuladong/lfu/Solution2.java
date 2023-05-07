package code.labuladong.lfu;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;


public class Solution2 {
    /**
     * lfu design
     * @param operators int整型二维数组 ops
     * @param k int整型 the k
     * @return int整型一维数组
     */

    public static void main(String[] args) {
//        [[1,1,1],[1,2,2],[1,3,3],[1,4,4],[2,4],[2,3],[2,2],[2,1],[1,5,5],[2,4]],4
//[[1,1,1],[1,2,2],[1,3,2],[1,2,4],[1,3,5],[2,2],[1,4,4],[2,1]],3
                int k = 4;
        int[][] operators = new int[10][];
        operators[0] = new int[]{1,1,1};
        operators[1] = new int[]{1,2,2};
        operators[2] = new int[]{1,3,3};
        operators[3] = new int[]{1,4,4};
        operators[4] = new int[]{2,4};
        operators[5] = new int[]{2,3};
        operators[6] = new int[]{2,2};
        operators[7] = new int[]{2,1};
        operators[8] = new int[]{1,5,5};
        operators[9] = new int[]{2,4};


        int[] ints = new Solution2().LFU(operators,k);

        for (int i:ints) {
            System.out.println(i);
        }

    }

    public  static class Node{
        Integer key;
        Integer value;
        Integer times = 0;
        //...
        public Node(Integer key,Integer value,Integer times){
            this.key = key;
            this.value = value;
            this.times = times;
        }
    }
    Map<Integer,Node> map = new HashMap();
    Map<Integer,LinkedList<Node>> timesMap = new HashMap();
    int size = 0;
    int minTimes = 0;
    int get(int key){
        if(!map.containsKey(key)){
            return -1;
        }
        Node node = map.get(key);
        update(node,node.value);
        return node.value;
    }

    void set(int key,int value){
        if(!map.containsKey(key)){
            if(map.size()==size){
                LinkedList<Node> linkedListMinTimes = timesMap.get(minTimes);
                Node node = linkedListMinTimes.removeLast();
                map.remove(node.key);

            }
            int times = 1;
            Node node = new Node(key,value,times);
            minTimes = 1;

            LinkedList<Node> linkedList = timesMap.get(times);
            if(linkedList == null){
                linkedList = new LinkedList();
            }
            linkedList.addFirst(node);
            map.put(key,node);
            timesMap.put(minTimes,linkedList);
        }else{
            update(map.get(key),value);
        }
    }
    void update(Node node,int value){
        LinkedList<Node> linkedList = timesMap.get(node.times);
        linkedList.remove(node);
        if(linkedList.size() == 0){
            timesMap.remove(node.times);
            map.remove(node.key);
            minTimes = node.times+1;
        }
        node.times++;
        node.value = value;
        linkedList = timesMap.get(node.times);
        if(linkedList == null){
            linkedList = new LinkedList();
        }
        linkedList.addFirst(node);
        timesMap.put(node.times,linkedList);

        map.put(node.key,node);
//        minTimes = Math.min(minTimes, node.times);
//        timesMap.put(node.times)
    }
    public int[] LFU (int[][] operators, int k) {
        // write code here
        List<Integer> result = new ArrayList();
        size = k;
        for(int i=0;i< operators.length;i++){
            int[] arr = operators[i];
            int op = arr[0];
            int key = arr[1];
            if(op == 1){
                int value = arr[2];
                set(key, value);
            } else {
                int v = get(key);
                result.add(v);
            }


        }
        int[] resultArr = new int [result.size()];

        for(int j=0;j<result.size();j++){
            resultArr[j] = result.get(j);
        }
        return resultArr;
    }
    
}