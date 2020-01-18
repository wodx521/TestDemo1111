package cn.github.fujianlian.klinechart.formatter;

import android.content.Context;

import com.github.fujianlian.klinechart.R;
import cn.github.fujianlian.klinechart.base.IValueFormatter;

import java.util.Locale;

/**
 * 对较大数据进行格式化
 * Created by tifezh on 2017/12/13.
 */

public class BigValueFormatter implements IValueFormatter {

    //必须是排好序的
    private int[] values = {10000, 1000000, 100000000};
    private String[] units;
    private Context context;

    public BigValueFormatter(Context context) {
        this.context = context;
        units = context.getResources().getStringArray(R.array.unitArray);
    }

    @Override
    public String format(float value) {
        String unit = "";
        int i = values.length - 1;
        while (i >= 0) {
            if (value > values[i]) {
                value /= values[i];
                unit = units[i];
                break;
            }
            i--;
        }
        return String.format(Locale.getDefault(), "%.2f", value) + unit;
    }
}
