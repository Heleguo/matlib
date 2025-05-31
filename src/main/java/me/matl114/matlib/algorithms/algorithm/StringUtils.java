package me.matl114.matlib.algorithms.algorithm;

import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final int BIG_LETTER = 26;
    private static final int LETTER = 52;
    private static final int LETTER_AND_NUM = 62;
    private static final int ALL = CHARACTERS.length();
    private static final Random rand = new Random();
    private static String randStr(int len, int range){
        var sb = new StringBuilder();
        for (int i=0; i<len; ++i){
            sb.append(CHARACTERS.charAt(rand.nextInt(0, range)));
        }
        return sb.toString();
    }
    public static String randCapLetter(int len){
        return randStr(len, BIG_LETTER);
    }
    public static String randLetter(int len){
        return randStr(len, LETTER);
    }
    public static String randString(int len){
        return randStr(len, LETTER_AND_NUM);
    }
    public static String randComplex(int len){
        return randStr(len, ALL);
    }

    public static String replaceSub(String a1, int index1, int index2, String n){
        if(index1 > index2){
            return replaceSub(a1, index2, index1, n);
        }
        return new StringBuilder(a1.substring(0, index1)).append(n).append(a1.substring(index2)).toString();
    }

    public static String replaceAll(String a1, Pattern pattern, Function<String, String> replacingFunction){
        StringBuilder builder = new StringBuilder();
        int currentIndex = 0 ;
        Matcher matcher = pattern.matcher(a1);
        while (matcher.find()){
            int beginIndex = matcher.start();
            int endIndex = matcher.end();

            String pat = a1.substring(beginIndex, endIndex);
            String result = replacingFunction.apply(pat);
            if(result != null){
                builder.append(a1, currentIndex, beginIndex);
                builder.append(result);
            }else {
                builder.append(a1, currentIndex, endIndex);
            }
            currentIndex = endIndex;
        }
        int size = a1.length();
        if(currentIndex < size){
            builder.append(a1, currentIndex, size);
        }
        return builder.toString();
    }
}
