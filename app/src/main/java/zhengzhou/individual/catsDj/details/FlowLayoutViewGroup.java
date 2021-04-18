package zhengzhou.individual.catsDj.details;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public final class FlowLayoutViewGroup extends ViewGroup {
    private static final String TAG = "FlowLayout";
    private int mHorizontalSpacing = dp2px(16);
    private int mVerticalSpacing = dp2px(8);
    private List<List<View>> allLines;
    private List<Integer> lineHeights;

    public FlowLayoutViewGroup(Context context) {
        super(context);
    }

    public FlowLayoutViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initMeasureParams() {
        allLines = new ArrayList<>();
        lineHeights = new ArrayList<>();
    }

    public FlowLayoutViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = allLines.size();
        int curL = getPaddingLeft();
        int curT = getPaddingTop();

        for (int i = 0; i < lineCount; i++) {
            List<View> lineViews = allLines.get(i);
            int lineHeight = lineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View view = lineViews.get(j);
                int left = curL;
                int top = curT;
                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();
                view.layout(left, top, right, bottom);
                curL = right + mHorizontalSpacing;
            }
            curL = getPaddingLeft();
            curT += lineHeight + mVerticalSpacing;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initMeasureParams();
        int childCount = getChildCount();

        //viewgroup 自身的 padding
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        List<View> lineViews = new ArrayList<>();
        int lineWidthUsed = 0; // 记录这行用了多宽的 size
        int lineHeight = 0;  //一行的height；

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        int parentNeededWidth = 0; // measure 中， 子 view 要求的父ViewGroup 宽；
        int parentNeededHeight = 0; // measure 中， 子 view 要求的父ViewGroup 高；

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams layoutParams = childView.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                    layoutParams.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                    layoutParams.height);
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            // 获取子view的宽和高
            int childMeasuredWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();

            if (childMeasuredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);
                parentNeededHeight += lineHeight + mVerticalSpacing;

                allLines.add(lineViews);
                lineHeights.add(lineHeight);

                lineViews = new ArrayList<>();
                lineWidthUsed = 0;
                lineHeight = 0;
            }
            lineViews.add(childView);
            lineWidthUsed += childMeasuredWidth + mHorizontalSpacing;
            lineHeight = Math.max(lineHeight, childMeasureHeight);
        }
        if (lineViews.size() != 0) {
            parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing);
            parentNeededHeight += lineHeight + mVerticalSpacing;
            allLines.add(lineViews);
            lineHeights.add(lineHeight);
        }
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = (wMode == MeasureSpec.EXACTLY) ? selfWidth : parentNeededWidth;
        int realHeight = (hMode == MeasureSpec.EXACTLY) ? selfHeight : parentNeededHeight;
        //测自己
        setMeasuredDimension(realWidth, realHeight);
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().getDisplayMetrics());
    }
}
