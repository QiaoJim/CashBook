package com.cq9191.cashbook.MyManageUi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cq9191.cashbook.R;

import java.util.List;

/**
 * Created by 91910 on 2016/8/15.
 */
public class DropDownListAdapter extends BaseAdapter {

    Context mContext;
    List<String> mList;
    LayoutInflater mLayoutInflater;


    public static interface MyListItemClickListener{
        public void onItemClick(int list_type, int position);
    }

    public DropDownListAdapter(Context context, List<String> list){
        this.mContext = context;
        this.mList = list;

        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.drop_down_list_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(mList.get(i));
        return view;
    }

    public static class ViewHolder {
        public TextView textView;
    }
}
