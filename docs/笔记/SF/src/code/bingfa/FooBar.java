package code.bingfa;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1115. 交替打印FooBar
 * 我们提供一个类：
 *
 * class FooBar {
 *   public void foo() {
 *     for (int i = 0; i < n; i++) {
 *       print("foo");
 *     }
 *   }
 *
 *   public void bar() {
 *     for (int i = 0; i < n; i++) {
 *       print("bar");
 *     }
 *   }
 * }
 * 两个不同的线程将会共用一个 FooBar 实例。其中一个线程将会调用 foo() 方法，另一个线程将会调用 bar() 方法。
 *
 * 请设计修改程序，以确保 "foobar" 被输出 n 次
 */
class FooBar {
    private int n;

    public FooBar(int n) {
        this.n = n;
    }
    Lock lock = new ReentrantLock();
    Condition fConditon = lock.newCondition();
    Condition bConditon = lock.newCondition();
    AtomicInteger atomicInteger = new AtomicInteger(0);
    public void foo(Runnable printFoo) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 0; i < n; i++) {
                if (atomicInteger.get()!=0){
                    fConditon.await();
                }
                // printFoo.run() outputs "foo". Do not change or remove this line.
                printFoo.run();
                atomicInteger.set(1);
                bConditon.signal();

            }
        }finally {
            lock.unlock();
        }

    }

    public void bar(Runnable printBar) throws InterruptedException {
        try {
            lock.lock();
            for (int i = 0; i < n; i++) {
                if (atomicInteger.get()!=1){
                    bConditon.await();
                }
                // printBar.run() outputs "bar". Do not change or remove this line.
                printBar.run();
                atomicInteger.set(0);
                fConditon.signal();
            }
        }finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) {
        FooBar fooBar = new FooBar(3);
    }
}