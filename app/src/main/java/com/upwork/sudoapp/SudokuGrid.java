package com.upwork.sudoapp;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SudokuGrid {
    private Context mContext;
    private GridView mGridView;
    private Cell[][] mCells = new Cell[9][9];
    private Box[] mBoxes = new Box[9];
    private Cell mSelectedCell;
    private int[][] mSolution = new int[9][9];

    private ArrayList<Cell> cellList = new ArrayList<>();

    public SudokuGrid(Context context, int[][] masks, int[][] solution) {
        mContext = context;
        mGridView = ((Activity) context).findViewById(R.id.grid_sudoku);

        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                int box = (row / 3) * 3 + col / 3;
                // copy solution
                mSolution[row][col] = solution[row][col];

                // initialize cell
                int highlightColor = (solution[row][col] <= 9) ? R.color.HIGHLIGHT_LOCKED_CELL_COLOR : R.color.HIGHLIGHT_EMPTY_CELL_COLOR;
                int defaultColor = (box % 2 == 0) ? R.color.EVEN_BOX_COLOR : R.color.ODD_BOX_COLOR;

                mCells[row][col] = new Cell(context.getApplicationContext(), row * 9 + col, highlightColor, defaultColor);
                mCells[row][col].setMask(masks[row][col]);
            }
        }

        // initialize grid
        for (int i = 0; i < 9; ++i) {
            mBoxes[i] = new Box();
            BoxAdapter boxAdapter = new BoxAdapter(i);
            mBoxes[i].setAdapter(boxAdapter);
        }

        SudokuGridAdapter gridAdapter = new SudokuGridAdapter(mBoxes);
        mGridView.setAdapter(gridAdapter);
    }

    public int[][] getCurrentMasks() {
        int[][] values = new int[9][9];
        for(int row = 0; row < 9; ++row) {
            for(int col = 0; col < 9; ++col) {
                values[row][col] = mCells[row][col].getMask();
            }
        }
        return values;
    }

    public int[][] getNumbers () {
        int[][] values = new int[9][9];
        for(int row = 0; row < 9; ++row) {
            for(int col = 0; col < 9; ++col) {
                values[row][col] = mCells[row][col].getNumber();
            }
        }
        return values;
    }

    public void fill() {
        boolean[][] rows = new boolean[9][10];
        boolean[][] cols = new boolean[9][10];
        boolean[][] boxes = new boolean[9][10];

        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                if (mCells[row][col].isLocked()) {
                    int box = (row / 3) * 3 + col / 3;
                    int number = mCells[row][col].getNumber();
                    rows[row][number] = true;
                    cols[col][number] = true;
                    boxes[box][number] = true;
                }
            }
        }

        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                if (!mCells[row][col].isLocked()) {
                    int mask = 1022; // full numbers = 2^10 - 2.
                    int box = (row / 3) * 3 + col / 3;
                    for (int x = 1; x <= 9; ++x) {
                        if (rows[row][x] || cols[col][x] || boxes[box][x]) mask &= ~(1 << x);
                    }
                    mCells[row][col].setMask(mask);
                }
            }
        }
    }

    public Cell getSelectedCell() {
        return mSelectedCell;
    }

    public void setSelectedCell(int index) {
        int row = index / 9;
        int col = index - row * 9;
        mSelectedCell = mCells[row][col];
    }

    public boolean isLegalGrid() {
        for (Cell[] row : mCells) {
            for (Cell cell : row) {
                if (Integer.bitCount(cell.getMask()) > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clear() {
        for (Cell[] row : mCells) {
            for (Cell cell : row) {
                if (!cell.isLocked()) {
                    cell.addNumber(0);
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        return mCells[row][col];
    }

    public void highlightNeighborCells(int index) {
        int row = index / 9;
        int col = index - row * 9;
        int box = (row / 3) * 3 + col / 3;

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                int k = (i / 3) * 3 + j / 3;
                if (i == row || j == col || k == box) {
                    mCells[i][j].setHighLight();
                } else {
                    mCells[i][j].setNoHighLight();
                }
            }
        }
    }

    public void highlightSameValueCells(int index) {
        int row = index / 9;
        int col = index - row * 9;
        int box = (row / 3) * 3 + col / 3;

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (mSelectedCell != null) {
                    int k = (i / 3) * 3 + j / 3;
                    try {
                        if (mSelectedCell.getNumber() == mCells[i][j].getNumber() && mSelectedCell.getNumber() != 0) {
                            mCells[i][j].setBackgroundResource(R.color.TARGET_CELL_COLOR);
                        } else if (i == row || j == col || k == box) {
                            mCells[i][j].setHighLight();
                        } else {
                            mCells[i][j].setNoHighLight();
                        }
                    } catch (NullPointerException ignored){}
                }
            }
        }
    }

    public void highlightErrorValues() {
        Map<Cell, Set<Cell>> mapR = new HashMap<Cell, Set<Cell>>();
        Map<Cell, Set<Cell>> mapC = new HashMap<Cell, Set<Cell>>();
        Map<Cell, Set<Cell>> mapB = new HashMap<Cell, Set<Cell>>();

        Cell[] rows

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {

            }
        }
    }

    public void highlightErrorValueCells(int index) {
        int row = index / 9;
        int col = index - row * 9;
        int box = (row / 3) * 3 + col / 3;

        Map<Cell, Set<Cell>> mapR = new HashMap<Cell, Set<Cell>>();
        Map<Cell, Set<Cell>> mapC = new HashMap<Cell, Set<Cell>>();
        Map<Cell, Set<Cell>> mapB = new HashMap<Cell, Set<Cell>>();


        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                for (int r = 0; r < 9; ++r) {
                    for (int c = 0; c < 9; ++c) {
                        int b = (r / 3) * 3 + c / 3;
                        if (r == row) {
                            if (mapR.containsKey(mCells[i][j])) {
                                mapR.get(mCells[i][j]).add(mCells[r][c]);
                            } else {
                                Set<Cell> set = new HashSet<Cell>();
                                set.add(mCells[r][c]);
                                mapR.put(mCells[i][j], set);
                            }
                        }
                        if (c == col) {
                            if (mapC.containsKey(mCells[i][j])) {
                                mapC.get(mCells[i][j]).add(mCells[r][c]);
                            } else {
                                Set<Cell> set = new HashSet<Cell>();
                                set.add(mCells[r][c]);
                                mapC.put(mCells[i][j], set);
                            }
                        }
                        if (b == box) {
                            if (mapB.containsKey(mCells[i][j])) {
                                mapB.get(mCells[i][j]).add(mCells[r][c]);
                            } else {
                                Set<Cell> set = new HashSet<Cell>();
                                set.add(mCells[r][c]);
                                mapB.put(mCells[i][j], set);
                            }
                        }
                    }
                }
            }
        }

        Map<Integer, Set<Cell>> map2 = new HashMap<Integer, Set<Cell>>();
        Map<Integer, Set<Cell>> map3 = new HashMap<Integer, Set<Cell>>();
        Map<Integer, Set<Cell>> map4 = new HashMap<Integer, Set<Cell>>();

        for (Map.Entry<Cell, Set<Cell>> entry : mapR.entrySet()) {
            for (Cell cell : entry.getValue()) {
                if (map2.containsKey(cell.getNumber()))
                    map2.get(cell.getNumber()).add(cell);
                else
                {
                    Set<Cell> set = new HashSet<Cell>();
                    set.add(cell);
                    map2.put(cell.getNumber(), set);
                }
            }
        }

        for (Map.Entry<Cell, Set<Cell>> entry : mapC.entrySet()) {
            for (Cell cell : entry.getValue()) {
                if (map3.containsKey(cell.getNumber()))
                    map3.get(cell.getNumber()).add(cell);
                else
                {
                    Set<Cell> set = new HashSet<Cell>();
                    set.add(cell);
                    map3.put(cell.getNumber(), set);
                }
            }
        }

        for (Map.Entry<Cell, Set<Cell>> entry : mapB.entrySet()) {
            for (Cell cell : entry.getValue()) {
                if (map4.containsKey(cell.getNumber()))
                    map4.get(cell.getNumber()).add(cell);
                else
                {
                    Set<Cell> set = new HashSet<Cell>();
                    set.add(cell);
                    map4.put(cell.getNumber(), set);
                }
            }
        }

        for (Map.Entry<Integer, Set<Cell>> entry : map2.entrySet()) {
            Log.d("size " + entry.getKey(), String.valueOf(entry.getValue().size()));
            for (Cell cell : entry.getValue()) {
                if (entry.getValue().size() > 1) {
                    //Log.d("cell ", String.valueOf(cell.getIndex()));
                    if (!cellList.contains(cell))
                        cellList.add(cell);
//                } else {
//                    cellList.remove(cell);
                }
            }
        }

        for (Map.Entry<Integer, Set<Cell>> entry : map3.entrySet()) {
            Log.d("size " + entry.getKey(), String.valueOf(entry.getValue().size()));
            for (Cell cell : entry.getValue()) {
                if (entry.getValue().size() > 1) {
                    //Log.d("cell ", String.valueOf(cell.getIndex()));
                    if (!cellList.contains(cell))
                        cellList.add(cell);
//                } else {
//                    cellList.remove(cell);
                }
            }
        }

        for (Map.Entry<Integer, Set<Cell>> entry : map4.entrySet()) {
            Log.d("size " + entry.getKey(), String.valueOf(entry.getValue().size()));
            for (Cell cell : entry.getValue()) {
                if (entry.getValue().size() > 1) {
                    //Log.d("cell ", String.valueOf(cell.getIndex()));
                    if (!cellList.contains(cell))
                        cellList.add(cell);
//                } else {
//                    cellList.remove(cell);
                }
            }
        }

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (cellList.contains(mCells[i][j])) {
                    mCells[i][j].setTextColor(Color.parseColor("#FF9A2525"));
                    Log.d("cell ", String.valueOf(mCells[i][j].getIndex()));
                } else {
                    if (!mCells[i][j].isLocked()) {
                        mCells[i][j].setTextColor(Color.parseColor("#0067ce"));
                    } else {
                        mCells[i][j].setTextColor(Color.BLACK);
                    }
                }
            }
        }
    }


    /* Grid adapter */
    public class SudokuGridAdapter extends BaseAdapter {
        Box[] mBoxes;

        public SudokuGridAdapter(Box[] boxes) {
            mBoxes = boxes;
        }
        @Override
        public int getCount() {
            return mBoxes.length;
        }

        @Override
        public Object getItem(int i) {
            return mBoxes[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return mBoxes[i];
        }
    }

    /* Box*/
    public class Box extends GridView {
        public Box() {
            super(mContext);
            setVerticalSpacing(AppConstant.BOX_LINE_SPACING);
            setHorizontalSpacing(AppConstant.BOX_LINE_SPACING);
            setNumColumns(3);
            setGravity(Gravity.CENTER);
            setBackgroundResource(R.color.GRID_BACKGROUND_COLOR);
            setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, AppConstant.BOX_HEIGHT));
        }
    }

    /* Box adapter */
    public class BoxAdapter extends BaseAdapter {
        private int mIndex;

        public BoxAdapter (int index) {
            this.mIndex = index;
        }

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int row = (mIndex / 3) * 3 + position / 3;
            int col = (mIndex % 3) * 3 + position % 3;
            return mCells[row][col];
        }
    }
}
