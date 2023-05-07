package code.juc.deadlock;

import java.util.concurrent.TimeUnit;

/**
 * @Author lisenmiao
 * @Date 2021/2/2 19:28
 */
public class DeadLockTest {

    Object lock1 = new Object();
    Object lock2 = new Object();

    public void start(){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock1){
                    System.out.println(Thread.currentThread()+"： 获取lock1成功");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread()+"： 等待获取lock2");

                    synchronized (lock2){
                        System.out.println(Thread.currentThread()+"： 获取lock2成功");
                    }
                }
            }
        },"t1");

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock2){
                    System.out.println(Thread.currentThread()+"： 获取lock2成功");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread()+"： 等待获取lock1");
                    synchronized (lock1){
                        System.out.println(Thread.currentThread()+"： 获取lock1成功");
                    }
                }
            }
        },"t2");
        t1.start();
        t2.start();
    }

    public static void main(String[] args) {
        DeadLockTest deadLockTest = new DeadLockTest();
        deadLockTest.start();
    }
}
