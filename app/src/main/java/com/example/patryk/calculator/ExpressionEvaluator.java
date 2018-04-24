package com.example.patryk.calculator;

import java.util.ArrayDeque;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * Created by Patryk on 2017-11-10.
 */

public class ExpressionEvaluator {

    public static final int MAX_DIGITS = 15;

    private ExpressionParser expressionParser = new ExpressionParser();

    /**
     * Evaluates mathematical expression.
     * @param expression presented in infix notation e.g. 1.34+2
     * @return Result of evaluated expression
     */
    public double evaluate(String expression){
        if("+—÷×.".indexOf(expression.charAt(expression.length() - 1)) != -1)
            expression = expression.substring(0,expression.length() - 1);
        ArrayDeque<String> postfixExpression = expressionParser.infixToPostfix(expression);
        Stack<Double> numbersToEvaluate = new Stack<>();
        while(!postfixExpression.isEmpty()){
            String token = postfixExpression.pop();
            if(CalculatorUtil.isNumber(token))
                numbersToEvaluate.push(Double.parseDouble(token));
            else{
                double num2 = numbersToEvaluate.pop();
                double num1 = numbersToEvaluate.pop();
                numbersToEvaluate.push(performOperation(num1,num2, token));
            }
        }
        return numbersToEvaluate.firstElement();
    }

    /**
     * Evaluates scientific expression.
     * @param operationButtonId to evaluate on input e.g. log, !, cos
     * @param input input on which operation will be performed
     * @return Result of evaluated expression
     */
    public String evaluateInPlace(int operationButtonId, String input) {
        StringBuilder inputSB = new StringBuilder(input);
        StringBuilder result = new StringBuilder();
        Character operator = null;
        if(CalculatorUtil.isFirstCharAnOperator(input)) {
            operator = inputSB.charAt(0);
            inputSB.deleteCharAt(0);
        }
        final Double inputAsDouble = Double.valueOf(inputSB.toString());
        switch(operationButtonId) {
            case R.id.buttonE:
                result.append(Math.E);
                break;
            case R.id.buttonPI:
                result.append(Math.PI);
                break;
            case R.id.buttonFactorial:
                try {
                    result.append(getFactorial(Integer.valueOf(inputSB.toString())));
                } catch (NumberFormatException e) {
                    result.append("Number must be integer.");
                } catch (ArithmeticException e) {
                    result.append("Result too big.");
                }
                break;
            case R.id.buttonPercent:
                result.append(inputAsDouble /100);
                break;
            case R.id.buttonToDeg:
                result.append(Math.toDegrees(inputAsDouble));
                break;
            case R.id.buttonToRad:
                result.append(Math.toRadians(inputAsDouble));
                break;
            case R.id.buttonLn:
                result.append(Math.log(inputAsDouble));
                break;
            case R.id.buttonLog10:
                result.append(Math.log10(inputAsDouble));
                break;
            case R.id.buttonSin:
                result.append(Math.sin(inputAsDouble));
                break;
            case R.id.buttonCos:
                result.append(Math.cos(inputAsDouble));
                break;
            case R.id.buttonTan:
                result.append(Math.tan(inputAsDouble));
                break;
            case R.id.buttonEToX:
                result.append(Math.exp(inputAsDouble));
                break;
            default:
                throw new IllegalArgumentException("Button with id: " + operationButtonId + " was not found.");
        }
        if(CalculatorUtil.isNumber(result.toString()) && operator != null)
            result.insert(0,operator);

            return result.toString();
    }

    private int getFactorial(int input) throws ArithmeticException {
        int sum = 1;
        if (input < 0) {
            sum = IntStream.range(input, 0).reduce(1, Math::multiplyExact);
        } else if (input > 0) {
            sum = IntStream.rangeClosed(1, input).reduce(1, Math::multiplyExact );
        }
        return sum;
    }

    private double performOperation(double num1, double num2, String operator) throws IllegalStateException{
        switch (operator){
            case "÷":
                return num1 / num2;
            case "×":
                return num1 * num2;
            case "+":
                return num1 + num2;
            case "—":
                return num1 - num2;
            default:
                throw new IllegalStateException();
        }
    }
}
