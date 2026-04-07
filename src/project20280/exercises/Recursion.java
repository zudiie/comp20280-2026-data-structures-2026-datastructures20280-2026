package project20280.exercises;


import org.junit.rules.Stopwatch;

public class Recursion {

    public static int fibonacci(int n) {
        if (n == 0 || n == 1) {
            return 1;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    public static int tribonacci(int n) {
        if (n == 0 || n == 1) {
            return 0;
        } else if  (n == 2) {
            return 1;
        } else {
            return tribonacci(n - 1) + tribonacci(n - 2) + tribonacci(n - 3);
        }
    }

    public static int function91(int n) {
        if (n > 100) {
            return n - 10;
        } else {
            return function91(function91(n + 11));
        }
    }

    //converts decimal to binary
    public static void foo (int x){
        if (x / 2 == 0){
            System.out.print(x);
            return;
        }
        foo(x/2);
        System.out.print(x%2);
    }

    /* Q6 a)
    void printList(Node<E> head){
        if (n != null) {
            printList(n.next);
            sout(n.data)
        }
    }

     */

    public static void main(String[] args) {
//        long startTime = System.nanoTime();
        System.out.println(fibonacci(5));
        System.out.println(tribonacci(9));
        System.out.println(function91(87));
        foo(2468);

//        long endTime = System.nanoTime();
//        long elapsedTime = endTime - startTime;
//        System.out.println("Elapsed time: " + elapsedTime/1000000000 + " s");
    }
}
