package code.bingfa;

import java.util.concurrent.locks.LockSupport;

public class TestObjWait {

    public static void main(String[] args)throws Exception {
        final Object obj = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for(int i=0;i<10;i++){
                    sum+=i;
                }
                LockSupport.park();
                System.out.println(sum);
            }
        });
        A.start();
        //睡眠一秒钟，保证线程A已经计算完成，阻塞在wait方法，使用unpark方法可以不用睡眠，先调用unpark
        //Thread.sleep(1000);
        LockSupport.unpark(A);
    }
}