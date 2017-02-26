package com.cq9191.cashbook.ThreeMainFragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cq9191.cashbook.R;

import java.util.List;

/**
 * Created by 91910 on 2016/12/19.
 */
public class MyListAdapter extends BaseAdapter {

    private final Context context;
    private List<String> time;
    private List<String> hour;
    private List<String> week;
    private List<String> person;
    private List<String> things;
    private List<String> money;
    private List<Integer> tag;

    private LayoutInflater inflater;

    public MyListAdapter(Context context, List<String> time, List<String> hour, List<String> week,
                         List<String> person,
                         List<String> things, List<String> money, List<Integer> tag){
        this.context = context;
        this.time = time;
        this.hour = hour;
        this.week = week;
        this.person = person;
        this.things = things;
        this.money = money;
        this.tag = tag;

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return person.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_time;
        TextView tv_person;
        TextView tv_things;
        TextView tv_money;
        convertView = inflater.inflate(R.layout.time_record_item, null);
        Resources resources = context.getResources();
        tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        tv_person = (TextView) convertView.findViewById(R.id.tv_person);
        tv_things = (TextView) convertView.findViewById(R.id.tv_things);
        tv_money = (TextView) convertView.findViewById(R.id.tv_money);

        tv_time.setText(time.get(position) + "    " +
                hour.get(position) + "    " + week.get(position));
        tv_things.setText(things.get(position));
        tv_person.setText(person.get(position));
        if (tag.get(position) == 0){
            tv_money.setTextColor(resources.getColor(R.color.green, null));
            tv_money.setText("+"+money.get(position)+" 元");
        }
        else if (tag.get(position) == 1){
            tv_money.setTextColor(resources.getColor(R.color.red, null));
            tv_money.setText("-"+money.get(position)+" 元");
        }
        return convertView;
    }
}
