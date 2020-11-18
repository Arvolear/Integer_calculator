package arvolear.calculator.calculator;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class OutputShower
{
    private MainActivity mainActivity;

    private LinearLayout buttonsLayout;
    private LinearLayout outputLayout;
    private TableLayout tableLayout;

    private int lineCount;

    private EditText output;
    private Button hider;

    private boolean showed;

    private int duration;
    private float extentBut;
    private float extentOut;
    private int leftStride;

    OutputShower(final MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        final LinearLayout mainLayout = mainActivity.findViewById(R.id.mainLayout);
        this.buttonsLayout = mainActivity.findViewById(R.id.buttonsLayout);
        this.outputLayout = mainActivity.findViewById(R.id.outputLayout);
        this.tableLayout = mainActivity.findViewById(R.id.tableLayout);

        this.output = mainActivity.findViewById(R.id.output);
        this.hider = mainActivity.findViewById(R.id.hideOrShow);

        this.showed = true;

        this.duration = 800;

        this.buttonsLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                leftStride = buttonsLayout.getWidth();
                extentBut = -((float)mainLayout.getHeight() / (float)buttonsLayout.getHeight()) / 1.22f;
                extentOut = extentBut * 2.7f;
            }
        });
    }

    private void startHide(View view, float extent)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();

        view.startAnimation(new WeightAnimation(view, duration, params.weight, extent));
    }

    private void startShow(View view, float extent)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();

        view.startAnimation(new WeightAnimation(view, duration, params.weight, -extent));
    }

    private void startHideCalcButtons(View view)
    {
        view.startAnimation(new MoveAnimation(view,
                duration,
                (int)view.getX(),
                -leftStride,
                (int)view.getY(),
                0));
    }

    private void startShowCalcButtons(View view)
    {
        view.startAnimation(new MoveAnimation(view,
                duration,
                (int)view.getX(),
                leftStride,
                (int)view.getY(),
                0));
    }

    void toggle()
    {
        if (showed)
        {
            lineCount = output.getLineCount();

            startHide(buttonsLayout, extentBut);
            startHideCalcButtons(tableLayout);
            startShow(outputLayout, Math.max(extentOut, -(float)(lineCount * 0.25)));

            hider.setText("Show");
        }
        else
        {
            startShow(buttonsLayout, extentBut);
            startShowCalcButtons(tableLayout);
            startHide(outputLayout, Math.max(extentOut, -(float)(lineCount * 0.25)));

            hider.setText("Hide");
        }

        showed = !showed;
    }
}
