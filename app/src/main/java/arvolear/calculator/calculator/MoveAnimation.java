package arvolear.calculator.calculator;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MoveAnimation extends Animation
{
    private final int targetY;
    private final int targetX;
    private final int startX;
    private final int startY;

    View view;

    MoveAnimation(View view, int duration, int startX, int targetX, int startY, int targetY)
    {
        setDuration(duration);

        this.view = view;

        this.startX = startX;
        this.targetX = targetX;
        this.startY = startY;
        this.targetY = targetY;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        int newX = (int)(startX + targetX * interpolatedTime);
        int newY = (int)(startY + targetY * interpolatedTime);

        view.setX(newX);
        view.setY(newY);

        view.requestLayout();
    }
}