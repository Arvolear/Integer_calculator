package arvolear.calculator.calculator;

import android.widget.EditText;

public class Adder extends InputOutputModifier
{
    private class Pair
    {
        String out;
        boolean fine;
        int pos;

        Pair(String out, boolean fine, int pos)
        {
            this.out = out;
            this.fine = fine;
            this.pos = pos;
        }
    }

    Adder(MainActivity mainActivity, EditText input, EditText output)
    {
        super(mainActivity, input, output);
    }

    private Pair analyze(String to, String what, int pos)
    {
        if (what.matches("([1-9]([0-9]*))|0"))
        {
            int charInd = -1;

            // length == 0
            if (to.length() == 0)
            {
                return new Pair(to, true, pos);
            }

            // closed brace -> multiply and number
            if (pos > 0 && to.charAt(pos - 1) == ')')
            {
                String newTo = "";

                if (pos < to.length() && Character.toString(to.charAt(pos)).matches("[0-9]"))
                {
                    if (what.matches("0"))
                    {
                        newTo = to.substring(0, pos) + "\u00D7" + to.substring(pos);

                        return new Pair(newTo, false, pos + 1);
                    }
                }
                else if (pos < to.length() && to.charAt(pos) == '(')
                {
                    newTo = to.substring(0, pos) + "\u00D7\u00D7" + to.substring(pos);

                    return new Pair(newTo, true, pos + 1);
                }

                newTo = to.substring(0, pos) + "\u00D7" + to.substring(pos);

                return new Pair(newTo, true, pos + 1);
            }

            // braces after pos -> number and multiply
            if (pos < to.length() && to.charAt(pos) == '(')
            {
                int leftNonZero = -1;

                // find left operator
                for (int i = pos - 1; i >= 0; i--)
                {
                    if (Character.toString(to.charAt(i)).matches("[^0-9]"))
                    {
                        leftNonZero = i;
                        break;
                    }
                }

                String newTo = "";

                if (to.charAt(leftNonZero + 1) == '0')
                {
                    if (leftNonZero >= 0 && to.charAt(leftNonZero) == ')')
                    {
                        newTo = to.substring(0, leftNonZero + 1) + "\u00D7\u00D7" + to.substring(pos);
                    }
                    else
                    {
                        newTo = to.substring(0, leftNonZero + 1) + "\u00D7" + to.substring(pos);
                        pos--;
                    }
                }
                else
                {
                    newTo = to.substring(0, pos) + "\u00D7" + to.substring(pos);
                }

                return new Pair(newTo, true, pos);
            }

            // find left operator
            for (int i = pos - 1; i >= 0; i--)
            {
                if (Character.toString(to.charAt(i)).matches("[^0-9]"))
                {
                    charInd = i;
                    break;
                }
            }

            // string end
            if (charInd == to.length() - 1)
            {
                return new Pair(to, true, pos);
            }

            // just after the operator
            if (pos == charInd + 1)
            {
                // operator after the operator
                if (Character.toString(to.charAt(charInd + 1)).matches("[^0-9\\(]"))
                {
                    return new Pair(to, true, pos);
                }

                return new Pair(to, !what.equals("0"), pos);
            }

            // 0 after operator
            if (Character.toString(to.charAt(charInd + 1)).equals("0"))
            {
                String newTo = "";

                // if brace before zero -> insert mul and number
                if (charInd >= 0 && to.charAt(charInd) == ')')
                {
                    newTo = to.substring(0, charInd + 1) + "\u00D7" + to.substring(pos);

                    return new Pair(newTo, true, charInd + 2);
                }

                // swap zero with number
                newTo = to.substring(0, charInd + 1) + to.substring(pos);

                return new Pair(newTo, true, charInd + 1);
            }

            return new Pair(to, true, pos);
        }
        // operator
        else
        {
            if (what.matches("\\(  \\)"))
            {
                int nonZero = to.length();
                int zeroCounter = 0;

                // find right non zero
                for (int i = pos; i < to.length(); i++)
                {
                    if (Character.toString(to.charAt(i)).matches("[1-9]"))
                    {
                        nonZero = i;
                        break;
                    }
                    else if (to.charAt(i) != '0')
                    {
                        if (zeroCounter == 0)
                        {
                            nonZero = i;
                        }
                        else
                        {
                            nonZero = i - 1;
                        }

                        break;
                    }

                    if (to.charAt(i) == '0')
                    {
                        zeroCounter++;
                    }

                    if (i == to.length() - 1)
                    {
                        nonZero = i;
                    }
                }

                String newTo = to.substring(0, pos) + to.substring(nonZero);

                return new Pair(newTo, true, pos);
            }

            // zero length -> false
            if (to.length() == 0)
            {
                return new Pair(to, false, pos);
            }

            // zero pos -> false
            if (pos == 0)
            {
                return new Pair(to, false, pos);
            }

            // number before -> true
            if (Character.toString(to.charAt(pos - 1)).matches("[0-9\\)]"))
            {
                // operator after -> false
                if (pos < to.length() && Character.toString(to.charAt(pos)).matches("[^0-9\\(\\)]"))
                {
                    String newTo = to.substring(0, pos) + to.substring(pos + 1);

                    return new Pair(newTo, true, pos);
                }

                int nonZero = to.length();
                int zeroCounter = 0;

                // find right non zero
                for (int i = pos; i < to.length(); i++)
                {
                    if (Character.toString(to.charAt(i)).matches("[1-9]"))
                    {
                        nonZero = i;
                        break;
                    }
                    else if (to.charAt(i) != '0')
                    {
                        if (zeroCounter == 0)
                        {
                            nonZero = i;
                        }
                        else
                        {
                            nonZero = i - 1;
                        }

                        break;
                    }

                    if (to.charAt(i) == '0')
                    {
                        zeroCounter++;
                    }

                    if (i == to.length() - 1)
                    {
                        nonZero = i;
                    }
                }

                String newTo = to.substring(0, pos) + to.substring(nonZero);

                return new Pair(newTo, true, pos);
            }
        }

        // else -> false
        return new Pair(to, false, pos);
    }

    private String addBrace(String to, int pos)
    {
        int beforeOpened = 0;

        for (int i = 0; i < to.length(); i++)
        {
            if (to.charAt(i) == '(')
            {
                if (i < pos)
                {
                    beforeOpened++;
                }
            }
            else if (to.charAt(i) == ')')
            {
                if (i < pos)
                {
                    beforeOpened--;
                }
            }
        }

        if (pos == 0)
        {
            return "(";
        }

        if (beforeOpened == 0)
        {
            if (to.charAt(pos - 1) == ')' || Character.toString(to.charAt(pos - 1)).matches("[0-9]"))
            {
                return "\u00D7(";
            }
            else
            {
                return "(";
            }
        }
        else
        {
            if (to.charAt(pos - 1) == ')' || Character.toString(to.charAt(pos - 1)).matches("[0-9]"))
            {
                if (pos < to.length() && Character.toString(to.charAt(pos)).matches("[0-9]"))
                {
                    return ")\u00D7";
                }

                return ")";
            }
            else
            {
                return "(";
            }
        }
    }

    void add(String symbols)
    {
        String to = input.getText().toString();
        int sel = input.getSelectionStart();

        int offset = 0;

        Pair out = analyze(to, symbols, sel);

        to = out.out;
        sel = out.pos;
        String sub = to.substring(0, sel);

        if (out.fine)
        {
            int before = sub.length();

            if (symbols.matches("\\(  \\)"))
            {
                sub += addBrace(to, sel);
            }
            else
            {
                sub += symbols;
            }

            offset += sub.length() - before;
        }

        to = sub + to.substring(sel);

        setInputText(to);
        setInputSelection(sel + offset);
    }

    void negate()
    {
        String to = input.getText().toString();

        int selBeg = input.getSelectionStart();

        int leftOperator = -1;

        // find left operator
        for (int i = selBeg - 1; i >= 0; i--)
        {
            if (Character.toString(to.charAt(i)).matches("[^0-9]"))
            {
                leftOperator = i;
                break;
            }
        }

        int offset = 0;

        if (leftOperator == -1 || to.charAt(leftOperator) == '(')
        {
            to = to.substring(0, leftOperator + 1) + "-" + to.substring(leftOperator + 1);
            offset = 1;
        }
        else if (to.charAt(leftOperator) == ')')
        {
            to = to.substring(0, leftOperator + 1) + "\u00D7(-" + to.substring(leftOperator + 1);
            offset = 3;
        }
        else if (to.charAt(leftOperator) == '-')
        {
            if (leftOperator > 0)
            {
                if (Character.toString(to.charAt(leftOperator - 1)).matches("[0-9\\)]"))
                {
                    to = to.substring(0, leftOperator + 1) + "(-" + to.substring(leftOperator + 1);
                    offset = 2;
                }
                else if (to.charAt(leftOperator - 1) == '(')
                {
                    to = to.substring(0, leftOperator - 1) + to.substring(leftOperator + 1);
                    offset = -2;
                }
                else
                {
                    to = to.substring(0, leftOperator) + to.substring(leftOperator + 1);
                    offset = -1;
                }
            }
            else
            {
                to = to.substring(0, leftOperator) + to.substring(leftOperator + 1);
                offset = -1;
            }
        }
        else
        {
            to = to.substring(0, leftOperator + 1) + "(-" + to.substring(leftOperator + 1);
            offset = 2;
        }

        setInputText(to);
        setInputSelection(selBeg + offset);
    }

    void clear()
    {
        setInputText("");
        setOutputText("");
    }
}
