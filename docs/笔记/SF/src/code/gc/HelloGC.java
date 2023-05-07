package code.gc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lisenmiao
 * @Date 2021/1/14 13:44
 */
public class HelloGC {
    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        while (true){
            list.add(new Object());
        }
    }
}
