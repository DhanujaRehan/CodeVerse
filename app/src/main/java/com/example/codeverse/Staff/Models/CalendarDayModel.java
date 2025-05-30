package com.example.codeverse.Staff.Models;

import java.util.Date;

public class CalendarDayModel {
    private Date date;
    private boolean isCurrentMonth;
    private boolean hasEvents;
    private boolean isSelected;

    public CalendarDayModel() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }

    public boolean hasEvents() {
        return hasEvents;
    }

    public void setHasEvents(boolean hasEvents) {
        this.hasEvents = hasEvents;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}