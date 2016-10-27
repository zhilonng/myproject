package com.cn21.speedtest.utils.calculate;

import com.cn21.speedtest.utils.User;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;

/**
 * Created by huangzhilong on 16/9/5.
 */
public class LineChartDataCalculate implements Calculate {
    LineChartData data;

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = false;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean hasLabelForSelected = true;
    @Override
    public float calculateResult(String[] str) {
        return 0;
    }

    @Override
    public LineChartData calculateLineChartData(List<List<Float>> totalCpuRate,int amount,String x ,String y) {
        List<Line> lines = new ArrayList<Line>();
        List<List<PointValue>> values3 = new ArrayList<>();
        List<PointValue> values = new ArrayList();
        values3.add(values);
        if (totalCpuRate != null) {
            for (int j = 0; j < totalCpuRate.get(0).size(); ++j) {
                values3.get(0).add(new PointValue(j, totalCpuRate.get(0).get(j)));
            }
            for (int i = 1; i < totalCpuRate.size(); i++) {
                values3.add(new ArrayList<PointValue>());
                if (totalCpuRate.get(i) != null) {
                    for (int k = 0, n = totalCpuRate.get(0).size() - totalCpuRate.get(i).size(); k < totalCpuRate.get(i).size(); k++, n++) {
                        values3.get(i).add(new PointValue(n, totalCpuRate.get(i).get(k)));
                    }
                }
            }
            for (int i = 0; i < values3.size(); i++) {
                Line line = new Line(values3.get(i));
                line.setColor(getColor(i % 5));
                line.setShape(shape);
                line.setCubic(true);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
                lines.add(line);
            }
            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName(x);
                    axisY.setName(y);
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }
            data.setBaseValue(Float.NEGATIVE_INFINITY);
        }
        return data;
    }

    @Override
    public ColumnChartData calculateColumnChartData(List<List<Float>> totalCpuRate, int amount) {
        return null;
    }

    private int getColor(int i){
        switch (i){
            case 0:
                return User.COLOR_RED;
            case 1:
                return User.COLOR_GREEN;
            case 2:
                return User.COLOR_ORANGE;
            case 3:
                return User.COLOR_BLUE;
            case 4:
                return User.COLOR_VIOLET;
            case 5:
                return User.DEFAULT_COLOR;
            default: break;
        }
        return User.COLOR_BLUE;
    }
}
