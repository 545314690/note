package code.bingfa;

/**
 * @Author lisenmiao
 * @Date 2021/1/8 15:51
 */
public class TestLock {
    public static void main(String[] args) {
        String a ="12345678";
        String b ="abcdefgh";
        Object lock = new Object();
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                char[] chars = a.toCharArray();

                    for (int i = 0; i < chars.length; i++) {
                        synchronized (lock){
                        System.out.print(chars[i]);
                        try {
                            lock.notify();
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }    }

                }

            }
        };
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                char[] chars = b.toCharArray();

                    for (int i = 0; i < chars.length; i++) {
                        synchronized (lock){ try {
                            lock.wait();
                            System.out.print(chars[i]);
                            lock.notify();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        new Thread(r2).start();
        new Thread(r1).start();
    }
}
