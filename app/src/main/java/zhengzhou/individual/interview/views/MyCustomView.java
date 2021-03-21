package zhengzhou.individual.interview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import zhengzhou.individual.interview.R;

public class MyCustomView extends androidx.appcompat.widget.AppCompatTextView {

    private int mColor = Color.RED;
    private String mText = "Try to add ids";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MyCustomView(Context context) {
        super(context);
        init();
    }
    public MyCustomView(Context context, AttributeSet attrs){
        this(context, attrs, 0);//注意不是super(context,attrs,0);
        init();
    }

    public MyCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCustomView);
        mColor = typedArray.getColor(R.styleable.MyCustomView_customColor, Color.RED);
        if (typedArray.getText(R.styleable.MyCustomView_customText) != null) {
            mText = typedArray.getText(R.styleable.MyCustomView_customText).toString();
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mText, 100, 100, mPaint);
    }
}
