package arvolear.calculator.calculator;

import android.widget.EditText;

import java.math.BigInteger;
import java.util.ArrayList;

public class Solver extends InputOutputModifier implements Runnable
{
    private boolean ready;

    Solver(MainActivity mainActivity, EditText input, EditText output)
    {
        super(mainActivity, input, output);

        ready = true;
    }

    @Override
    protected void setOutputText(final String text)
    {
        if (ready)
        {
            return;
        }

        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ready = true;
                output.setText(text);
            }
        });
    }

    private int priority(char symbol)
    {
        switch (symbol)
        {
            case 'N':
            {
                return 5;
            }

            case 'F':
            {
                return 4;
            }

            case '^':
            {
                return 3;
            }

            case '/':
            case '%':
            case '\u00D7':
            {
                return 2;
            }

            case '-':
            case '+':
            {
                return 1;
            }
        }

        return 0;
    }

    private BigInteger calculate(BigInteger a, BigInteger b, char op) throws Exception
    {
        switch (op)
        {
            case 'N':
            {
                return a.negate();
            }
            case 'F':
            {
                return factorial(a.intValue());
            }
            case '^':
            {
                if (b.compareTo(new BigInteger(Integer.toString(Integer.MAX_VALUE))) != -1)
                {
                    throw new Exception("Power is too big");
                }

                if (b.compareTo(new BigInteger(Integer.toString(Integer.MIN_VALUE))) != 1)
                {
                    throw new Exception("Power is too small");
                }

                if (a.equals(BigInteger.ZERO) && b.equals(BigInteger.ZERO))
                {
                    throw new Exception("0^0");
                }

                return a.pow(b.intValue());
            }
            case '/':
            {
                return a.divide(b);
            }
            case '%':
            {
                return a.mod(b);
            }
            case '\u00D7':
            {
                return a.multiply(b);
            }
            case '-':
            {
                return a.subtract(b);
            }
            case '+':
            {
                return a.add(b);
            }
        }

        return a;
    }

    private void solve()
    {
        ready = false;

        String expr = input.getText().toString();

        if (expr.isEmpty())
        {
            setOutputText("");
            return;
        }

        int closed = 0;
        int opened = 0;

        try
        {
            for (int i = 0; i < expr.length(); i++)
            {
                if (expr.charAt(i) == '(')
                {
                    opened++;
                }
                else if (expr.charAt(i) == ')')
                {
                    closed++;
                }

                if (i > 0)
                {
                    if (expr.charAt(i) == '(' && Character.toString(expr.charAt(i - 1)).matches("[0-9\\)]"))
                    {
                        throw new Exception("[0-9](");
                    }

                    if (expr.charAt(i) == ')' && expr.charAt(i - 1) == '(')
                    {
                        throw new Exception(")(");
                    }

                    if (expr.charAt(i - 1) == ')' && Character.toString(expr.charAt(i)).matches("[0-9\\(]"))
                    {
                        throw new Exception(")[0-9(]");
                    }

                    if (i < expr.length() - 1 && expr.charAt(i) == '!' && Character.toString(expr.charAt(i + 1)).matches("[0-9\\(!]"))
                    {
                        throw new Exception("![0-9(!]");
                    }
                }
            }

            if (opened < closed)
            {
                while (closed != opened)
                {
                    expr = "(" + expr;
                    opened++;
                }
            }

            while (closed != opened)
            {
                expr += ")";
                closed++;
            }

            ArrayList<BigInteger> values = new ArrayList<>();
            ArrayList<Character> ops = new ArrayList<>();

            for (int i = 0; i < expr.length(); i++)
            {
                if (expr.charAt(i) == ' ')
                {
                    continue;
                }

                if (expr.charAt(i) == '(')
                {
                    ops.add('(');
                }
                else if (Character.toString(expr.charAt(i)).matches("[0-9]"))
                {
                    StringBuilder val = new StringBuilder();

                    while (i < expr.length() && Character.toString(expr.charAt(i)).matches("[0-9]"))
                    {
                        val.append(expr.charAt(i));
                        i++;
                    }

                    i--;

                    BigInteger res = new BigInteger(val.toString());

                    while (!ops.isEmpty() && ops.get(ops.size() - 1) == 'N')
                    {
                        ops.remove(ops.size() - 1);
                        res = res.negate();
                    }

                    values.add(res);
                }
                else if (expr.charAt(i) == ')')
                {
                    while (!ops.isEmpty() && ops.get(ops.size() - 1) != '(')
                    {
                        char op = ops.get(ops.size() - 1);
                        ops.remove(ops.size() - 1);

                        if (op != 'F')
                        {
                            BigInteger right = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            BigInteger left = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            values.add(calculate(left, right, op));
                        }
                        else
                        {
                            BigInteger right = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            values.add(calculate(right, right, op));
                        }
                    }

                    BigInteger res;

                    res = values.get(values.size() - 1);
                    values.remove(values.size() - 1);

                    // remove brace
                    if (!ops.isEmpty())
                    {
                        ops.remove(ops.size() - 1);
                    }

                    while (!ops.isEmpty() && ops.get(ops.size() - 1) == 'N')
                    {
                        ops.remove(ops.size() - 1);
                        res = res.negate();
                    }

                    values.add(res);
                }
                // operator
                else
                {
                    if (expr.charAt(i) == '-' && (i == 0 || Character.toString(expr.charAt(i - 1)).matches("[^0-9\\)!F]")))
                    {
                        expr = expr.substring(0, i) + "N" + expr.substring(i + 1);
                    }

                    if (expr.charAt(i) == '!')
                    {
                        expr = expr.substring(0, i) + "F" + expr.substring(i + 1);
                    }

                    while (!ops.isEmpty() &&
                            priority(ops.get(ops.size() - 1)) >= priority(expr.charAt(i)) &&
                            ops.get(ops.size() - 1) != 'N')
                    {
                        char op = ops.get(ops.size() - 1);
                        ops.remove(ops.size() - 1);

                        if (op != 'F')
                        {
                            BigInteger right = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            BigInteger left = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            values.add(calculate(left, right, op));
                        }
                        else
                        {
                            BigInteger right = values.get(values.size() - 1);
                            values.remove(values.size() - 1);

                            values.add(calculate(right, right, op));
                        }
                    }

                    ops.add(expr.charAt(i));
                }
            }

            while (!ops.isEmpty())
            {
                char op = ops.get(ops.size() - 1);
                ops.remove(ops.size() - 1);

                if (op == 'N')
                {
                    BigInteger right = values.get(values.size() - 1);
                    values.remove(values.size() - 1);

                    values.add(calculate(right, right, op));
                }
                else if (op == 'F')
                {
                    BigInteger right = values.get(values.size() - 1);
                    values.remove(values.size() - 1);

                    values.add(calculate(right, right, op));
                }
                else
                {
                    BigInteger right = values.get(values.size() - 1);
                    values.remove(values.size() - 1);

                    BigInteger left = values.get(values.size() - 1);
                    values.remove(values.size() - 1);

                    values.add(calculate(left, right, op));
                }
            }

            if (!values.isEmpty())
            {
                setOutputText(values.get(values.size() - 1).toString());
            }
            else
            {
                setOutputText("Error");
            }
        }
        catch (Exception ex)
        {
            setOutputText("Error");
        }
    }

    private BigInteger factorial(int fact) throws Exception
    {
        if (fact < 0)
        {
            throw new Exception("factorial < 0");
        }

        BigInteger res = BigInteger.ONE;

        for (int i = fact; i >= 1; i--)
        {
            res = res.multiply(BigInteger.valueOf(i));
        }

        return res;
    }

    @Override
    public void run()
    {
        solve();
    }

    void stopShowAnswer()
    {
        ready = true;
    }

    boolean isReady()
    {
        return ready;
    }
}