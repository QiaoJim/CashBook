package com.cq9191.cashbook.MyManageUi;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by 91910 on 2016/9/4.
 */
public class SetEndTimeDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private OnTimeListener timeListener;

    public interface OnTimeListener{
        void getEndTime(int year, int month, int day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        timeListener.getEndTime(i, i1+1, i2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        timeListener = (OnTimeListener) activity;
    }
}
