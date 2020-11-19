package arvolear.calculator.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

        this.input = input;
        this.output = output;

        configTextSize();

        adder = new Adder(mainActivity, input, output);
        solver = null;
        eraser = new Eraser(mainActivity, input, output);

        outputShower = new OutputShower(mainActivity);

        outputBlocker = new OutputBlocker(mainActivity, input, output);

        service = null;
    }

    private void configTextSize()
    {
        final LinearLayout mainLayout = mainActivity.findViewById(R.id.mainLayout);

        mainLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                int inputTextSize = mainLayout.getWidth() / 24;
                int outputTextSize = mainLayout.getWidth() / 36;
                int clearTextSize = mainLayout.getWidth() / 36;
                int textTextSize = mainLayout.getWidth() / 72;
                int buttonTextSize = mainLayout.getWidth() / 43;

                int eraseTextSize = mainLayout.getHeight() / 48;

                input.setTextSize(Math.max(35, inputTextSize));
                output.setTextSize(Math.max(25, outputTextSize));

                ((Button) mainActivity.findViewById(R.id.clear)).setTextSize(Math.max(30, clearTextSize));
                ((Button) mainActivity.findViewById(R.id.hideOrShow)).setTextSize(Math.max(12, textTextSize));
                ((Button) mainActivity.findViewById(R.id.copyAns)).setTextSize(Math.max(12, textTextSize));
                ((Button) mainActivity.findViewById(R.id.erase)).setTextSize(Math.max(30, eraseTextSize));


                TableLayout table = mainActivity.findViewById(R.id.tableLayout);

                for (int i = 0; i < table.getChildCount(); i++)
                {
                    for (int j = 0; j < ((TableRow) table.getChildAt(i)).getChildCount(); j++)
                    {
                        ((Button) ((TableRow) table.getChildAt(i)).getChildAt(j)).setTextSize(Math.max(17, buttonTextSize));
                    }
                }
            }
        });
    }

    public void copyAns()
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

            case R.id.factorial:
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
                vibrate();
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
                catch (InterruptedException e)
                {
                }

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

    private void vibrate()
    {
        Vibrator v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            v.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else
        {
            v.vibrate(5);
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
