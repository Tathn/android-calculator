package com.example.patryk.calculator;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

/**
 * Created by Patryk on 2017-11-07.
 */

public class ExpressionBuilder extends SpannableStringBuilder{

    public ExpressionBuilder(CharSequence text){
        super(text);
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend){
        String appendExpr = tb.toString();
        String expr = toString();
        if(appendExpr.length() == 1){
            boolean containsSign =  expr.length() > 0 && CalculatorUtil.isFirstCharAnOperator(expr);
            switch (appendExpr.charAt(0)){
                case '.':
                    // don't allow two decimals in the same number
                    final int index = expr.lastIndexOf('.');
                    if (index != -1 && TextUtils.isDigitsOnly(expr.substring(index + 1, start))) {
                        appendExpr = "";
                    }
                    if(expr.length() == 1 && containsSign ||
                            expr.isEmpty()){
                        appendExpr = "0.";
                    }
                    break;
                default:
                    if(expr.length() == 1 && expr.charAt(0) == '0'){
                        delete(0,1);
                        start -= 1;
                        end -= 1;
                    }
                    if(expr.length() == 2 && expr.charAt(1) == '0' && containsSign){
                        delete(1,2);
                        start -= 1;
                        end -= 1;
                    }
                    boolean exceedsPrecisionLimit = expr.length() - (containsSign ? 1 : 0) - (expr.contains(".") ? 1 : 0) >= ExpressionEvaluator.MAX_DIGITS;
                    if(exceedsPrecisionLimit){
                        appendExpr = "";
                    }
            }
        }
        return super.replace(start, end, appendExpr, 0, appendExpr.length());
    }


}
