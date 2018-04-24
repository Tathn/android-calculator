package com.example.patryk.calculator;

import java.util.ArrayDeque;
import java.util.Stack;

/**
 * Created by Patryk on 2017-11-05.
 */

public class ExpressionParser {

    /**
     * Uses Shunting-yard algorithm to convert mathematical expression represented in infix notation (1.34+2)
     * to it's equivalent in postfix notation (1.34,2,+)
     * @param expression presented in infix notation e.g. 1.34+2
     * @return ArrayDeque containing expression converted from infix to postfix notation e.g. [1.34, 2, +]
     * @see <a href="Shunting-yard algorithm">https://en.wikipedia.org/wiki/Shunting-yard_algorithm</a>
     */

    public ArrayDeque<String> infixToPostfix(String expression){
        Stack<String> operators = new Stack<>();
        ArrayDeque<String> output = new ArrayDeque<>();
        String[] splitExpression = expression.split("((?<=\\+|—|÷|×)|(?=\\+|—|÷|×))");
        for(String token : splitExpression){
            if(CalculatorUtil.isNumber(token))
                output.add(token);
            else if(operators.isEmpty() || getPrecedence(token) > getPrecedence(operators.peek()))
                operators.push(token);
            else {
                while(!operators.isEmpty() && getPrecedence(token) <= getPrecedence(operators.peek()))
                    output.add(operators.pop());
                operators.push(token);
            }
        }
        while(!operators.isEmpty())
            output.add(operators.pop());
        return output;
    }

    private int getPrecedence(String token){
        switch (token){
            case "÷":
            case "×":
                return 3;
            case "+":
            case "—":
                return 2;
            default:
                return 1;
        }
    }
}