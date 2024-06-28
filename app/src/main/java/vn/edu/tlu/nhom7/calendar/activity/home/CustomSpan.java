package vn.edu.tlu.nhom7.calendar.activity.home;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class CustomSpan implements LineBackgroundSpan {
    private final float radius;
    private final int color;

    public CustomSpan(float radius, int color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void drawBackground(
            Canvas canvas,
            Paint paint,
            int left,
            int right,
            int top,
            int baseline,
            int bottom,
            CharSequence charSequence,
            int start,
            int end,
            int lineNum) {

        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((left + right) / 2, bottom + radius * 2, radius, paint);
        paint.setColor(oldColor);
    }
}
