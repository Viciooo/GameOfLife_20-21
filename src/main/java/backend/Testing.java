package backend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Testing {
    public static void main(String[] args) {
        TreeSet<Integer> test = new TreeSet<>();
        test.add(1);
        test.add(2);
        test.add(3);
        test.add(4);
        Iterator<Integer> i = test.iterator();
        int t = -2;
        int k = -1;
        while (i.hasNext()){
            if(t != k) {
                t = i.next();
                System.out.println(t);
                k = t;
                t = i.next();
            }

        }
    }
}
