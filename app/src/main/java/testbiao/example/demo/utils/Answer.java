package testbiao.example.demo.utils;

/**
 * Created by szylover on 2017/5/18.
 */

public class Answer {
    private static int answer = -2;

    public static void  inital(){
        answer = -2;
    }

    public static void set(int x){
        answer = x;
    }

    public static int get(){
        return answer;
    }
}
