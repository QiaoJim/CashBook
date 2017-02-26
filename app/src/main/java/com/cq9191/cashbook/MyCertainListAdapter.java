package com.cq9191.cashbook;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cq9191.cashbook.ThreeMainFragments.MyListAdapter;

import java.util.List;

/**
 * Created by 91910 on 2016/12/19.
 */
public class MyCertainListAdapter extends BaseAdapter {

    private final Context context;
    private final int type;
    private List<String> time;
    private List<String> hour;
    private List<String> week;
    private List<String> person;
    private List<String> things;
    private List<String> money;
    private List<Integer> tag;

    private LayoutInflater inflater;

    public MyCertainListAdapter(int type, Context context, List<String> time, List<String> hour, List<String> week,
                         List<String> person,
                         List<String> things, List<String> money, List<Integer> tag){
        this.context = context;
        this.type = type;
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
        TextView tv_person_or_things;
        TextView tv_money;
        convertView = inflater.inflate(R.layout.certain_cash_item, null);
        Resources resources = context.getResources();
        tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        tv_person_or_things = (TextView) convertView.findViewById(R.id.tv_person_or_things);
        tv_money = (TextView) convertView.findViewById(R.id.tv_money);

        tv_time.setText(time.get(position) + "    " +
                hour.get(position) + "    " + week.get(position));

        if (type == 2){
            tv_person_or_things.setText(things.get(position));
        }
        else if (type == 3){
            tv_person_or_things.setText(person.get(position));
        }

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
