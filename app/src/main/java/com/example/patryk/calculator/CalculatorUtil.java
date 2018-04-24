package com.example.patryk.calculator;

/**
 * Created by Patryk on 2017-11-15.
 */

public class CalculatorUtil {

    public static boolean isFirstCharAnOperator(String text){
        return "+—÷×".indexOf(text.charAt(0)) != -1;
    }

    public static boolean isNumber(String text){
        return text.matches("-?[0-9]+\\.?([0-9]+)?");
    }
}
