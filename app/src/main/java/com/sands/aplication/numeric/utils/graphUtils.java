package com.sands.aplication.numeric.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Pair;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;
import com.udojava.evalex.Expression;

import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class graphUtils {
    private final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public PointsGraphSeries<DataPoint> graphPoint(double x, double y, PointsGraphSeries.Shape figure, int color, boolean listener) {
        PointsGraphSeries<DataPoint> point = new PointsGraphSeries<>(new DataPoint[]{
                new DataPoint(x, y)
        });
        if (listener)
            point.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    //some shit here
                    //Toast.makeText(activity, "(" + dataPoint.getX() + " , " + dataPoint.getY() + ")", Toast.LENGTH_SHORT).show();
                }
            });
        point.setShape(figure);
        point.setColor(color);
        return point;
    }

    public String functionRevision(String function) {
        if (function.toLowerCase().contains("ln"))
            return function.toLowerCase().replace("ln", "log");
        else
            return function;
    }

    public List<LineGraphSeries<DataPoint>> bestGraphPharallel(int iters, String functionExpr, int color, Context context) {
        int realIters = iters * 2;
        int perCore = (int) Math.ceil(realIters / NUMBER_OF_CORES) * 2;
        double each = (realIters * 0.1 * -1);
        double end = (each + (perCore * 0.1) - 0.1);
        Thread[] cores = new Thread[NUMBER_OF_CORES];

        bestThreadGraph[] values = new bestThreadGraph[NUMBER_OF_CORES];
        for (int i = 0; i < cores.length; i++) {
            values[i] = new bestThreadGraph(each, end, functionExpr, color, perCore);
            cores[i] = new Thread(values[i]);
            cores[i].start();
            each = (each + (perCore * 0.1) - 0.1);
            end = (each + (perCore * 0.1) + 0.1);
        }
        List<LineGraphSeries<DataPoint>> listSeries = new LinkedList<>();
        for (int i = 0; i < cores.length; i++) {
            try {
                cores[i].join();
                listSeries.add(values[i].getSeries());
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        return listSeries;
    }

    //(function,(color,(init,end))
    public List<LineGraphSeries<DataPoint>> graphPharallelByFunctions(List<Pair<String, Pair<Integer, Pair<Double, Double>>>> functions) {

        int maxThreads = NUMBER_OF_CORES;
        if (functions.size() < NUMBER_OF_CORES)
            maxThreads = functions.size();


        Thread[] cores = new Thread[maxThreads];
        bestThreadGraph[] values = new bestThreadGraph[maxThreads];
        int i = 0;

        List<LineGraphSeries<DataPoint>> listSeries = new LinkedList<>();
        for (Pair<String, Pair<Integer, Pair<Double, Double>>> func : functions) {
            if (i == maxThreads)
                i = 0;


            if (cores[i] == null) {
                double init = func.second.second.first;
                double end = func.second.second.second;

                values[i] = new bestThreadGraph(init, end, func.first, func.second.first, (int) (Math.ceil(Math.abs(end - init) / 0.1)));
                cores[i] = new Thread(values[i]);
                cores[i].start();

            } else {
                try {
                    cores[i].join();
                    listSeries.add(values[i].getSeries());
                    double init = func.second.second.first;
                    double end = func.second.second.second;
                    //do another serie
                    values[i] = new bestThreadGraph(init, end, func.first, func.second.first, (int) (Math.ceil(Math.abs(end - init) / 0.1)));
                    cores[i] = new Thread(values[i]);
                    cores[i].start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            i += 1;
        }
        for (int j = 0; j < maxThreads; j++) {
            if (cores[j] != null) {
                try {
                    cores[j].join();
                    listSeries.add(values[j].getSeries());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        return listSeries;
    }

    public List<LineGraphSeries<DataPoint>> graphPharallel(int iters, String funcitonExpr, int color) {
        int realIters = iters * 2;
        int perCore = (int) Math.ceil(realIters / NUMBER_OF_CORES) * 2;
        double each = (realIters * 0.1 * -1);
        double end = (each + (perCore * 0.1));
        double lastEnd = each * -1;
        Thread[] cores = new Thread[NUMBER_OF_CORES];

        threadGraph[] values = new threadGraph[NUMBER_OF_CORES];
        for (int i = 0; i < cores.length; i++) {
            values[i] = new threadGraph(each - 1, end, funcitonExpr, color, perCore);
            cores[i] = new Thread(values[i]);
            cores[i].start();
            each = (each + (perCore * 0.1));
            end = (end + (perCore * 0.1));
            if (i == cores.length - 1) { //error de redondeo
                end = lastEnd;
            }
        }
        List<LineGraphSeries<DataPoint>> listSeries = new LinkedList<>();
        for (int i = 0; i < cores.length; i++) {
            try {
                cores[i].join();
                listSeries.add(values[i].getSeries());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listSeries;

    }

    private class bestThreadGraph implements Runnable {
        private final double x;
        private final String function;
        private final int color;
        private final int perCore;
        private final DoubleEvaluator engine = new DoubleEvaluator();
        private double end;
        private LineGraphSeries<DataPoint> series;

        bestThreadGraph(double x, double end, String function, int color, int perCore) {
            this.x = x;
            this.end = end;
            this.function = function;
            this.color = color;
            this.perCore = perCore;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            series = new LineGraphSeries<>();
            double y;
            double x = this.x;

            IDoubleValue vd = new DoubleVariable(3.0);
            if (x > this.end) {
                y = x;
                x = this.end;
                this.end = y;
            }
            try {
                engine.defineVariable("x", vd);
                engine.evaluate(function);

                while (x <= this.end) {

                    vd.setValue(x);
                    y = engine.evaluate();

                    this.series.appendData(new DataPoint(x, y), true, perCore * 2);


                    x = Math.round((x + 0.1) * 1000.0) / 1000.0;
                }
            } catch (Exception ignored) {

            }
            series.setColor(color);
            //graphFragment.listSeries.add(series);
        }

        LineGraphSeries<DataPoint> getSeries() {
            return series;
        }
    }

    private class threadGraph implements Runnable {
        private final double x;
        private final Expression function;
        private final int color;
        private final int perCore;
        private final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        private double end;

        threadGraph(double x, double end, String function, int color, int perCore) {
            this.x = x;
            this.end = end;
            this.function = new Expression(function);
            this.color = color;
            this.perCore = perCore;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {

            this.function.setPrecision(5);
            double y;
            double x = this.x;
            if (x > this.end) {
                y = x;
                x = this.end;
                this.end = y;
            }
            while (x <= this.end) {
                try {
                    y = (this.function.with("x", BigDecimal.valueOf(x)).eval()).doubleValue();
                    this.series.appendData(new DataPoint(x, y), true, perCore * 2);

                } catch (Exception ignored) {

                }
                x = Math.round((x + 0.1) * 1000.0) / 1000.0;
                //x = (x + 0.1);
            }
            series.setColor(color);
            //graphFragment.listSeries.add(series);
        }

        LineGraphSeries<DataPoint> getSeries() {
            return series;
        }
    }

}
