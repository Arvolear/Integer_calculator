package arvolear.calculator.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener
{
    private MainActivity mainActivity;
    private Adder adder;
    private Solver solver;
    private Eraser eraser;

    private OutputShower outputShower;
    private OutputBlocker outputBlocker;

    private ExecutorService service;

    private EditText input;
    private EditText output;

    Controller(MainActivity mainActivity, EditText input, EditText output)
    {
        this.mainActivity = mainActivity;

        adder = new Adder(mainActivity, input, output);
        solver = null;
        eraser = new Eraser(mainActivity, input, output);

        outputShower = new OutputShower(mainActivity);

        outputBlocker = new OutputBlocker(mainActivity, input, output);

        service = null;

        this.input = input;
        this.output = output;
    }

    void copyAns()
    {
        ClipboardManager clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("answer", output.getText().toString());

        clipboard.setPrimaryClip(clip);

        Toast.makeText(mainActivity, "Copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.zero:
            case R.id.one:
            case R.id.two:
            case R.id.three:
            case R.id.four:
            case R.id.five:
            case R.id.six:
            case R.id.seven:
            case R.id.eight:
            case R.id.nine:

            case R.id.add:
            case R.id.subtract:
            case R.id.multiply:
            case R.id.divide:
            case R.id.power:
            case R.id.modulo:

            case R.id.braces:
            {
                Button b = (Button) v;
                adder.add(b.getText().toString());

                break;
            }

            case R.id.clear:
            {
                adder.clear();
                break;
            }

            case R.id.equals:
            {
                if (solver != null && !solver.isReady())
                {
                    solver.stopShowAnswer();
                    service.shutdownNow();
                }

                solver = new Solver(mainActivity, input, output);
                outputBlocker.setSolver(solver);

                service = Executors.newSingleThreadExecutor();

                service.submit(solver);
                service.shutdown();

                try
                {
                    if (!service.awaitTermination(400, TimeUnit.MILLISECONDS))
                    {
                        Thread blockOutput = new Thread(outputBlocker);
                        blockOutput.start();
                    }
                }
                catch (InterruptedException e) { }

                break;
            }

            case R.id.negate:
            {
                adder.negate();
                break;
            }

            case R.id.copyAns:
            {
                copyAns();
                break;
            }

            ////////////////////

            case R.id.hideOrShow:
            {
                outputShower.toggle();
                break;
            }

            ////////////////////

            case R.id.erase:
            {
                if (!eraser.isKeepErasing())
                {
                    eraser.erase();
                }

                eraser.stopErasing();
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v)
    {
        switch (v.getId())
        {
            case R.id.erase:
            {
                Thread letsErase = new Thread(eraser);
                eraser.startErasing();
                letsErase.start();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (v.getId())
        {
            case R.id.erase:
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    eraser.stopErasing();
                }
            }
        }

        return false;
    }
}
