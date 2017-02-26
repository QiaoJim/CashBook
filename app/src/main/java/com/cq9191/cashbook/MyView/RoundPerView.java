package com.cq9191.cashbook.MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.cq9191.cashbook.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 91910 on 2016/9/8.
 */
public class RoundPerView extends ImageView {

    private List<String> money_record = new ArrayList<>();
    private int[] color;

    public RoundPerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        //分别是左上右下的顺序参数，正方形
        RectF r = new RectF(getWidth()/2 - (getHeight()/2-40) , 40 , getWidth()/2 + (getHeight()/2-40), getHeight() - 40);

        getList();

        float sum = 0;
        for (int i = 0; i < money_record.size(); i++) {
            sum += Float.valueOf(money_record.get(i));
        }
        //设置旋转角度
        float range = 0;
        //和初始角度
        float ori = 0;

        for (int i = 0; i < (money_record.size()>=7 ? 7:money_record.size()); i++) {
            p.setColor(getResources().getColor(color[i]));
            float level = Float.valueOf(money_record.get(i));
            //当前的初始角度是在旋转的角度加上一个数据的初始角度
            ori = ori + range;
            range = (level * 360) / sum;
            //绘制扇形，起始角度，旋转角度
            canvas.drawArc(r, ori, range, true, p);
        }
    }

    private void getList() {
        //用来分别给不同等级的扇形做底色
        color = new int[]{
                R.color.green,
                R.color.pink,
                R.color.slight_orange,
                R.color.blue,
                R.color.yellow,
                R.color.purple,
                R.color.slight_gray
        };
    }

    public void setList(List<String> list){
        this.money_record = list;
    }
}
