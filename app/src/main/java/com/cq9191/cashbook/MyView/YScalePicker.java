package com.cq9191.cashbook.MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.cq9191.cashbook.R;

/**
 * Created by 91910 on 2016/9/16.
 */
public class YScalePicker extends NumberPicker {

    public YScalePicker(Context context) {
        super(context);
    }

    public YScalePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YScalePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view) {
        if (view instanceof EditText) {
            //这里修改字体的属性
            ((EditText) view).setTextColor(getResources().getColor(R.color.deep_blue));
            ((EditText) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }
    }
}
