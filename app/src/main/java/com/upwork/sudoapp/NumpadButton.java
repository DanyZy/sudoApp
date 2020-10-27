package com.upwork.sudoapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;


@SuppressLint({"AppCompatCustomView", "ViewConstructor"})
public class NumpadButton extends TextView {
    final private int MARKED_COLOR = R.color.NUMPAD_BUTTON_MARKED_COLOR;
    final private int UNMARKED_COLOR = R.color.NUMPAD_BUTTON_UNMARKED_COLOR;
    private String[] buttonText = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "NOTES", "CLEAR", "REDO", "TIP: " + GameActivity.grid.tipCounter, "UNDO"};
    final private int[] index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    private int position;

    @SuppressLint("ResourceAsColor")
    public NumpadButton(Context context, int position) {
        super(context);
        this.position = position;
        setBackgroundResource(R.color.NUMPAD_BUTTON_UNMARKED_COLOR);
        setText(buttonText[position]);
        setGravity(Gravity.CENTER);
        setTypeface(AppConstant.APP_FONT);
        setHeight(Cell.CELL_HEIGHT);

        if(position >= 10) {
            setTextSize(12);
            setTextColor(Color.BLACK);
        }
        else {
            setTextSize(20);
            setTextColor(Color.parseColor("#0067ce"));
        }
    }

    public void changeName() {
        this.setText("TIP: " + GameActivity.grid.tipCounter);
    }

    public int getIndex() {
        return index[position];
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (getIndex() > 10) {
                Log.d("action", "down");
                setBackgroundColor(true);
            }
            GameActivity.onPressNumpad(getIndex());
        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (getIndex() > 10) {
                Log.d("action", "up");
                setBackgroundColor(false);
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            if (getIndex() > 10) {
                setBackgroundColor(false);
            }
        }
        return true;
    }

    public void setBackgroundColor(boolean marked) {
        setBackgroundResource(marked ? MARKED_COLOR : UNMARKED_COLOR);
    }
}
