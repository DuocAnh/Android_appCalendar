//package vn.edu.tlu.nhom7.calendar.activity.home;
//
//import android.graphics.Color;
//import android.graphics.drawable.GradientDrawable;
//import android.icu.util.ChineseCalendar;
//import android.os.Bundle;
//import android.text.style.ForegroundColorSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.prolificinteractive.materialcalendarview.CalendarDay;
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
//import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
//import com.prolificinteractive.materialcalendarview.DayViewDecorator;
//import com.prolificinteractive.materialcalendarview.DayViewFacade;
//import vn.edu.tlu.nhom7.calendar.R;
//import vn.edu.tlu.nhom7.calendar.adapter.EventAdapter;
//import vn.edu.tlu.nhom7.calendar.model.Event;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//public class CalendarFragment extends Fragment {
//
//    private MaterialCalendarView calendar;
//    private CalendarDay selectedDate;
//    private TextView tvDuong, tvAm;
//    private RecyclerView recyclerView;
//    private EventAdapter eventAdapter;
//    private List<Event> eventList;
//
//    public CalendarFragment() {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
//
//        calendar = rootView.findViewById(R.id.calendarView);
//        tvDuong = rootView.findViewById(R.id.tv_duong);
//        tvAm = rootView.findViewById(R.id.tv_am);
//        recyclerView = rootView.findViewById(R.id.list_event);
//
//        selectedDate = CalendarDay.today();
//        calendar.setDateSelected(selectedDate, true);
//        updateDateTextViews(selectedDate);
//
//        calendar.addDecorator(new DayViewDecorator() {
//            @Override
//            public boolean shouldDecorate(CalendarDay day) {
//                return day.equals(selectedDate);
//            }
//            @Override
//            public void decorate(DayViewFacade view) {
//                view.setDaysDisabled(false);
//                GradientDrawable drawable = new GradientDrawable();
//                drawable.setColor(Color.parseColor("#6750a4"));
//                drawable.setCornerRadius(8);
//                view.setBackgroundDrawable(drawable);
//                view.addSpan(new ForegroundColorSpan(Color.WHITE));
//            }
//        });
//
//        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                selectedDate = date;
//                updateDateTextViews(selectedDate);
//                calendar.invalidateDecorators();
//            }
//        });
//
//        // Setup RecyclerView
//        eventList = new ArrayList<>();
//        eventList.add(new Event("01/01", "New Year's Day"));
//        eventList.add(new Event("14/02", "Valentine's Day"));
//        // Thêm các sự kiện khác
//
//        eventAdapter = new EventAdapter(eventList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(eventAdapter);
//
//        return rootView;
//    }
//
//    private void updateDateTextViews(CalendarDay date) {
//        String solarDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
//
//        tvDuong.setText(solarDate);
//
//
//        String lunarDate = convertSolarToLunar(date);
//        tvAm.setText(lunarDate);
//    }
//
//    private String convertSolarToLunar(CalendarDay date) {
//        ChineseCalendar chineseCalendar = new ChineseCalendar();
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(date.getYear(), date.getMonth(), date.getDay());
//        chineseCalendar.setTimeInMillis(calendar.getTimeInMillis());
//
//        int lunarYear = chineseCalendar.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
//        int lunarMonth = chineseCalendar.get(ChineseCalendar.MONTH) + 1; // Add 1 because MONTH is 0-based
//        int lunarDay = chineseCalendar.get(ChineseCalendar.DAY_OF_MONTH);
//        boolean isLeapMonth = chineseCalendar.get(ChineseCalendar.IS_LEAP_MONTH) == 1;
//
//        // Format the lunar date
//        String lunarDate = (isLeapMonth ? "Leap " : "") + lunarDay + "/" + lunarMonth + "/" + lunarYear;
//
//        return lunarDate;
//    }
//}
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import vn.edu.tlu.nhom7.calendar.R;
import vn.edu.tlu.nhom7.calendar.adapter.EventAdapter;
import vn.edu.tlu.nhom7.calendar.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendar;
    private CalendarDay selectedDate;
    private TextView tvDuong, tvAm;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private List<Event> filteredEventList;

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendar = rootView.findViewById(R.id.calendarView);
        tvDuong = rootView.findViewById(R.id.tv_duong);
        tvAm = rootView.findViewById(R.id.tv_am);
        recyclerView = rootView.findViewById(R.id.list_event);

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
                updateEventListForSelectedMonth(selectedDate);
                calendar.invalidateDecorators();
            }
        });

        loadEventData();
        filteredEventList = new ArrayList<>();
        eventAdapter = new EventAdapter(filteredEventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventAdapter);

        updateEventListForSelectedMonth(selectedDate);

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


        String lunarDate = (isLeapMonth ? "Leap " : "") + lunarDay + "/" + lunarMonth + "/" + lunarYear;

        return lunarDate;
    }

    private void loadEventData() {
        eventList = new ArrayList<>();
        eventList.add(new Event("01/01", "Tết Dương lịch"));
        eventList.add(new Event("14/02", "Ngày lễ tình nhân"));
        eventList.add(new Event("03/02", "Ngày thành lập Đảng Cộng sản Việt Nam"));
        eventList.add(new Event("27/02", "Ngày Thầy thuốc Việt Nam"));
        eventList.add(new Event("08/03", "Ngày Quốc tế Phụ nữ"));
        eventList.add(new Event("20/03", "Ngày Quốc tế Hạnh phúc"));
        eventList.add(new Event("26/03", "Ngày thành lập Đoàn TNCS Hồ Chí Minh"));
        eventList.add(new Event("22/04", "Ngày Trái Đất"));
        eventList.add(new Event("30/04", "Ngày Giải phóng miền Nam, thống nhất đất nước"));
        eventList.add(new Event("01/05", "Ngày Quốc tế Lao động"));
        eventList.add(new Event("19/05", "Ngày sinh Chủ tịch Hồ Chí Minh"));
        eventList.add(new Event("01/06", "Ngày Quốc tế Thiếu nhi"));
        eventList.add(new Event("28/06", "Ngày Gia đình Việt Nam"));
        eventList.add(new Event("27/07", "Ngày Thương binh liệt sĩ"));
        eventList.add(new Event("19/08", "Ngày Cách mạng tháng Tám thành công"));
        eventList.add(new Event("02/09", "Ngày Quốc khánh"));
        eventList.add(new Event("10/10", "Ngày Giải phóng Thủ đô"));
        eventList.add(new Event("20/10", "Ngày Phụ nữ Việt Nam"));
        eventList.add(new Event("20/11", "Ngày Nhà giáo Việt Nam"));
        eventList.add(new Event("22/12", "Ngày thành lập Quân đội Nhân dân Việt Nam"));
    }


    private void updateEventListForSelectedMonth(CalendarDay date) {
        int selectedMonth = date.getMonth() + 1; // Vì tháng trong CalendarDay bắt đầu từ 0
        filteredEventList.clear();

        for (Event event : eventList) {
            String[] parts = event.getDate().split("/");
            int eventMonth = Integer.parseInt(parts[1]);
            if (eventMonth == selectedMonth) {
                filteredEventList.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
    }
}
