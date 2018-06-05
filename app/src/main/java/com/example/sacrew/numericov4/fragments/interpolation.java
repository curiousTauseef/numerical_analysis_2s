package com.example.sacrew.numericov4.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.sacrew.numericov4.R;
import com.example.sacrew.numericov4.fragments.interpolationFragments.lagrange;
import com.example.sacrew.numericov4.fragments.interpolationFragments.newtonInterpolator;
import com.example.sacrew.numericov4.fragments.interpolationFragments.splineCuadratico;
import com.example.sacrew.numericov4.fragments.interpolationFragments.splineCubico;
import com.example.sacrew.numericov4.fragments.interpolationFragments.splineLineal;
import com.example.sacrew.numericov4.graphUtils;
import com.example.sacrew.numericov4.pagerAdapter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class interpolation extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static TableLayout vectors;
    private int count = 3;
    private graphUtils graphUtils = new graphUtils();
    public static GraphView interpolationGraph;
    private HashMap<EditText, Pair<PointsGraphSeries<DataPoint>,Integer>> viewToPoint = new HashMap<>();
    private List<Integer> poolColors = new LinkedList<>();
    public interpolation() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interpolation, container, false);
        //EvalEngine.DOUBLE_PRECISION = 30;

      /*  ExprEvaluator util = new ExprEvaluator(false, 100);
        String javaForm = util.toJavaForm("sin(x)*cos(x)+x+x+x+x");
        IDoubleValue vd = new DoubleVariable(3.0);
        DoubleEvaluator engine = new DoubleEvaluator(true);
        EvalEngine aux = new EvalEngine();
        aux.setNumericPrecision(25);
        aux.evaluate("x*x+log(x)");
        engine.defineVariable("x", 2);

        //engine.val
        //double d = aux.evaluate("x*x+log(x)").evalNumber().reDoubleValue();
        double d = engine.evaluate("x*x+log(x)");
        System.out.println("ln "+d);
        vd.setValue(4.0);
        Config.PARSER_USE_LOWERCASE_SYMBOLS = false;*/
        // false -> switch to Mathematica syntax mode:
        EvalEngine engine = new EvalEngine(false);

        TeXUtilities texUtil = new TeXUtilities(engine, false);

       StringWriter stw = new StringWriter();
       texUtil.toTeX("x*x*x-x*e^x", stw);
        // print: \sum_{i = 1}^{n}i
        System.out.println(stw.toString());
        // prints: D(Times(Sin(x),Cos(x)),x)

        for(float i = 0; i < 360; i += 360 / 20) {
            float[] hsv = new float[3];
            hsv[0] = i;
            hsv[1] = (float) Math.random();
            hsv[2] = (float) Math.random();
            poolColors.add(Color.HSVToColor(hsv));
        }
        vectors = view.findViewById(R.id.vectors);
        ViewPager slideView = view.findViewById(R.id.pager);
        interpolationGraph = view.findViewById(R.id.interpolationGraph);
        ImageButton add = view.findViewById(R.id.addRow);
        ImageButton homeGraph = view.findViewById(R.id.homeGraphButton);
        final List<LineGraphSeries<DataPoint>> listSeries = graphUtils.graphPharallel(50, "x", 0, getContext());
        homeGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (LineGraphSeries<DataPoint> inSerie : listSeries)
                    interpolationGraph.removeSeries(inSerie);
                interpolationGraph.getViewport().setYAxisBoundsManual(true);
                interpolationGraph.getViewport().setMinY(-3);
                interpolationGraph.getViewport().setMaxY(3);

                interpolationGraph.getViewport().setXAxisBoundsManual(true);
                interpolationGraph.getViewport().setMinX(-3);
                interpolationGraph.getViewport().setMaxX(3);
                interpolationGraph.getViewport().setScrollable(true);
                interpolationGraph.getViewport().setScrollableY(true);
                interpolationGraph.getViewport().setScalable(true);
                interpolationGraph.getViewport().setScalableY(true);
                for (LineGraphSeries<DataPoint> inSerie : listSeries)
                    interpolationGraph.addSeries(inSerie);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                addRow();
            }
        });
        ImageButton remove = view.findViewById(R.id.deleteRow);
        remove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                removeRow();
            }
        });
        List<Fragment> fragments = new LinkedList<>();
        fragments.add(new newtonInterpolator());
        fragments.add(new lagrange());
        fragments.add(new splineLineal());
        fragments.add(new splineCuadratico());
        fragments.add(new splineCubico());
        pagerAdapter pager = new pagerAdapter(getChildFragmentManager(), fragments);
        slideView.setAdapter(pager);
        initialize(count);
        for (LineGraphSeries<DataPoint> inSerie : listSeries)
            interpolationGraph.addSeries(inSerie);
        interpolationGraph.getViewport().setYAxisBoundsManual(true);
        interpolationGraph.getViewport().setMinY(-3);
        interpolationGraph.getViewport().setMaxY(3);
        interpolationGraph.getViewport().setXAxisBoundsManual(true);
        interpolationGraph.getViewport().setMinX(-3);
        interpolationGraph.getViewport().setMaxX(3);
        interpolationGraph.getViewport().setScrollable(true); // enables horizontal scrolling
        interpolationGraph.getViewport().setScrollableY(true); // enables vertical scrolling
        interpolationGraph.getViewport().setScalable(true);// esto genera errores se podria solucionar pero
        interpolationGraph.getViewport().setScalableY(true);// es complejo, es para el zoom
        for (LineGraphSeries<DataPoint> inSerie : graphUtils.graphPharallel(50, "x", 0, getContext()))
            interpolationGraph.addSeries(inSerie);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addRow() {
        int aux = ((TableRow) vectors.getChildAt(0)).getChildCount() - 1;
        EditText text = (EditText) ((TableRow) vectors.getChildAt(0)).getChildAt(aux);
        try {
            int auxColor = poolColors.remove(0);
            poolColors.add(auxColor);
            Integer checkInteger = Integer.parseInt(text.getText().toString());
            EditText key = defaultEditText(String.valueOf(checkInteger + 1),auxColor);
            ((TableRow) vectors.getChildAt(0)).addView(key);
            ((TableRow) vectors.getChildAt(1)).addView(defaultEditText("0",auxColor));

            viewToPoint.put(key, new Pair<>(updatePointGraph(checkInteger + 1, 0, auxColor), auxColor));
            count = count + 1;
        } catch (Exception isDouble) {
            try {
                int auxColor = poolColors.remove(0);
                poolColors.add(auxColor);
                double checkDouble = Double.parseDouble( text.getText().toString());
                EditText key = defaultEditText(String.valueOf(checkDouble+ 1),auxColor);
                ((TableRow) vectors.getChildAt(0)).addView(key);
                ((TableRow) vectors.getChildAt(1)).addView(defaultEditText("0",auxColor));

                viewToPoint.put(key,new Pair<>(updatePointGraph(checkDouble+1,0,auxColor),auxColor));
                count = count + 1;
            } catch (Exception d) {
                text.setError("invalid number");
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void removeRow() {
        if (count > 2) {
            int last = ((TableRow) vectors.getChildAt(0)).getChildCount() - 1;
            EditText key = (EditText) ((TableRow) vectors.getChildAt(0)).getChildAt(last);
            ((TableRow) vectors.getChildAt(0)).removeViewAt(last);
            ((TableRow) vectors.getChildAt(1)).removeViewAt(last);
            interpolationGraph.removeSeries(viewToPoint.get(key).first);
            viewToPoint.remove(key);
            count = count - 1;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public PointsGraphSeries<DataPoint> updatePointGraph(double x, double y,int color) {
        return graphUtils.graphPoint(x, y, PointsGraphSeries.Shape.POINT, interpolationGraph, getActivity(), color, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initialize(int count) {
        TableRow aux = new TableRow(getContext());
        aux.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TableRow aux2 = new TableRow(getContext());
        aux2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < count; i++) {
            int auxColor = poolColors.remove(0);
            poolColors.add(auxColor);
            EditText key = defaultEditText(String.valueOf(i),auxColor);
            aux.addView(key);
            aux2.addView(defaultEditText("0",auxColor));

            viewToPoint.put(key,new Pair <>(updatePointGraph(i,0,auxColor),auxColor));
        }
        vectors.addView(aux, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        vectors.addView(aux2, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public EditText defaultEditText(String value,int color) {
        EditText text = new EditText(getContext());
        text.setLayoutParams(new TableRow.LayoutParams(100, 110));
        text.setEms(2);
        text.setMaxLines(1);
        text.setBackground(null);
        text.setTextColor(Color.WHITE);
        text.setTypeface(null, Typeface.BOLD);
        //text.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        text.setBackgroundColor(color);
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        text.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

            text.addTextChangedListener(new TextChangedListener<EditText>(text) {
                @Override
                public void onTextChanged(EditText target, Editable s) {
                    try {
                        double x = 0;
                        double y = 0;
                        EditText key = target;
                        int aux = ((TableRow) vectors.getChildAt(0)).indexOfChild(key);
                        if (aux != -1) {
                            interpolationGraph.removeSeries(viewToPoint.get(key).first);
                            x = Double.parseDouble(target.getText().toString());
                            y = Double.parseDouble(((EditText) ((TableRow) vectors.getChildAt(1)).getChildAt(aux)).getText().toString());
                        } else {
                            key = ((EditText) ((TableRow) vectors.getChildAt(0)).getChildAt(((TableRow) vectors.getChildAt(1)).indexOfChild(target)));
                            interpolationGraph.removeSeries(viewToPoint.get(key).first);
                            x = Double.parseDouble(key.getText().toString());
                            y = Double.parseDouble(target.getText().toString());
                        }


                        if(viewToPoint.containsKey(key)){
                            int auxColor = viewToPoint.get(key).second;
                            viewToPoint.put(key, new Pair<>(updatePointGraph(x, y, auxColor), auxColor));
                        }
                    } catch (Exception ignored) {

                    }
                }
            });

        text.setKeyListener(DigitsKeyListener.getInstance("0123456789.-E"));
        text.setText(value);
        return text;
    }

    private abstract class TextChangedListener<T> implements TextWatcher {
        private T target;

        TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }

}
