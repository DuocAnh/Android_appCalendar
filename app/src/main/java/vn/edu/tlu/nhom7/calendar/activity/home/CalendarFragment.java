package vn.edu.tlu.nhom7.calendar.activity.home;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import vn.edu.tlu.nhom7.calendar.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        try {
            highlightDates(eventList);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

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
        eventList.add(new Event("07/05", "Ngày Chiến thắng Điện Biên Phủ"));
        eventList.add(new Event("09/05", "Ngày của Mẹ"));
        eventList.add(new Event("19/05", "Ngày sinh Chủ tịch Hồ Chí Minh"));
        eventList.add(new Event("01/06", "Ngày Quốc tế Thiếu nhi"));
        eventList.add(new Event("05/06", "Ngày Môi trường Thế giới"));
        eventList.add(new Event("17/06", "Ngày phòng chống sa mạc hóa và hạn hán thế giới"));
        eventList.add(new Event("28/06", "Ngày Gia đình Việt Nam"));
        eventList.add(new Event("11/07", "Ngày Dân số Thế giới"));
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

    private void highlightDates(List<Event> eventList) throws ParseException {
        for (Event event : eventList) {
            String date;
            int color = ContextCompat.getColor(requireContext(), R.color.color_hightlighEvent);

            for (int year = 2020; year <= 2029; year++) {
                date = event.getDate() + "/" + year;
                try {
                    Date eventDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    CurrentDayDecorator decorator = new CurrentDayDecorator(eventDate, color);
                    calendar.addDecorator(decorator);
                } catch (ParseException e) {
                }
            }
        }
    }
}
