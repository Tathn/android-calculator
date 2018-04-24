package com.example.patryk.calculator;

import android.app.Activity;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

public class Calculator extends Activity implements StandardKeyboardFragment.OnButtonClickListener, ScientificKeyboardFragment.OnButtonClickListener {

    private final int minTabletModeWidth = 800;
    private final int minTabletModeHeight = 1024;
    private TextView input;
    private TextView formula;
    private TextView result;
    private ExpressionEvaluator expressionEvaluator;
    private ScientificKeyboardFragment scientificKeyboardFragment;
    private StandardKeyboardFragment standardKeyboardFragment;
    private boolean hasTabletModeWidth;
    private boolean hasTabletModeHeight;

    private final TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String formulaStr = formula.getText().toString();
            String editableStr = editable.toString();
            if (editableStr.isEmpty() || formulaStr.isEmpty()) {
                result.setText("");
            } else if (editableStr.contains("NaN")) {
                result.setText("NaN");
            } else if (editableStr.contains("Infinity")) {
                result.setText("Infinity");
            } else {
                    double expressionResult = expressionEvaluator.evaluate(formulaStr + editableStr);
                    result.setText("= " + formatExpression(expressionResult));
            }
        }
    };

    private final Editable.Factory inputEditableFactory = new Editable.Factory() {
        @Override
        public Editable newEditable(CharSequence source) {
            return new ExpressionBuilder(source);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowHeight = displayMetrics.heightPixels;
        int windowWidth = displayMetrics.widthPixels;
        boolean isPortraitMode = windowHeight > windowWidth;
        hasTabletModeWidth = windowWidth >= minTabletModeWidth;
        hasTabletModeHeight = windowHeight >= minTabletModeHeight;

        if(hasTabletModeHeight && isPortraitMode)
            setContentView(R.layout.activity_main_tablet_vertical);
        else if(hasTabletModeWidth && !isPortraitMode)
            setContentView(R.layout.activity_main_tablet_horizontal);
        else
            setContentView(R.layout.activity_main);

        input = (TextView) findViewById(R.id.currentInput);
        input.setEditableFactory(inputEditableFactory);
        input.addTextChangedListener(inputTextWatcher);
        formula = (TextView) findViewById(R.id.formula);
        formula.setMovementMethod(new ScrollingMovementMethod());
        result = (TextView) findViewById(R.id.result);
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowHeight / 16);
        formula.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowHeight / 26);
        result.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowHeight / 21);

        if(savedInstanceState != null) {
            String inputStr = savedInstanceState.getString("input", "0");
            String formulaStr = savedInstanceState.getString("formula", "");
            String resultStr = savedInstanceState.getString("result", "");
            input.setText(inputStr);
            formula.setText(formulaStr);
            result.setText(resultStr);
        }

        expressionEvaluator = new ExpressionEvaluator();
        standardKeyboardFragment = new StandardKeyboardFragment();
        scientificKeyboardFragment = new ScientificKeyboardFragment();

        if(!hasTabletModeWidth && !hasTabletModeHeight) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.calculator_keyboard_wrapper,standardKeyboardFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("input", input.getText().toString());
        outState.putString("formula", formula.getText().toString());
        outState.putString("result", result.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!hasTabletModeWidth && !hasTabletModeHeight) {
            getMenuInflater().inflate(R.menu.calculator_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_mode:
                boolean isChecked = item.isChecked();
                if(isChecked) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.calculator_keyboard_wrapper, standardKeyboardFragment)
                            .commit();
                    item.setIcon(R.drawable.science);
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.calculator_keyboard_wrapper, scientificKeyboardFragment)
                            .commit();
                    item.setIcon(R.drawable.calculator);
                }
                item.setChecked(!isChecked);
                return true;
            default:
                return false;
        }
    }

    public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.buttonEquals:
                onEquals();
                break;
            case R.id.buttonDelete:
                onDelete();
                break;
            case R.id.buttonClear:
                onClear();
                break;
            case R.id.buttonChangeSign:
                onChangeSign();
                break;
            case R.id.buttonAdd:
            case R.id.buttonSubtract:
            case R.id.buttonDivide:
            case R.id.buttonMultiply:
                onOperation(view);
                break;
            case R.id.buttonE:
            case R.id.buttonPI:
                onScientificButtonPressed(view.getId(), input.getText().toString());
                break;
            case R.id.buttonFactorial:
            case R.id.buttonPercent:
            case R.id.buttonToDeg:
            case R.id.buttonToRad:
            case R.id.buttonLn:
            case R.id.buttonLog10:
            case R.id.buttonSin:
            case R.id.buttonCos:
            case R.id.buttonTan:
            case R.id.buttonEToX:
                String inputStr = input.getText().toString();
                if(     inputStr.length() > 1 && CalculatorUtil.isFirstCharAnOperator(inputStr) ||
                        inputStr.length() > 0 && !CalculatorUtil.isFirstCharAnOperator(inputStr)) {
                    onScientificButtonPressed(view.getId(), inputStr);
                }
                break;
            default:
                input.append(((Button) view).getText().toString());
                break;
        }
    }

    private void onScientificButtonPressed(int viewId, String inputStr) {
        String result = expressionEvaluator.evaluateInPlace(viewId, inputStr);
        String resultWithoutOperator = CalculatorUtil.isFirstCharAnOperator(result) ? result.substring(1) : result;
        if(CalculatorUtil.isNumber(resultWithoutOperator))
            input.setText(result);
        else
            this.result.setText(resultWithoutOperator);
    }

    private void onEquals(){
        input.setText(input.getText());
        String resultStr = result.getText().toString();
        if(!resultStr.isEmpty()){
            formula.setText("");
            input.setText(resultStr.replaceAll("[∞= ]", ""));
        }
    }

    private void onDelete(){
        String newInputText = "";
        if(input != null && input.length() > 0){
            String inputText = input.getText().toString();
            boolean doDeleteSign = inputText.length() == 2 && inputText.charAt(0) == '-' || inputText.length() == 3 && inputText.charAt(1) == '-';
            newInputText = inputText.substring(0, inputText.length() - (doDeleteSign ? 2 : 1));
        }
        if(newInputText.length() == 0 && formula.length() > 0){
            ArrayList<String> formulaElements = new ArrayList<>(Arrays.asList(formula.getText().toString().split("(?=\\+|—|÷|×)")));
            newInputText = formulaElements.get(formulaElements.size() - 1);
            formulaElements.remove(formulaElements.size() - 1);
            formula.setText("");
            for(String operation : formulaElements)
                formula.append(operation);
        }
        input.setText(newInputText);
    }

    private void onClear(){
        formula.setText("");
        input.setText("0");
    }

    private void onChangeSign(){
        if(input.getText().length() == 0)
            return;
        Double alteredValue = null;
        StringBuilder sign = new StringBuilder("");
        if(formula.getText().length() == 0)
            alteredValue = -Double.parseDouble(input.getText().toString());
        else if(input.length() > 1){
            sign.append(input.getText().charAt(0));
            alteredValue = -Double.parseDouble(input.getText().toString().substring(1));
        }
        if(alteredValue != null)
        input.setText(sign + formatExpression(alteredValue));
    }

    private String formatExpression(double num){
        StringBuilder decimalFormatPattern = new StringBuilder("#.");
        for (int i = 0; i < ExpressionEvaluator.MAX_DIGITS; i++) {
            decimalFormatPattern.append("#");
        }
        return new DecimalFormat(decimalFormatPattern.toString()).format(num);
    }

    private void onOperation(View view){
        if(input.getText().length() == 0 && formula.length() == 0)
            return;
        String inputStr = input.getText().toString();
        boolean hasOperator = CalculatorUtil.isFirstCharAnOperator(inputStr);
        if(!hasOperator || inputStr.length() > 1) {
            double inputValue = Double.parseDouble(inputStr.substring(hasOperator ? 1 : 0));
            formula.append((hasOperator ? String.valueOf(inputStr.charAt(0)) : "") + formatExpression(inputValue));
        }
        input.removeTextChangedListener(inputTextWatcher);
        input.setText(((Button) view).getText());
        input.addTextChangedListener(inputTextWatcher);
    }
}
