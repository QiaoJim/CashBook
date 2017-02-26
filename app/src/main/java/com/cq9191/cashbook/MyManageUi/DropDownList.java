package com.cq9191.cashbook.MyManageUi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cq9191.cashbook.R;

import java.util.List;

/**
 * Created by 91910 on 2016/8/15.
 */
public class DropDownList extends PopupWindow implements AdapterView.OnItemClickListener {

    int list_type;   //1-人物列表；2-事件列表
    Context mContext;
    DropDownListAdapter mDropDownListAdapter;
    LayoutInflater mLayoutInflater;
    DropDownListAdapter.MyListItemClickListener myClickListener;

    ListView mListView;
    List<String> mList;

    public DropDownList(int list_type, Context context, List<String> list,
                        DropDownListAdapter.MyListItemClickListener listener){
        super(context);
        this.list_type = list_type;
        this.mContext = context;
        this.mList = list;
        this.myClickListener = listener;
        mLayoutInflater = LayoutInflater.from(context);

        showDropDownList();
    }

    private void showDropDownList() {
        View view = mLayoutInflater.inflate(R.layout.drop_down_list_layout, null);
        this.setContentView(view);

        mListView = (ListView) view.findViewById(R.id.drop_down_list);

        mDropDownListAdapter = new DropDownListAdapter(mContext, mList);
        mListView.setAdapter(mDropDownListAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
        if (myClickListener != null) {
            myClickListener.onItemClick(list_type, i);
        }
    }

}
