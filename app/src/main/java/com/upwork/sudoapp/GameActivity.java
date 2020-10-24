package com.upwork.sudoapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Scanner;
import java.util.Stack;

import static android.widget.Toast.LENGTH_LONG;

public class GameActivity extends AppCompatActivity {
    static public final int[] NUMBER_OF_EMPTY_CELLS = {0, 30, 35, 45, 50};
    static public final String[] DIFFICULT_NAME = {"NONE", "Easy", "Normal", "Hard", "Extreme"};

    @SuppressLint("StaticFieldLeak")
    static public SudokuGrid grid;
    @SuppressLint("StaticFieldLeak")
    static private Numpad numpad;
    static private Stack<CellState> stack = new Stack<>();
    static private Stack<CellState> redostack = new Stack<>();

    SudokuSolver solver = new SudokuSolver();


    /* game state */
    private int[][] solution;
    private int difficulty;
    static private int status; // -3 game done | -2: auto solved | -1: auto fill | 0: playing | 1: player solved
    static public int notesActive = -1;
    private Timer timer;

    private void generateGrid() {
        // generate a grid
        solution = solver.getRandomGrid(NUMBER_OF_EMPTY_CELLS[difficulty]);
        int[][] masks = new int[9][9];

        // compute masks
        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                if(solution[row][col] > 9) {
                    masks[row][col] = 0;
                }
                else {
                    masks[row][col] = (1 << solution[row][col]);
                }
            }
        }

        grid = new SudokuGrid(this, masks, solution);
    }

    private void restoreGrid(String solutionString, String gridString) {
        // restore solution
        solution = new int[9][9];
        Scanner scanner = new Scanner(solutionString);
        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                solution[row][col] = scanner.nextInt();
            }
        }
        // restore masks
        int[][] masks = new int[9][9];
        scanner = new Scanner(gridString);
        for (int row = 0; row < 9; ++row) {
            for (int col = 0; col < 9; ++col) {
                masks[row][col] = scanner.nextInt();
            }
        }

        grid = new SudokuGrid(this, masks, solution);
    }

    private void saveGame() {
        if (status < -1) return;
        int[][] currentMask = grid.getCurrentMasks();
        GameState state = new GameState(status, difficulty, timer.getElapsedSeconds(), solution, currentMask);
        try {
            DatabaseHelper DBHelper = DatabaseHelper.newInstance(this);
            SQLiteDatabase database = DBHelper.getWritableDatabase();
            database.insert("GameState", null, state.getContentValues());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        saveGame();
        super.onPause();
    }

    boolean wannaBack = false;
    @Override
    public void onBackPressed() {
        if (wannaBack) {
            super.onBackPressed();
            return;
        }

        wannaBack = true;
        Toast.makeText(this, "Press 'BACK' again to return to the main menu.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wannaBack = false;
            }
        }, 3000);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                onClickReset();
                break;
            case R.id.action_tutorial:
                onClickTutorial();
                break;
            default:
                break;
        }

        return true;
    }

    private void onClickTutorial() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyTutorialTheme);
        Spannable message = SpannableWithImage.getTextWithImages(this, getString(R.string.tutorial), 50);

        dialog.setMessage(message).setTitle("Tutorial").show();
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        difficulty = bundle.getInt("difficulty", 0);
        status = bundle.getInt("status", 0);

        // lock screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // setup action bar title
        getSupportActionBar().setTitle(DIFFICULT_NAME[difficulty]);

        // hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setTypeface(AppConstant.APP_FONT);

        String solutionString = bundle.getString("solutionString", "none");
        String gridString = bundle.getString("gridString", "none");
        if (solutionString.equals("none") || gridString.equals("none")) {
            generateGrid();
        } else {
            restoreGrid(solutionString, gridString);
        }

        numpad = new Numpad(this);

        int elapsedTime = bundle.getInt("elapsedSeconds", 0);
        timer = new Timer(this, elapsedTime);
        timer.start();
    }

    public static void updateNumpad() {
        Cell selectedCell = grid.getSelectedCell();
        if (selectedCell != null) {
            numpad.update(selectedCell.getMask());
        }
    }

    public void onClickSubmit(View view) {

        if (solver.checkValidGrid(grid.getNumbers())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyTutorialTheme);
            Spannable message = SpannableWithImage.getTextWithImages(this, "Congratulations, you solve the puzzle!", 50);
            final Intent intent = new Intent(this, MainMenuActivity.class);
            dialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(intent);
                }
            });
            dialog.setMessage(message).setTitle("You won with time: " + timer.getElapsedTimeString()).show();
            //Toast.makeText(this, "Congratulations, you solve the puzzle!", LENGTH_LONG).show();
            // set status to GAME_DONE
            status = -3;
            timer.stop();
        } else {
            Toast.makeText(this, "Try again, answer is wrong.", LENGTH_LONG).show();
        }
    }

    private void onClickReset() {
        if (status < -1) return;
        grid.clear();
        updateNumpad();
    }

    public static void onPressNumpad(int number) {
        if(status < -1) return;
        Cell selectedCell = grid.getSelectedCell();
        if (selectedCell != null) {
            if (number < 10 && !selectedCell.isLocked()) {
                // backup current selected cell state
                stack.push(selectedCell.getState());
                if (notesActive == -1) {
                    selectedCell.setNumber(number);
                    highlightSameValueCells(selectedCell.getIndex());
                    highlightErrorValueCells();
                } else {
                    selectedCell.addNumber(number);
                }
            } else if (number == 12) {
                if (!redostack.isEmpty()) {
                    CellState preState = redostack.peek();
                    stack.push(preState);
                    redostack.pop();
                    int index = preState.index;
                    int row = index / 9;
                    int col = index - row * 9;
                    grid.getCell(row, col).setMask(preState.mask);
                }
                highlightSameValueCells(selectedCell.getIndex());
                highlightErrorValueCells();
            } else if (number == 11) {
                selectedCell.setNumber(0);
                highlightSameValueCells(selectedCell.getIndex());
                highlightErrorValueCells();
            } else if (number == 10) {
                notesActive *= -1;
            } else if (number == 13) {
                if (!selectedCell.isLocked()) {
                    setTipCell(selectedCell.getIndex());
                    highlightErrorValueCells();
                }
            } else if (number == 14) {
                // restore previous selected cell state
                if (!stack.isEmpty()) {
                    CellState preState = stack.peek();
                    redostack.push(preState);
                    stack.pop();
                    int index = preState.index;
                    int row = index / 9;
                    int col = index - row * 9;
                    grid.getCell(row, col).setMask(preState.mask);
                }
                highlightSameValueCells(selectedCell.getIndex());
                highlightErrorValueCells();
            }
        }
        updateNumpad();
    }

    public static void highlightNeighborCells(int index) { grid.highlightNeighborCells(index); }

    public static void highlightSameValueCells(int index) { grid.highlightSameValueCells(index); }

    public static void highlightErrorValueCells() { grid.highlightErrorValues(); }

    public static void setSelectedCell(int index) { grid.setSelectedCell(index); }

    public static void setTipCell(int index) {grid.setTipCell(index); }
}
