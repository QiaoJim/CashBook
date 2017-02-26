package com.cq9191.cashbook.Data;

import java.io.Serializable;

/**
 * Created by 91910 on 2016/9/6.
 */
public class QueryRequirement implements Serializable {

    private String person;
    private String things;
    private String start_time;
    private String end_time;
    private String min_money;
    private String max_money;
    private boolean[] tag;

    public void setTag(boolean[] tag) {
        this.tag = tag;
    }

    public boolean[] getTag() {
        return tag;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setThings(String things) {
        this.things = things;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setMin_money(String min_money) {
        this.min_money = min_money;
    }

    public void setMax_money(String max_money) {
        this.max_money = max_money;
    }

    public String getPerson() {
        return person;
    }

    public String getThings() {
        return things;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getMin_money() {
        return min_money;
    }

    public String getMax_money() {
        return max_money;
    }
}
