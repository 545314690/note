package code.bingfa;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author lisenmiao
 * @Date 2021/1/8 15:51
 */
public class TestLock2 {
    static  Thread thread1 = null,thread2=null;
    public static void main(String[] args) {
        String a ="12345678";
        String b ="abcdefgh";

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                char[] chars = a.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        System.out.print(chars[i]);
                        LockSupport.unpark(thread2);
                        LockSupport.park();
                }

            }
        };
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                char[] chars = b.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        LockSupport.park();
                        System.out.print(chars[i]);
                        LockSupport.unpark(thread1);
                    }
            }
        };
        thread1 = new Thread(r1);
        thread2 = new Thread(r2);

        thread2.start();
        thread1.start();
    }
}
