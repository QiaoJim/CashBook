package com.cq9191.cashbook.Data;

import java.io.Serializable;

/**
 * Created by 91910 on 2016/8/23.
 */
public class CashData implements Serializable {

    private int cash_id;

    private String time;
    private String hour;
    private String week;

    private String cash_person;
    private String cash_things;
    private String cash_money;
    private String cash_desc;
    private int tag;   //0-收入；1-支出

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setCash_id(int cash_id) {
        this.cash_id = cash_id;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setCash_person(String cash_person) {
        this.cash_person = cash_person;
    }

    public void setCash_things(String cash_things) {
        this.cash_things = cash_things;
    }

    public void setCash_money(String cash_money) {
        this.cash_money = cash_money;
    }

    public void setCash_desc(String cash_desc) {
        this.cash_desc = cash_desc;
    }

    public int getCash_id() {
        return cash_id;
    }

    public String getHour() {
        return hour;
    }

    public String getTime() {
        return time;
    }

    public String getWeek() {
        return week;
    }

    public String getCash_person() {
        return cash_person;
    }

    public String getCash_things() {
        return cash_things;
    }

    public String getCash_money() {
        return cash_money;
    }

    public String getCash_desc() {
        return cash_desc;
    }
}
