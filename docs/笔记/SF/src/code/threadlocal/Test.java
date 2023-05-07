package code.threadlocal;

public class Test {


//    static  User user = new User();

//    private static ThreadLocal<User> threadLocal = new ThreadLocal<User>(){
//        @Override
//        protected User initialValue() {
//            return user;
//        }
//    };


    private static ThreadLocal<User> threadLocal = new ThreadLocal<User>();


    public static void main(String[] args) {
        User user = new User();
        user.setValue(0);
        threadLocal.set(user);
        System.out.println(Thread.currentThread()+ ":" + threadLocal.get());

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                }catch (Exception e){
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread()+ ":" + threadLocal.get());
                user.setValue(111);
                threadLocal.set(user);
                System.out.println(Thread.currentThread()+ ":" + threadLocal.get().getValue());

            }
        }, "t1");
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread()+ ":" + threadLocal.get());
                user.setValue(222);
                threadLocal.set(user);
                System.out.println(Thread.currentThread()+ ":" + threadLocal.get().getValue());
                try {
                    Thread.sleep(100);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread()+ "sleep:" + threadLocal.get().getValue());
            }
        }, "t2");
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread()+ ":" + threadLocal.get());

            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
    }
}
