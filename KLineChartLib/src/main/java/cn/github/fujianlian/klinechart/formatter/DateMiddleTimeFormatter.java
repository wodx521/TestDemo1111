package cn.github.fujianlian.klinechart.formatter;

import cn.github.fujianlian.klinechart.base.IDateTimeFormatter;
import cn.github.fujianlian.klinechart.utils.DateUtil;

import java.util.Date;

/**
 * 时间格式化器
 * Created by tifezh on 2016/6/21.
 */

public class DateMiddleTimeFormatter implements IDateTimeFormatter {
    @Override
    public String format(Date date) {
        if (date != null) {
            return DateUtil.middleTimeFormat.format(date);
        } else {
            return "";
        }
    }
}
