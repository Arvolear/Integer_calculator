package arvolear.calculator.calculator;

import android.util.TypedValue;
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

    private EditText output;
    private Button hider;

    private boolean showed;

    private int duration;
    private float extentKey;
    private float extentOut;
    private float leftStride;

    OutputShower(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;

        this.buttonsLayout = mainActivity.findViewById(R.id.buttonsLayout);
        this.outputLayout = mainActivity.findViewById(R.id.outputLayout);
        this.tableLayout = mainActivity.findViewById(R.id.tableLayout);

        this.output = mainActivity.findViewById(R.id.output);
        this.hider = mainActivity.findViewById(R.id.hideOrShow);

        this.showed = true;

        this.duration = 800;
        this.extentKey = -1.37f;
        this.extentOut = -2.85f;
        this.leftStride = 400;
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

    private void startHide2(View view)
    {
        int pxTo = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftStride, mainActivity.getResources().getDisplayMetrics());

        view.startAnimation(new MoveAnimation(view,
                duration,
                (int)view.getX(),
                -pxTo,
                (int)view.getY(),
                0));
    }

    private void startShow2(View view)
    {
        int pxTo = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftStride, mainActivity.getResources().getDisplayMetrics());

        view.startAnimation(new MoveAnimation(view,
                duration,
                (int)view.getX(),
                pxTo,
                (int)view.getY(),
                0));
    }

    void toggle()
    {
        if (showed)
        {
            startHide(buttonsLayout, extentKey);
            startHide2(tableLayout);
            startShow(outputLayout, Math.max(extentOut, -(float)(output.getLineCount() * 0.25)));

            hider.setText("Show");
        }
        else
        {
            startShow(buttonsLayout, extentKey);
            startShow2(tableLayout);
            startHide(outputLayout, Math.max(extentOut, -(float)(output.getLineCount() * 0.25)));

            hider.setText("Hide");
        }

        showed = !showed;
    }
}
