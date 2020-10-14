package com.upwork.sudoapp;
import android.content.ContentValues;

public class GameState {
    private int status;
    private int difficulty;
    private int[][] solution = new int[9][9];
    private int[][] grid = new int[9][9];

    public GameState (int status, int difficulty, int[][] solution, int[][] grid) {
        this.status = status;
        this.difficulty = difficulty;
        for(int row = 0; row < 9; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.solution[row][col] = solution[row][col];
                this.grid[row][col] = grid[row][col];
            }
        }
    }

    public ContentValues getContentValues () {
        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("difficulty", difficulty);
        values.put("solutionString", getString(solution));
        values.put("gridString", getString(grid));
        return values;
    }
    public String getString(int[][] arr) {
        StringBuilder builder = new StringBuilder();
        for(int row = 0; row < 9; ++row) {
            for(int col = 0; col < 9; ++col) {
                builder.append(arr[row][col]).append(" ");
            }
        }
        return builder.toString();
    }
}
