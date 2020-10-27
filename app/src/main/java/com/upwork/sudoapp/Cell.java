package com.upwork.sudoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.HashMap;
import java.util.Map;


@SuppressLint({"ResourceAsColor", "ViewConstructor"})
public class Cell extends AppCompatTextView {
    static public int CELL_DEFAULT_TEXT_SIZE = 18;
    static public int CELL_HEIGHT;
    static private final int[] indexOfNumber = {0, 0, 3, 6, 8, 11, 14, 16, 19, 22}; // in format cell string
    //static final int MARKED_CELL_COLOR = R.color.MARKED_CELL_COLOR;
    static public final Map<Integer, Integer> maskToNumber = new HashMap<Integer, Integer>() {
        {
            put(0, 0);
            put(1, 0);
            put(2, 1);
            put(4, 2);
            put(8, 3);
            put(16, 4);
            put(32, 5);
            put(64, 6);
            put(128, 7);
            put(256, 8);
            put(512, 9);
        }
    };

    private int defaultColor, highlightColor, markedColor;
    private boolean isLocked, isMarked;
    private int mask, index;

    public Cell(Context context, int index, int highlightColor, int defaultColor) {
        super(context);
        this.index = index;
        this.defaultColor = defaultColor;
        this.highlightColor = highlightColor;

        //markedColor = R.color.MARKED_CELL_COLOR;
        setHeight(CELL_HEIGHT);
        setGravity(Gravity.CENTER);
        setTypeface(AppConstant.APP_FONT);
        setBackgroundResource(defaultColor);

        if (highlightColor == R.color.HIGHLIGHT_LOCKED_CELL_COLOR) {
            isLocked = true;
            setTextColor(Color.BLACK);
        } else {
            isLocked = false;
            //setTextColor(Color.parseColor("#0067ce"));
        }
    }

    CellState getState() {
        CellState state = new CellState();
        state.mask = mask;
        state.index = index;
        return state;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean state) {
        isMarked = state;
        setBackgroundResource(isMarked ? markedColor : R.color.TARGET_CELL_COLOR);
    }

    public void addNumber(int number) {
        setMask(mask ^ (1 << number));
    }

    public int getIndex() {
        return index;
    }

    public int getMask() {
        return mask;
    }

    public int getNumber() {
        return maskToNumber.get(mask);
    }

    public void setHighLight() {
        //int color = isMarked ? MARKED_CELL_COLOR : highlightColor;
        setBackgroundResource(highlightColor);
    }

    public void setNoHighLight() {
        //int color = isMarked ? MARKED_CELL_COLOR : defaultColor;
        setBackgroundResource(defaultColor);
    }

    public void setNumber(Integer number) {
        int mask = (1 << number);
        setMask(mask);
    }

    public void setMask(int mask) {
        this.mask = mask;
        if (mask != 0 && mask % 2 == 0) {
            int counter = Integer.bitCount(mask);
            if (counter > 1 || (counter == 1 && GameActivity.notesActive == 1)) {
                char[] format = "1  2  3\n4  5  6\n7  8  9".toCharArray();
                for (int x = 1; x <= 9; ++x) {
                    if ((mask >> x) % 2 == 0) {
                        format[indexOfNumber[x]] = ' ';
                    }
                }
                setTextSize(CELL_DEFAULT_TEXT_SIZE >> 1);
                setText(String.valueOf(format));
                setTextColor(Color.BLACK);
            } else {
                setTextSize(CELL_DEFAULT_TEXT_SIZE);
                setText(String.valueOf(maskToNumber.get(mask)));
                setTextColor(Color.BLACK);
            }
        } else {
            this.mask = 0;
            setText("");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GameActivity.highlightNeighborCells(index);
        if (!isLocked) {
            setBackgroundResource(R.color.TARGET_CELL_COLOR);
        }
        GameActivity.setSelectedCell(index);
        GameActivity.updateNumpad();
        GameActivity.highlightErrorValueCells();
        return true;
    }
}
