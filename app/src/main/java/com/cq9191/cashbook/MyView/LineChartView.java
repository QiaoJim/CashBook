package com.cq9191.cashbook.MyView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.cq9191.cashbook.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 91910 on 2016/9/11.
 */
public class LineChartView extends View {

    private WindowManager wm;
    private int screen_width;

    //画折线图的横向坐标
    private int XPoint;
    //折线图的竖向坐标
    private int YPoint;
    //横向刻度长度，根据传入数据数决定
    private int XScale;
    //竖轴刻度，每一格 30 元
    private int YScale;
    //横轴总长度
    private int XLength;
    //竖轴总长度
    private int YLength;
    //竖轴文字提示
    private String[] YLabel;

    //数据数组
    private List<Float> money_data = new ArrayList<>();
    private List<String> time_data = new ArrayList<>();

    SharedPreferences mPreferences;

    private Canvas canvas;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        //获得SharedPreferences，读取数据
        mPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        initLength();

        for (int i = 0; i < YLabel.length; i++) {
            YLabel[i] = mPreferences.getInt("y_scale", 50) * i + "";
        }

        for (int i=0;i<time_data.size();i++) {
            Log.v("---- on draw 1---", "ok     " + time_data.get(i) + "    " + money_data.get(i));
        }

        Paint paint_line = new Paint();
        Paint paint_text = new Paint();
        Paint paint_xuxian = new Paint();
        Paint paint_xuxian2 = new Paint();

        PathEffect effect = new DashPathEffect(new float[]{10,10},1);
        PathEffect effect2 = new DashPathEffect(new float[]{7,7},1);
        paint_xuxian.setPathEffect(effect);
        paint_xuxian2.setPathEffect(effect2);

        paint_line.setStyle(Paint.Style.FILL);
        paint_text.setStyle(Paint.Style.STROKE);
        paint_xuxian.setStyle(Paint.Style.STROKE);
        paint_xuxian2.setStyle(Paint.Style.STROKE);
        paint_line.setAntiAlias(true); //去锯齿
        paint_text.setAntiAlias(true); //去锯齿
        paint_xuxian.setAntiAlias(true); //去锯齿
        paint_xuxian2.setAntiAlias(true); //去锯齿
        paint_line.setColor(getResources().getColor(R.color.deep_blue));
        paint_text.setColor(getResources().getColor(R.color.slight_blue));
        paint_xuxian.setColor(getResources().getColor(R.color.slight_orange));
        paint_xuxian2.setColor(getResources().getColor(R.color.deep_blue));
        paint_text.setTextSize((int) (14 * this.getResources().getDisplayMetrics().density + 0.5f));
        paint_line.setStrokeWidth(5);
        paint_xuxian.setStrokeWidth(3);
        paint_xuxian2.setStrokeWidth(5);

        //画Y轴
        canvas.drawLine(XPoint+25, YPoint - YLength, XPoint+25, YPoint - YLength + 13, paint_line);
        //画不精确测量的 Y轴 虚线
        Path path2 = new Path();
        path2.moveTo(XPoint+25, YPoint - YLength + 13);
        path2.lineTo(XPoint+25, YPoint - 11.5f * YScale - YScale * 2);
        canvas.drawPath(path2, paint_xuxian2);
        canvas.drawLine(XPoint+25, YPoint - 11.5f * YScale - YScale * 2, XPoint+25, YPoint - YScale *2, paint_line);

        //Y轴箭头
        canvas.drawLine(XPoint+25, YPoint - YLength, XPoint +20, YPoint - YLength + 10, paint_line);
        canvas.drawLine(XPoint+25, YPoint - YLength, XPoint +30, YPoint - YLength + 10, paint_line);

        //添加刻度和文字
        for (int i = 0; i < YLabel.length; i++) {
            canvas.drawLine(XPoint+25, YPoint - i * YScale - YScale *2, XPoint + 32, YPoint - i * YScale - YScale *2, paint_line);  //刻度

            canvas.drawText(YLabel[i], XPoint - 80, YPoint - i * YScale- YScale *2 + YScale/4, paint_text);//文字
        }
        canvas.drawText("单位/元", XPoint - 140, YScale*0.8f, paint_text);

        //画X轴
        canvas.drawLine(XPoint+25, YPoint - YScale*2, screen_width - 60, YPoint - YScale *2, paint_line);
        //画x轴箭头
        canvas.drawLine(screen_width - 70 , YPoint - YScale *2 - 6,screen_width - 60, YPoint - YScale*2, paint_line);
        canvas.drawLine(screen_width - 70 , YPoint - YScale *2 + 6,screen_width - 60, YPoint - YScale *2, paint_line);


        if (time_data.size()>0) {
            //画 时间
            canvas.drawText(time_data.get(0) + "", XPoint + XScale - XScale+25, YPoint - YScale, paint_text);
            //画虚线
            Path path = new Path();
            path.moveTo(XPoint + XScale+25, YPoint - YScale * 2);
            path.lineTo(XPoint + XScale+25, YPoint - money_data.get(0) * YScale - YScale * 2);
            canvas.drawPath(path, paint_xuxian);
            //画圆点
            canvas.drawCircle(XPoint + XScale+25, YPoint - money_data.get(0) * YScale - YScale * 2,
                    6f,paint_line);

            if (money_data.size() > 1) {
                for (int i = 1; i < money_data.size(); i++) {
                    //画折线
                    canvas.drawLine(XPoint + i * XScale+25, YPoint - money_data.get(i - 1) * YScale - YScale * 2,
                            XPoint + (i + 1) * XScale+25, YPoint - money_data.get(i) * YScale - YScale * 2, paint_line);
                    //画圆点
                    canvas.drawCircle(XPoint + (i + 1) * XScale+25, YPoint - money_data.get(i) * YScale - YScale * 2,
                            6f,paint_line);

                    if (i % 3 == 0) {
                        //坐标底部时间
                        canvas.drawText(time_data.get(i) + "", XPoint + i * XScale+25, YPoint - YScale, paint_text);
                        //连接虚线
                        Path path1 = new Path();
                        path1.moveTo(XPoint + (i + 1) * XScale+25, YPoint - YScale * 2);
                        path1.lineTo(XPoint + (i + 1) * XScale+25, YPoint - money_data.get(i) * YScale - YScale * 2);
                        canvas.drawPath(path1, paint_xuxian);

                    }
                }
            }
        }
    }

    private void initLength() {
        screen_width = wm.getDefaultDisplay().getWidth();

        XPoint = (int) (50 * this.getResources().getDisplayMetrics().density + 0.5f);
        YPoint = (int) (300 * this.getResources().getDisplayMetrics().density + 0.5f);

        XScale = (int) (10 * this.getResources().getDisplayMetrics().density + 0.5f);

        YScale = (int) (20 * this.getResources().getDisplayMetrics().density + 0.5f);
        XLength = (int) (screen_width * this.getResources().getDisplayMetrics().density + 0.5f);
        YLength = (int) (300 * this.getResources().getDisplayMetrics().density + 0.5f);

        YLabel = new String[12];

    }

    public void setList(List<String> timeList, List<Float> moneyList){
        this.time_data = timeList;
        this.money_data = moneyList;

    }

    public void refresh(){
        Log.v("---- refresh ----","done");
        requestLayout();
        invalidate();
    }

}
