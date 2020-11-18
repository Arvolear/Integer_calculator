package arvolear.calculator.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.input);
        input.setShowSoftInputOnFocus(false);

        configInput(input);

        EditText output = findViewById(R.id.output);
        output.setShowSoftInputOnFocus(false);

        controller = new Controller(this, input, output);

        findViewById(R.id.clear).setOnClickListener(controller);

        findViewById(R.id.erase).setOnTouchListener(controller);
        findViewById(R.id.erase).setOnClickListener(controller);
        findViewById(R.id.erase).setOnLongClickListener(controller);

        findViewById(R.id.copyAns).setOnClickListener(controller);
        findViewById(R.id.hideOrShow).setOnClickListener(controller);

        TableLayout TL = findViewById(R.id.tableLayout);

        for (int i = 0; i < TL.getChildCount(); i++)
        {
            TableRow TR = (TableRow) TL.getChildAt(i);

            for (int j = 0; j < TR.getChildCount(); j++)
            {
                View v = TR.getChildAt(j);

                v.setOnClickListener(controller);
            }
        }
    }

    private void configInput(final EditText input)
    {
        input.requestFocus();
        input.setGravity(Gravity.END);

        input.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                input.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (input.getLineCount() == 1)
                {
                    input.setGravity(Gravity.END);
                }
                else
                {
                    input.setGravity(Gravity.START);
                }
            }
        });
    }
}
