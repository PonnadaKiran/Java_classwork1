import java.util.Arrays;
import java.util.*;

public class su {
    private int[][] board;
    private static final int BOARD_SIZE = 9;
    private static final int BOX_SIZE = 3;

    public su() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOX_SIZE == 0) {
                System.out.println("+-------+-------+-------+");
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (j % BOX_SIZE == 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("+-------+-------+-------+");
    }

    public boolean solve() {
        int row = -1;
        int col = -1;
        boolean isComplete = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    row = i;
                    col = j;
                    isComplete = false;
                    break;
                }
            }
            if (!isComplete) {
                break;
            }
        }
        if (isComplete) {
            return true;
        }
        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solve()) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }
        int boxRow = row - row % BOX_SIZE;
        int boxCol = col - col % BOX_SIZE;
        for (int i = boxRow; i < boxRow + BOX_SIZE; i++) {
            for (int j = boxCol; j < boxCol + BOX_SIZE; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        su game = new su();
        int[][] board = {
                { 0, 0, 3, 0, 2, 0, 6, 0, 0 },
                { 9, 0, 0, 3, 0, 5, 0, 0, 1 },
                { 0, 0, 1, 8, 0, 6, 4, 0, 0 },
                { 0, 0, 8, 1, 0, 2, 9, 0, 0 },
                { 7, 0, 0, 0, 0, 0, 0, 0, 8 },
                { 0, 0, 6, 7, 0, 8, 2, 0, 0 },
                { 0, 0, 2, 6, 0, 2, 5, 9, 0 },
                { 0, 9, 5, 0, 0, 1, 3, 0, 0 },
                { 8, 0, 0, 2, 0, 3, 0, 0, 9 },
                { 0, 0, 7, 0, 4, 0, 5, 0, 0 }
        };
        game.setBoard(board);
        System.out.println("Initial board:");
        game.printBoard();
        if (game.solve()) {
            System.out.println("Solved board:");
            game.printBoard();
        } else {
            System.out.println("Unsolvable board.");
        }
    }
}
