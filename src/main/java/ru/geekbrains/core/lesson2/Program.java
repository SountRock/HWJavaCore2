package ru.geekbrains.core.lesson2;

import java.util.Arrays;

public class Program {

    public static void main(String[] args) {
        CrestZero crestZero = new CrestZero(5, 4);
        crestZero.game();

        //String[] arr = {"0", "X", "0", "X", "X"};
        //System.out.println(Arrays.toString(search(arr, 3)));
    }
    /*
    static int[] search(String[] arr, int val){
        int count = 0;
        int[] pos = null;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals("X")){
                count++;
            } else {
                pos = new int[]{i};
            }
            if(count >= val){
                return pos;
            }
        }

        return pos;
    }
     */

}
