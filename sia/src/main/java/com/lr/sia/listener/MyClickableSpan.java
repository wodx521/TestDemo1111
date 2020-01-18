package com.lr.sia.listener;

import android.text.TextPaint;
import android.view.View;

import androidx.annotation.ColorInt;

public class MyClickableSpan extends android.text.style.ClickableSpan {

    private View.OnClickListener listener;
    private int color;

    public MyClickableSpan(@ColorInt int color, View.OnClickListener listener) {
        this.listener = listener;
        this.color = color;
    }

    @Override
    public void onClick(View widget) {
        listener.onClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(color);
    }
}
