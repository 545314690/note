package code.labuladong;

/**
 * @Author lisenmiao
 * @Date 2021/1/22 16:08
 */
public class LFU {

//    class V{
//        private AtomicInteger count = new AtomicInteger(1);
//        private Object value;
//
//        public V( Object value) {
//            this.value = value;
//        }
//        public V incrCount(){
//            this.count.incrementAndGet();
//            return this;
//        }
//
//    }
//    ConcurrentSkipListMap<String, Object> cache = new ConcurrentSkipListMap<String, Object>(new Comparator<String>() {
//        @Override
//        public int compare(Key o1, Key o2) {
//            return o1.count-o2.count;
//        }
//    });
//
//    public void put(String key,Integer value){
//        Key k = new Key(key);
//        if(cache.containsKey(k)){
//            cache.remove(k);
//            k = cache.k
//            cache.put(k.incrCount(),value);
//        }else {
//            cache.put(k,value);
//        }
//    }
//
//    public void get(Key key){
//        if(cache.containsKey(key)){
//            cache.remove(key);
//            cache.put(key.incrCount(),value);
//        }
//    }
}
