package code.bingfa;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author lisenmiao
 * @Date 2021/1/8 15:51
 */
public class TestLock3 {
    static  Thread thread1 = null,thread2=null;
    public static void main(String[] args) {
        String a ="12345678";
        String b ="abcdefgh";
        Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    char[] chars = a.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        try {
                            System.out.print(chars[i]);
                            condition2.signal();
                            condition1.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }finally {
                    lock.unlock();
                }


            }
        };
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    char[] chars = b.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        try {
                            condition2.await();
                            System.out.print(chars[i]);
                            condition1.signal();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }finally {
                    lock.unlock();
                }
            }
        };
        thread1 = new Thread(r1);
        thread2 = new Thread(r2);

        thread2.start();
        thread1.start();
    }
}
