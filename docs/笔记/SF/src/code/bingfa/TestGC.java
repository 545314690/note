package code.bingfa;

/**
 * @Author lisenmiao
 * @Date 2021/1/15 17:08
 */
public class TestGC {

    public static void main(String[] args) {
        while (true){
            try {
                Thread.sleep(10000);
                System.gc();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
