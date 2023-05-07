package code.sl.s1195;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 * 1195. 交替打印字符串
 * 编写一个可以从 1 到 n 输出代表这个数字的字符串的程序，但是：
 *
 * 如果这个数字可以被 3 整除，输出 "fizz"。
 * 如果这个数字可以被 5 整除，输出 "buzz"。
 * 如果这个数字可以同时被 3 和 5 整除，输出 "fizzbuzz"。
 * 例如，当 n = 15，输出： 1, 2, fizz, 4, buzz, fizz, 7, 8, fizz, buzz, 11, fizz, 13, 14, fizzbuzz。
 *
 * 假设有这么一个类：
 *
 * class FizzBuzz {
 *   public FizzBuzz(int n) { ... }               // constructor
 *   public void fizz(printFizz) { ... }          // only output "fizz"
 *   public void buzz(printBuzz) { ... }          // only output "buzz"
 *   public void fizzbuzz(printFizzBuzz) { ... }  // only output "fizzbuzz"
 *   public void number(printNumber) { ... }      // only output the numbers
 * }
 * 请你实现一个有四个线程的多线程版  FizzBuzz， 同一个 FizzBuzz 实例会被如下四个线程使用：
 *
 * 线程A将调用 fizz() 来判断是否能被 3 整除，如果可以，则输出 fizz。
 * 线程B将调用 buzz() 来判断是否能被 5 整除，如果可以，则输出 buzz。
 * 线程C将调用 fizzbuzz() 来判断是否同时能被 3 和 5 整除，如果可以，则输出 fizzbuzz。
 * 线程D将调用 number() 来实现输出既不能被 3 整除也不能被 5 整除的数字。
 */
class FizzBuzz {
    private int n;

    public FizzBuzz(int n) {
        this.n = n;
    }
    volatile int a = 0 ;
    // printFizz.run() outputs "fizz".
    Lock lock = new ReentrantLock();
    Condition fizzConditon = lock.newCondition();
    Condition buzzConditon = lock.newCondition();
    Condition fizzBuzzConditon = lock.newCondition();
    Condition intConditon = lock.newCondition();
    public void fizz(Runnable printFizz) throws InterruptedException {
        try {
            lock.lock();
            for (a = 1; a <=n; ) {
                if(!(a%3==0)){
                    fizzConditon.await();
                }
//                printFizz.run();
                System.out.println("fizz");
                a++;
                checkCondition(a);
                if(a>n|| n-a<3){
                    fizzConditon.signalAll();
                    break;
                }
            }
        }finally {
            lock.unlock();
        }

    }
    private void checkCondition(int a){
        if(a%3==0 && a%5==0){
            fizzBuzzConditon.signal();
        }else if(a%3==0){
            fizzConditon.signal();
        }else  if(a%5==0){
            buzzConditon.signal();
        }else   {
            intConditon.signal();
        }
    }
    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        try {
            lock.lock();
            for (a = 1; a <=n; ) {
                if (!(a%5==0)){
                    buzzConditon.await();
                }
//                printBuzz.run();
                System.out.println("buzz");
                a++;
                checkCondition(a);
                if(a>n|| n-a<5){
                    buzzConditon.signalAll();
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        try {
            lock.lock();
            for (a = 1; a <=n; ) {
                if(!(a%3==0&& a%5==0)){
                    fizzBuzzConditon.await();
                }
//                printFizzBuzz.run();
                System.out.println("FizzBuzz");
                a++;
                checkCondition(a);
                if(a>n|| n-a<15){
                    fizzBuzzConditon.signalAll();
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        try {
            lock.lock();
            for (a = 1; a <=n; ) {
                if(a%3==0 || a%5==0){
                    intConditon.await();
                }
//                printNumber.accept(a);
                System.out.println(a);
                a++;
                checkCondition(a);
                if(a>n){
                    intConditon.signalAll();
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        FizzBuzz fizzBuzz = new FizzBuzz(16);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fizzBuzz.fizz(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"fizz").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fizzBuzz.buzz(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"buzz").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fizzBuzz.fizzbuzz(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"fizzbuzz").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fizzBuzz.number(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"number").start();
    }
}