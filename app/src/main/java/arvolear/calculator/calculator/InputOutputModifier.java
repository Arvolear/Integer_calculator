package arvolear.calculator.calculator;

import android.view.Gravity;
import android.widget.EditText;

public class InputOutputModifier
{
    protected MainActivity mainActivity;
    protected EditText input;
    protected EditText output;

    InputOutputModifier(MainActivity mainActivity, EditText input, EditText output)
    {
        this.mainActivity = mainActivity;
        this.input = input;
        this.output = output;
    }

    protected void setOutputText(final String text)
    {
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                output.setText(text);
            }
        });
    }

    protected void setInputText(final String text)
    {
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                input.setText(text);
            }
        });
    }

    protected void setInputSelection(final int selBeg)
    {
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                input.setSelection(selBeg);
            }
        });
    }
}
