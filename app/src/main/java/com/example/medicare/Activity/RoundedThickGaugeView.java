package com.example.medicare.Activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.medicare.R;

import java.util.HashMap;
import java.util.Map;

public class RoundedThickGaugeView extends View {
    // Default values
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#E0E0E0");
    private static final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#4A90E2");
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_SUBTITLE_COLOR = Color.GRAY;

    // Days of the week mapping
    private static final Map<String, String> DAY_ABBREVIATIONS = new HashMap<String, String>() {{
        put("Monday", "Mon");
        put("Tuesday", "Tue");
        put("Wednesday", "Wed");
        put("Thursday", "Thu");
        put("Friday", "Fri");
        put("Saturday", "Sat");
        put("Sunday", "Sun");
    }};

    // Customizable attributes
    private int currentValue;
    private int totalValue;
    private String dayOfWeek;
    private String label;

    // Customizable colors
    private int backgroundColor;
    private int progressColor;
    private int textColor;
    private int subtitleColor;

    // Paints
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint mainTextPaint;
    private Paint labelPaint;
    private Paint subtitlePaint;

    public RoundedThickGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Parse custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedThickGaugeView);

        // Default values with option to customize via XML
        currentValue = a.getInt(R.styleable.RoundedThickGaugeView_currentValue, 0);
        totalValue = a.getInt(R.styleable.RoundedThickGaugeView_totalValue, 2);
        dayOfWeek = a.getString(R.styleable.RoundedThickGaugeView_dayOfWeek);
        label = a.getString(R.styleable.RoundedThickGaugeView_label);

        // Color attributes
        backgroundColor = a.getColor(R.styleable.RoundedThickGaugeView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        progressColor = a.getColor(R.styleable.RoundedThickGaugeView_progressColor, DEFAULT_PROGRESS_COLOR);
        textColor = a.getColor(R.styleable.RoundedThickGaugeView_textColor, DEFAULT_TEXT_COLOR);
        subtitleColor = a.getColor(R.styleable.RoundedThickGaugeView_subtitleColor, DEFAULT_SUBTITLE_COLOR);

        // Recycle the TypedArray
        a.recycle();

        // Initialize paints
        setupPaints();
    }

    private void setupPaints() {
        // Background paint - thicker and rounded
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(50f); // Increased thickness
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);

        // Progress paint - thicker and rounded
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(50f); // Increased thickness
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // Main value text paint
        mainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainTextPaint.setColor(textColor);
        mainTextPaint.setTextSize(80f);

        mainTextPaint.setTextAlign(Paint.Align.CENTER);
        mainTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Label paint
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(textColor);
        labelPaint.setTextSize(50f);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        // Subtitle paint for day
        subtitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subtitlePaint.setColor(subtitleColor);
        subtitlePaint.setTextSize(40f);
        subtitlePaint.setTextAlign(Paint.Align.CENTER);
    }

    // Method to update gauge dynamically
    public void updateGauge(int currentValue, int totalValue, String dayOfWeek, String label) {
        this.currentValue = currentValue;
        this.totalValue = totalValue;
        this.dayOfWeek = dayOfWeek;
        this.label = label;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Get the center and radius
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - 60f;

        // Draw background arc
        RectF rectF = new RectF(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius);
        canvas.drawArc(rectF, -240f, 300f, false, backgroundPaint);

        // Calculate progress
        float sweepAngle = (currentValue / (float)totalValue) * 300f;
        canvas.drawArc(rectF, -240f, sweepAngle, false, progressPaint);

        // Draw main value text (Current/Total)
        String valueText = currentValue + "/" + totalValue;
        canvas.drawText(valueText, centerX, centerY, mainTextPaint);

        // Draw label below the value
        if (label != null) {
            canvas.drawText(label, centerX, centerY + 50f, labelPaint);
        }

        // Draw day of week (abbreviated)
        if (dayOfWeek != null) {
            String abbreviatedDay = DAY_ABBREVIATIONS.getOrDefault(dayOfWeek, dayOfWeek);
            canvas.drawText(abbreviatedDay, centerX, centerY + 100f, subtitlePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Make the view square
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
}