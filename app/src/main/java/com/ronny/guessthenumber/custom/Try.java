package com.ronny.guessthenumber.custom;

import java.util.Arrays;
import java.util.Collections;

public class Try {
    public int[] DIGITS;
    int[] HIDDEN;
    public Integer[] result;
    public int num;
    public Try(int[] digits, int[] target, int tryNum){
        num = tryNum;
        DIGITS = digits;
        HIDDEN = target;
        result = new Integer[digits.length];
        for(int i = 0; i < digits.length; i++)
            result[i] = contains(DIGITS[i], HIDDEN) ? 1 : 0;
        for(int i = 0; i < digits.length; i++)
            result[i] = DIGITS[i] == HIDDEN[i] ? 2 : result[i];
        Arrays.sort(result, Collections.reverseOrder());
    }
    private static boolean contains(int c, int[] p){
        for(int a:p){
            if(c == a)
                return true;
        }
        return false;
    }
}
