package code.bingfa;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CHMDemo {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String,Integer>();
        map.put("key", 1);
        Object o = new Object();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (o){
                        int key = map.get("key") + 1; //step 1
                        map.put("key", key);//step 2
                    }
                }
            });
        }
        executorService.shutdown();
//        executorService.shutdownNow();
        System.out.println(executorService.isShutdown());

        while (!executorService.isTerminated()){

        }
        System.out.println("Finished all threads");
//        Thread.sleep(3000); //模拟等待执行结束
        System.out.println("------" + map.get("key") + "------");

    }
}
