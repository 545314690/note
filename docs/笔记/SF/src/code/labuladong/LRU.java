package code.labuladong;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author lisenmiao
 * @Date 2021/1/22 16:08
 */
public class LRU {
    LinkedHashMap<String,String> cache = new LinkedHashMap<>();
    int cap;
    public LRU(int cap){
        this.cap = cap;
    }
    public String get(String k){
        if(cache.containsKey(k)){
            // 将 key 变为最近使用
            String value = cache.get(k);
            makeRecentlyUse(k,value);
            return cache.get(k);

        }
        else {
            return null;
        }
    }

    public void put(String k,String v){
        if(cache.containsKey(k)){
            makeRecentlyUse(k,v);
            //标记为最近使用
            return;
        }
        if(cache.size()>=cap && cache.size()>0){
            String oldestKey  = cache.keySet().iterator().next();
            cache.remove(oldestKey );
        }
        // 将新的 key 添加链表尾部
        cache.put(k, v);
    }
    private void makeRecentlyUse(String key,String value){
        // 删除 key，重新插入到队尾
        cache.remove(key);
        cache.put(key,value);
    }
    public void printAll(){
        Iterator<String> iterator = cache.keySet().iterator();
        while (iterator.hasNext()){
            System.out.print(iterator.next());
        }
        System.out.println();
    }
    public static void main(String[] args) {
        LRU lru = new LRU(3);
        List<String> list = Arrays.asList("1", "2", "3", "1", "4", "3", "5");
        for (int i = 0; i < list.size(); i++) {
            lru.put(list.get(i),list.get(i));
            lru.printAll();
        }
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
            lru.get(list.get(i));
            lru.printAll();
        }
    }
}
