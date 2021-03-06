package com.sands.aplication.numeric.fragments.oneVariableFragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sands.aplication.numeric.R;
import com.sands.aplication.numeric.fragments.customPopUps.popUpNewton;
import com.sands.aplication.numeric.fragments.listViewCustomAdapter.Newton;
import com.sands.aplication.numeric.fragments.listViewCustomAdapter.NewtonListAdapter;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.sands.aplication.numeric.fragments.homeFragment.poolColors;


/**
 * A simple {@link Fragment} subclass.
 */
public class newtonFragment extends baseOneVariableFragments {

    private Expression functionDeriv1;
    private View view;
    private ListView listView;
    private EditText xvalue;
    private EditText textFunctionG;

    public newtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            view = inflater.inflate(R.layout.fragment_newton, container, false);
        } catch (InflateException e) {
            // ignorable
        }
        Button runFixed = view.findViewById(R.id.runNewton);
        runFixed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                cleanGraph();
                bootStrap();
            }
        });
        Button runHelp = view.findViewById(R.id.runHelp);
        Button runChart = view.findViewById(R.id.runChart);
        runChart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                executeChart(getContext());
            }
        });
        listView = view.findViewById(R.id.listView);
        runHelp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                executeHelp();
            }
        });

        xvalue = view.findViewById(R.id.xValue);
        textFunctionG = view.findViewById(R.id.functionG);


        registerEditText(textFunctionG, getContext(), getActivity());
        registerEditText(xvalue, getContext(), getActivity());
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void executeHelp() {
        Intent i = new Intent(Objects.requireNonNull(getContext()).getApplicationContext(), popUpNewton.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void execute(boolean error, double errorValue, int ite) {
        Double xValue = 0.0;

        String originalFunctionDeriv1 = textFunctionG.getText().toString();

        this.functionDeriv1 = new Expression(functionRevision(originalFunctionDeriv1));
        boolean errDeriv1 = checkSyntax(originalFunctionDeriv1, textFunctionG);
        if (errDeriv1)
            updateMyFunctions(originalFunctionDeriv1);
        error = errDeriv1 && error;

        try {
            xValue = Double.parseDouble(xvalue.getText().toString());
        } catch (Exception e) {
            xvalue.setError("Invalid Xi");
            error = false;
        }

        if (error) {
            if (errorToggle.isChecked()) {
                newtonMethod(xValue, errorValue, ite, true);
            } else {
                newtonMethod(xValue, errorValue, ite, false);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void newtonMethod(Double x0, Double tol, int ite, boolean errorRel) {
        try {


            function.setPrecision(100);
            functionDeriv1.setPrecision(100);
            ArrayList<Newton> listValues = new ArrayList<>();
            Newton titles = new Newton("n", "Xn", "f(Xn)", "f'(Xn)", "Error");
            listValues.add(titles);
            listValuesTitles = new LinkedList<>();
            listValuesTitles.add("Xn");
            listValuesTitles.add("f(Xn)");
            listValuesTitles.add("f'(Xn)");
            listValuesTitles.add("Error");
            //TableViewModel.getTitles(listValuesTitles);
            completeList = new LinkedList<>();
            if (tol >= 0) {
                if (ite > 0) {
                    double y0 = (this.function.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    double y0p = (this.functionDeriv1.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    if (y0 != 0) {
                        int cont = 0;
                        double error = tol + 1;
                        Newton iteZero = new Newton(String.valueOf(cont), String.valueOf(normalTransformation(x0)), String.valueOf(cientificTransformation(y0)), String.valueOf(cientificTransformation(y0p)), String.valueOf(cientificTransformation(error)));
                        listValues.add(iteZero);
                        List<String> listValuesIteZero = new LinkedList<>();
                        listValuesIteZero.add(String.valueOf(x0));
                        listValuesIteZero.add(String.valueOf(cientificTransformation(y0)));
                        listValuesIteZero.add(String.valueOf(cientificTransformation(y0p)));
                        listValuesIteZero.add(String.valueOf(""));
                        double xa = x0;
                        completeList.add(listValuesIteZero);
                        calc = true;
                        Expression newtonFunction;
                        while ((y0 != 0) && (error > tol) && y0p != 0 && (cont < ite)) {
                            ArrayList<String> listValuesIteNext = new ArrayList<String>();
                            double xn;
                            newtonFunction = new Expression("x" + "-" + (y0 / y0p));
                            newtonFunction.setPrecision(100);
                            xn = (newtonFunction.with("x", BigDecimal.valueOf(xa)).eval()).doubleValue();
                            try {
                                y0 = Double.NaN;
                                y0p = Double.NaN;
                                y0p = (this.functionDeriv1.with("x", BigDecimal.valueOf(xn)).eval()).doubleValue();
                                y0 = (this.function.with("x", BigDecimal.valueOf(xn)).eval()).doubleValue();

                            } catch (Exception e) {
                                styleWrongMessage("Unexpected error posibly NaN ");
                            }
                            if (errorRel)
                                error = Math.abs(xn - xa) / xn;
                            else
                                error = Math.abs(xn - xa);

                            xa = xn;
                            cont++;
                            Newton iteNext = new Newton(String.valueOf(cont), String.valueOf(normalTransformation(xa)), String.valueOf(cientificTransformation(y0)), String.valueOf(cientificTransformation(y0p)), String.valueOf(cientificTransformation(error)));
                            listValues.add(iteNext);
                            listValuesIteNext.add(String.valueOf(xa));//correccion xa-> xn
                            listValuesIteNext.add(String.valueOf(cientificTransformation(y0)));
                            listValuesIteNext.add(String.valueOf(cientificTransformation(y0p)));
                            listValuesIteNext.add(String.valueOf(cientificTransformation(error)));
                            completeList.add(listValuesIteNext);
                        }
                        listValues.add(new Newton("", "", "", "", ""));

                        int color = poolColors.remove(0);
                        poolColors.add(color);
                        graphSerie(function.getExpression(), xa, color);
                        if (y0 == 0) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);

                            styleCorrectMessage(normalTransformation(xa) + " is a root");
                        } else if (error <= tol) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);

                            styleCorrectMessage(normalTransformation(xa) + " is an aproximate root");
                        } else {
                            styleWrongMessage("The method failed with " + ite + " iterations!");
                        }
                    } else {
                        int color = poolColors.remove(0);
                        poolColors.add(color);
                        graphPoint(x0, y0, color);
                        styleCorrectMessage(normalTransformation(x0) + " is an aproximate root");
                    }
                } else {
                    iter.setError("Wrong iterates");
                    styleWrongMessage("Wrong iterates");
                }
            } else {
                textError.setError("Tolerance must be > 0");
                styleWrongMessage("Tolerance must be > 0");
            }
            NewtonListAdapter adapter = new NewtonListAdapter(getContext(), R.layout.list_adapter_newton, listValues);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            styleWrongMessage("Unexpected error posibly NaN");
        }
    }

}



