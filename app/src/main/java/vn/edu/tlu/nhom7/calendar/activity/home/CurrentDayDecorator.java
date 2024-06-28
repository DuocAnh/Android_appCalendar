package vn.edu.tlu.nhom7.calendar.activity.home;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Date;

public class CurrentDayDecorator implements DayViewDecorator {
    private final int color;
    private final CalendarDay day;

    public CurrentDayDecorator(Date date, int color) {
        this.color = color;
        this.day = CalendarDay.from(date);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (this.day.equals(day)){
            return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new CustomSpan(9, color)); // Adjust the radius and color here
    }

}