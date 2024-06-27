package vn.edu.tlu.nhom7.calendar.activity.home;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import vn.edu.tlu.nhom7.calendar.R;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendar;
    private CalendarDay selectedDate;
    private TextView tvDuong, tvAm;

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = rootView.findViewById(R.id.calendarView);
        tvDuong = rootView.findViewById(R.id.tv_duong);
        tvAm = rootView.findViewById(R.id.tv_am);

        selectedDate = CalendarDay.today();
        calendar.setDateSelected(selectedDate, true);
        updateDateTextViews(selectedDate);

        calendar.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(selectedDate);
            }
            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled(false);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.parseColor("#6750a4"));
                drawable.setCornerRadius(8);
                view.setBackgroundDrawable(drawable);
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
            }
        });

        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date;
                updateDateTextViews(selectedDate);
                calendar.invalidateDecorators();
            }
        });

        return rootView;
    }

    private void updateDateTextViews(CalendarDay date) {
        String solarDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
        tvDuong.setText(solarDate);

        String lunarDate = convertSolarToLunar(date);
        tvAm.setText(lunarDate);
    }

    private String convertSolarToLunar(CalendarDay date) {
        ChineseCalendar chineseCalendar = new ChineseCalendar();
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDay());
        chineseCalendar.setTimeInMillis(calendar.getTimeInMillis());

        int lunarYear = chineseCalendar.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int lunarMonth = chineseCalendar.get(ChineseCalendar.MONTH) + 1; // Add 1 because MONTH is 0-based
        int lunarDay = chineseCalendar.get(ChineseCalendar.DAY_OF_MONTH);
        boolean isLeapMonth = chineseCalendar.get(ChineseCalendar.IS_LEAP_MONTH) == 1;

        // Format the lunar date
        String lunarDate = (isLeapMonth ? "Leap " : "") + lunarDay + "/" + lunarMonth + "/" + lunarYear;

        return lunarDate;
    }
}
