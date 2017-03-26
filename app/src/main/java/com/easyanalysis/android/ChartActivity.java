package com.easyanalysis.android;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by www10 on 2017/3/26.
 */

public class ChartActivity extends Activity{
    private LineChartView mchart;
    private Map<String,Integer> table = new TreeMap<>();//和坐标排序
    private LineChartData mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);
        mchart = (LineChartView) findViewById(R.id.chart);
        List<CostBean> allDate = (List<CostBean>) getIntent().getSerializableExtra("cost_list");//取出数据
        //对数据进行处理，如果有同一天则合并
        generateValue(allDate);//此时table是数据源
        generateData();
        mchart.setLineChartData(mData);
    }

    private void generateData() {
        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        int indexX = 0;
        //处理点
        for (Integer value : table.values()) {
            values.add(new PointValue(indexX,value));
            indexX++;
        }
        //处理线
        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(ValueShape.CIRCLE);
        line.setPointColor(ChartUtils.COLORS[1]);
        lines.add(line);
        mData = new LineChartData(lines);
    }

    private void generateValue(List<CostBean> allDate) {//重复数据累加，不重复收集到数据源中table
        if (allDate != null) {
            for (int i =0;i<allDate.size();i++) {//每一二个都是costbean类型
                CostBean costBean = allDate.get(i);
                String costDate = costBean.costDate;//日期
                int costMoney = Integer.parseInt(costBean.costMoney);//金额
                if (!table.containsKey(costDate)) {
                    table.put(costDate,costMoney);
                }else {
                    int originMoney = table.get(costDate);//取出原始值
                    table.put(costDate,originMoney + costMoney);//table是数据源
                }
            }
        }
    }
}
