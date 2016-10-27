package com.cn21.speedtest.utils.calculate;

import com.cn21.speedtest.utils.User;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;

/**
 * Created by huangzhilong on 16/9/5.
 */
public class ColumnChartDataCalculate implements Calculate {
    ColumnChartData data_column;
    @Override
    public float calculateResult(String[] str) {
        return 0;
    }

    @Override
    public LineChartData calculateLineChartData(List<List<Float>> datas, int amount,String x,String y) {
        return null;
    }

    @Override
    public ColumnChartData calculateColumnChartData(List<List<Float>> datas, int amount) {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        values = new ArrayList<SubcolumnValue>();
        for (int j = 0; j < User.totalCpuRate.size(); ++j) {
            values.add(new SubcolumnValue(datas.get(0).get(j), getColor(j%5)));
        }

        Column column = new Column(values);
        column.setHasLabels(true);
        //column.setHasLabelsOnlyForSelected(true);
        columns.add(column);

        data_column = new ColumnChartData(columns);

        if (true) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (true) {
                axisX.setName("进程");
                axisY.setName("平均占用率/%");
            }
            data_column.setAxisXBottom(axisX);
            data_column.setAxisYLeft(axisY);
        } else {
            data_column.setAxisXBottom(null);
            data_column.setAxisYLeft(null);
        }
        return data_column;
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
