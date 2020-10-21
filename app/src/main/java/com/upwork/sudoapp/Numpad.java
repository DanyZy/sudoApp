package com.upwork.sudoapp;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.GridView;

import java.util.ArrayList;

public class Numpad {
    static final int[] numpadPosition = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

    private Context mContext;
    private GridView mGridView1;
    private GridView mGridView2;
    private NumpadButton notesBtn;

    public Numpad (Context context) {
        mContext = context;
        mGridView1 = ((Activity) context).findViewById(R.id.grid_numpad1);
        mGridView2 = ((Activity) context).findViewById(R.id.grid_numpad2);
        init();
    }

    private void init () {
        ArrayList<NumpadButton> buttons = new ArrayList<>();
        ArrayList<NumpadButton> nums = new ArrayList<>();
        for (int pos = 10; pos <= 13; ++pos) {
            buttons.add(new NumpadButton(mContext, pos));
        }
        for (int pos = 1; pos <= 9; ++pos) {
            nums.add(new NumpadButton(mContext, pos));
        }

        NumpadAdapter numpadAdapter1 = new NumpadAdapter(mContext, buttons);
        mGridView1.setAdapter(numpadAdapter1);
        NumpadAdapter numpadAdapter2 = new NumpadAdapter(mContext, nums);
        mGridView2.setAdapter(numpadAdapter2);
    }

    public void update (int mask) {
        for (int x = 1; x <= 9; ++x) {
            NumpadButton button = (NumpadButton) mGridView2.getChildAt(numpadPosition[x]);
            boolean marked = (mask >> x) % 2 == 1;
            //Log.d("mask", String.valueOf((mask >> x) % 2));
            if (button != null)
                button.setBackgroundColor(marked);
        }
        notesBtn = (NumpadButton) mGridView1.getChildAt(numpadPosition[1]);
        if (notesBtn != null) {
            Log.d("notes null", "nope");
            if (GameActivity.notesActive == 1) {
                notesBtn.setBackgroundColor(true);
            } else {
                notesBtn.setBackgroundColor(false);
            }
        }
    }
}
