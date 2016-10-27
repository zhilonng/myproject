package com.cn21.speedtest.utils.calculate;

import java.util.List;

import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;

/**
 * Created by huangzhilong on 16/9/5.
 */
public interface Calculate {
    /**
     * 计算并返回float类型的结果
     * @param str
     * @return
     */
    float calculateResult(String[] str);

    /**
     * 返回lincechartdata用于绘图
     * @return LineChartData
     */
    LineChartData calculateLineChartData(List<List<Float>> datas, int amount, String x, String y);

    /**
     * 返回columndata用于绘图
     * @return ColumnChartData
     */
    ColumnChartData calculateColumnChartData(List<List<Float>> datas, int amount);

}
