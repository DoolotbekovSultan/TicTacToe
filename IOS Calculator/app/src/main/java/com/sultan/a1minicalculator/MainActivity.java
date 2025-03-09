package com.sultan.a1minicalculator;

import static com.sultan.a1minicalculator.Methods.*;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ATTRIBUTES

    // Result view
    private TextView result;

    // Operation elements
    private Double firstOperand = 0d;
    private Double secondOperand = null;
    private Operator operator = null;

    // Other
    private Button[] operationButtons;
    private int countOfFloatingZero = 0;
    private Button lastOperator;
    private boolean haveComma = false;
    private boolean endsWithComma = false;

    // METHODS

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // result view initialization
        result = findViewById(R.id.result);

        // Variables

        // number format constants
        final String floatFormat = "#.#########";
        final String integerFormat = "#";

        // Buttons

        // digits
        Button[] digitButtons = {findViewById(R.id.one_button), findViewById(R.id.two_button), findViewById(R.id.three_button),
                findViewById(R.id.zero_button), findViewById(R.id.four_button), findViewById(R.id.five_button), findViewById(R.id.six_button),
                findViewById(R.id.seven_button), findViewById(R.id.eight_button), findViewById(R.id.nine_button)};
        // operations
        operationButtons = new Button[]{findViewById(R.id.division_button), findViewById(R.id.multiplication_button),
                findViewById(R.id.minus_button), findViewById(R.id.plus_button)};

        // other
        Button commaButton = findViewById(R.id.comma_button);
        Button changeSignButton = findViewById(R.id.change_sign_button);
        Button clearButton = findViewById(R.id.clear_button);
        Button percentButton = findViewById(R.id.percent_button);
        Button equalButton = findViewById(R.id.equal_button);

        // Listeners

        // digits
        for (Button digitButton : digitButtons) {

            digitButton.setOnClickListener(view -> {

                String firstIntegerNumber = getDecimalFormatString(firstOperand, integerFormat);
                if ((firstIntegerNumber.length() == 10 && operator == null) ||
                        (secondOperand != null && getDecimalFormatString(secondOperand, integerFormat).length() == 10)) {
                    return;
                }

                Button button = (Button) view;
                String textOfButton = button.getText().toString();
                endsWithComma = false;

                if (operator == null) {
                    String fO = firstOperand.toString();
                    if (firstOperand % 1 != 0 || haveComma) {
                        if (textOfButton.equals("0") && haveComma) {
                            countOfFloatingZero ++;
                            updateInfo();
                            return;
                        }
                        String temp = fO;
                        if (firstOperand % 1 == 0) {
                            temp = fO.substring(0, fO.length() - 1);
                        }
                        firstOperand = Double.parseDouble((temp + "0".repeat(countOfFloatingZero) + textOfButton));
                        countOfFloatingZero = 0;
                    } else {
                        firstOperand = Double.parseDouble(firstOperand.intValue() + button.getText().toString());
                    }
                } else {
                    if (secondOperand == null) {
                        changeColorToNormalAllOperationButtons();
                        secondOperand = 0d;
                    }
                    String sO = secondOperand.toString();
                    if (secondOperand % 1 != 0 || haveComma) {
                        if (textOfButton.equals("0") && haveComma) {
                            countOfFloatingZero ++;
                            updateInfo();
                            return;
                        }
                        String temp = sO;
                        if (secondOperand % 1 == 0) {
                            temp = sO.substring(0, sO.length() - 1);
                        }
                        secondOperand = Double.parseDouble(temp + "0".repeat(countOfFloatingZero) + textOfButton);
                        countOfFloatingZero = 0;
                    } else {
                        secondOperand = Double.parseDouble(secondOperand.intValue() + button.getText().toString());
                    }
                }

                updateInfo();

            });

        }

        // operations
        for (Button operationButton : operationButtons) {

            operationButton.setOnClickListener(view -> {

                Button button = (Button) view;
                lastOperator = button;
                changeColorOfOperationButton(button);
                Operator operatorOfButton = getOperator(button.getText().toString());

                if (operator != null && secondOperand != null) {
                    calculate();
                    updateInfo();
                }

                countOfFloatingZero = 0;
                haveComma = false;
                operator = operatorOfButton;

            });

        }

        // other
        commaButton.setOnClickListener(view -> {

            Double lastNumber = getLastNumber();
            String integerNumber = getDecimalFormatString(lastNumber, integerFormat);

            if (integerNumber.length() > 7) {
                return;
            }
            String firstNumber = getDecimalFormatString(firstOperand, floatFormat);
            String secondNumber = secondOperand != null ? getDecimalFormatString(secondOperand, floatFormat) : "0";

            firstNumber = firstNumber.replace(",", ".");
            secondNumber = secondNumber.replace(",", ".");

            if (operator == null && !firstNumber.contains(".")) {
                firstOperand = Double.parseDouble(firstNumber + ".");
                endsWithComma = true;
                haveComma = true;
            } else if (operator != null && !secondNumber.contains(".")) {
                secondOperand = Double.parseDouble(secondNumber + ".");
                endsWithComma = true;
                haveComma = true;
            } else {
                return;
            }

            updateInfo();

        });

        changeSignButton.setOnClickListener(view -> {

            if (secondOperand != null) {
                secondOperand = -secondOperand;
            } else {
                firstOperand = -firstOperand;
            }

            updateInfo();

        });

        clearButton.setOnClickListener(view -> {

            clear();
            updateInfo();

        });

        percentButton.setOnClickListener(view -> {

            if (operator != null && secondOperand != null) {
                switch (operator) {
                    case MULTIPLICATION:
                        secondOperand /= 100;
                        break;
                    case DIVISION:
                        Double temp = (firstOperand / secondOperand) * 100;
                        secondOperand = firstOperand / temp;
                        break;
                    case PLUS:
                    case MINUS:
                        secondOperand /= 100;
                        secondOperand *= firstOperand;
                        break;
                    default:
                        throw new RuntimeException("Invalid operator!");
                }
            } else {
                firstOperand /= 100;
            }

            updateInfo();

        });

        equalButton.setOnClickListener(view -> {

            if (operator != null && secondOperand != null) {
                calculate();
                changeColorToNormalAllOperationButtons();
                updateInfo();
            }

        });

    }


    private Double getLastNumber() {

        if (secondOperand != null) {
            return secondOperand;
        } else {
            return firstOperand;
        }

    }

    private void changeColorOfOperationButton(Button button) {

        changeColorToNormalAllOperationButtons();

        ColorStateList white = ContextCompat.getColorStateList(this, R.color.white);
        ColorStateList orange = ContextCompat.getColorStateList(this, R.color.orange);

        button.setBackgroundTintList(white);
        button.setTextColor(orange);

    }

    private void changeColorToNormalAllOperationButtons() {

        ColorStateList white = ContextCompat.getColorStateList(this, R.color.white);
        ColorStateList orange = ContextCompat.getColorStateList(this, R.color.orange);

        for (Button btn : operationButtons) {
            btn.setBackgroundTintList(orange);
            btn.setTextColor(white);
        }

    }

    private Operator getOperator(String stringOperator) {

        switch (stringOperator) {
            case "÷":
                return Operator.DIVISION;
            case "×":
                return Operator.MULTIPLICATION;
            case "-":
                return Operator.MINUS;
            case "+":
                return Operator.PLUS;
            default:
                throw new RuntimeException("Invalid operator!");
        }

    }

    private void calculate() {

        switch (operator) {
            case MULTIPLICATION:
                firstOperand *= secondOperand;
                break;
            case DIVISION:
                firstOperand /= secondOperand;
                break;
            case PLUS:
                firstOperand += secondOperand;
                break;
            case MINUS:
                firstOperand -= secondOperand;
                break;
            default:
                throw new RuntimeException("Invalid operator!");
        }

        operator = null;
        countOfFloatingZero = 0;
        haveComma = false;
        secondOperand = null;

    }

    // Clear methods

    private void clear() {

        if (getLastNumber() != 0d) {
            clearLast();
        } else {
            allClear();
        }

    }

    private void clearLast() {

        if (operator != null) {
            secondOperand = 0d;
            changeColorOfOperationButton(lastOperator);
        } else {
            allClear();
        }

        haveComma = false;
        countOfFloatingZero = 0;

    }

    private void allClear() {

        firstOperand = 0d;
        secondOperand = null;
        haveComma = false;
        operator = null;
        countOfFloatingZero = 0;
        changeColorToNormalAllOperationButtons();

    }

    private void adaptSize(String text) {

        int[] textSizes = {74, 65, 58, 52, 47, 43, 40};
        int numberOfDigits = text.length();

        if (6 < numberOfDigits && numberOfDigits < 14) {
            result.setTextSize(textSizes[numberOfDigits - 7]);
        } else if (numberOfDigits <= 6) {
            result.setTextSize(85);
        } else {
            result.setTextSize(40);
        }

    }

    private void updateInfo() {

        try {

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("#.#########", symbols);
            Double lastNumber = getLastNumber();
            String temp = df.format(lastNumber);
            String stringLastNumber = ((lastNumber % 1 == 0 && haveComma ? temp + "." : temp) + "0".repeat(countOfFloatingZero));
            stringLastNumber = getStringNumberWithUnderScope(stringLastNumber).replace(".", ",");

            adaptSize(stringLastNumber);

            Button clearButton = findViewById(R.id.clear_button);

            clearButton.setText(lastNumber != 0 ? R.string.clear : R.string.all_clear);

            stringLastNumber += endsWithComma ? "," : "";

            result.setText(stringLastNumber);

        } catch(Exception e) {

            String errorText = getString(R.string.error);
            if (errorText.length() == 5) {
                result.setTextSize(100);
            } else {
                adaptSize(errorText);
            }
            result.setText(getString(R.string.error));
            allClear();

        }

    }

}