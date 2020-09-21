package arvolear.calculator.calculator;

import android.widget.EditText;

public class Eraser extends InputOutputModifier implements Runnable
{
    private boolean keepErasing;

    Eraser(MainActivity mainActivity, EditText input, EditText output)
    {
        super(mainActivity, input, output);
        keepErasing = false;
    }

    void erase()
    {
        String to = input.getText().toString();

        int selBeg = input.getSelectionStart();
        int selEnd = selBeg;

        if (selBeg > 0)
        {
            String sub;

            // erase back zeros
            if (Character.toString(to.charAt(selBeg - 1)).matches("[0-9]"))
            {
                if (selBeg - 1 == 0 || Character.toString(to.charAt(selBeg - 2)).matches("[^0-9]"))
                {
                    int zeroCounter = 0;

                    // find right number != 0
                    for (int i = selBeg; i < to.length(); i++)
                    {
                        if (Character.toString(to.charAt(i)).matches("[1-9]"))
                        {
                            selEnd = i;
                            break;
                        }
                        else if (to.charAt(i) != '0')
                        {
                            if (zeroCounter != 0)
                            {
                                selEnd = i - 1;
                            }
                            else
                            {
                                selEnd = i;
                            }

                            break;
                        }

                        if (to.charAt(i) == '0')
                        {
                            zeroCounter++;
                        }

                        if (i == to.length() - 1)
                        {
                            selEnd = i;
                        }
                    }
                }
            }
            else if (selBeg > 1)
            {
                // erase front zeros
                if (Character.toString(to.charAt(selBeg - 1)).matches("[^0-9]"))
                {
                    if (selBeg < to.length() && Character.toString(to.charAt(selBeg)).matches("[0-9]"))
                    {
                        int charInd = -1;

                        // find left operator || ( || )
                        for (int i = selBeg - 2; i >= 0; i--)
                        {
                            if (Character.toString(to.charAt(i)).matches("[^0-9]"))
                            {
                                charInd = i;
                                break;
                            }
                        }

                        // if left operator + 1 == 0 -> erase the whole num
                        if (to.charAt(charInd + 1) == '0')
                        {
                            selBeg = charInd + 2;
                        }
                    }
                }
            }

            sub = to.substring(0, selBeg - 1);
            sub += to.substring(selEnd);

            setInputText(sub);
            setInputSelection(selBeg - 1);
        }
    }

    void startErasing()
    {
        keepErasing = true;
    }

    void stopErasing()
    {
        keepErasing = false;
    }

    boolean isKeepErasing()
    {
        return keepErasing;
    }

    @Override
    public void run()
    {
        while (keepErasing)
        {
            try
            {
                Thread.sleep(60);
            }
            catch (Exception ex)
            {
            }

            erase();
        }
    }
}
