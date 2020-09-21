package arvolear.calculator.calculator;

import android.widget.Button;
import android.widget.EditText;

public class OutputBlocker extends InputOutputModifier implements Runnable
{
    private String dots;
    private Solver solver;

    OutputBlocker(MainActivity mainActivity, EditText input, EditText output)
    {
        super(mainActivity, input, output);
        this.solver = null;

        dots = "";
    }

    void setSolver(Solver solver)
    {
        this.solver = solver;
    }

    @Override
    public void run()
    {
        final Button equalsButton = mainActivity.findViewById(R.id.equals);

        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                equalsButton.setBackgroundResource(R.drawable.badequalsbutton);
            }
        });

        while (!solver.isReady())
        {
            dots = dots.length() > 3 ? "" : dots + ".";

            mainActivity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (!solver.isReady())
                    {
                        output.setText(dots);
                    }
                }
            });

            try
            {
                Thread.sleep(200);
            }
            catch (Exception ex)
            {
            }
        }

        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                equalsButton.setBackgroundResource(R.drawable.equalsbutton);
            }
        });
    }
}
