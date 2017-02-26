package com.cq9191.cashbook.MyManageUi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cq9191.cashbook.R;

import java.util.List;

/**
 * Created by 91910 on 2016/8/8.
 */
public class GridViewAdapter extends BaseAdapter{

    Context context;
    LayoutInflater layoutInflater;

    private List<String> my_list;
    private TextView my_text;
    private int person_or_things;

    //传入i=1，人物卡；i=2，事件卡; i=3, 存储时限卡
    public GridViewAdapter (Context c, List<String> l,int i){
        this.context = c;
        this.my_list = l;
        this.person_or_things = i;
        layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return my_list.size();
    }

    @Override
    public Object getItem(int i) {
        return my_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (person_or_things == 1) {
            view = layoutInflater.inflate(R.layout.add_new_person_cards_item, null);
            my_text = (TextView) view.findViewById(R.id.text_new_person);
        }
        else if (person_or_things == 2){
            view = layoutInflater.inflate(R.layout.add_new_things_cards_item, null);
            my_text = (TextView) view.findViewById(R.id.text_new_things);
        }
        if (i < my_list.size()){
            my_text.setText(my_list.get(i));
        }
        return view;
    }
}