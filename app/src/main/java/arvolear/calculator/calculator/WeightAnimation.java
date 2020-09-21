package arvolear.calculator.calculator;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class WeightAnimation extends Animation
{
    private final float targetW;
    private final float startW;
    private LinearLayout.LayoutParams params;

    View view;

    WeightAnimation(View view, int duration, float startW, float targetW)
    {
        setDuration(duration);

        this.view = view;

        this.startW = startW;
        this.targetW = targetW;
        this.params = (LinearLayout.LayoutParams)view.getLayoutParams();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        float newWeight = (startW + targetW * interpolatedTime);

        view.setLayoutParams(new LinearLayout.LayoutParams(params.width, params.height, newWeight));

        view.requestLayout();
    }
}
