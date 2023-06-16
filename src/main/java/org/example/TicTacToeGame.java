package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGame extends JFrame {
    private final int BOARD_SIZE = 3;
    private final String PLAYER_MARK = "X";
    private final String COMPUTER_MARK = "O";

    private JButton[][] board;
    private boolean playerTurn;
    private boolean gameOver;

    public TicTacToeGame() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        playerTurn = true;
        gameOver = false;
    }

    private void initializeBoard() {
        board = new JButton[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 48));
                button.addActionListener(new CellClickListener(i, j));
                board[i][j] = button;
                add(button);
            }
        }

        setSize(300, 300);
        setVisible(true);
    }

    private void markCell(int row, int col, String mark) {
        if (board[row][col].getText().isEmpty() && !gameOver) {
            board[row][col].setText(mark);
            checkGameOver(row, col, mark);
            playerTurn = !playerTurn;

            if (!playerTurn && !gameOver) {
                computerMove();
            }
        }
    }

    private void checkGameOver(int row, int col, String mark) {
        // Check row
        boolean rowMatch = true;
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (!board[row][j].getText().equals(mark)) {
                rowMatch = false;
                break;
            }
        }

        // Check column
        boolean colMatch = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[i][col].getText().equals(mark)) {
                colMatch = false;
                break;
            }
        }

        // Check diagonals
        boolean diag1Match = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[i][i].getText().equals(mark)) {
                diag1Match = false;
                break;
            }
        }

        boolean diag2Match = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (!board[i][BOARD_SIZE - 1 - i].getText().equals(mark)) {
                diag2Match = false;
                break;
            }
        }

        if (rowMatch || colMatch || diag1Match || diag2Match) {
            String winner = playerTurn ? "Player" : "Computer";
            JOptionPane.showMessageDialog(this, winner + " wins!");
            gameOver = true;
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            gameOver = true;
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void computerMove() {
        int[] bestMove = findBestMove();
        int row = bestMove[0];
        int col = bestMove[1];
        board[row][col].setText(COMPUTER_MARK);
        checkGameOver(row, col, COMPUTER_MARK);
        playerTurn = true;
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j].getText().isEmpty()) {
                    board[i][j].setText(COMPUTER_MARK);
                    int score = minimax(0, false);
                    board[i][j].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(int depth, boolean isMaximizingPlayer) {
        if (checkWinningMove(PLAYER_MARK)) {
            return -10 + depth;
        }

        if (checkWinningMove(COMPUTER_MARK)) {
            return 10 - depth;
        }

        if (isBoardFull()) {
            return 0;
        }

        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j].getText().isEmpty()) {
                        board[i][j].setText(COMPUTER_MARK);
                        int score = minimax(depth + 1, false);
                        board[i][j].setText("");
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j].getText().isEmpty()) {
                        board[i][j].setText(PLAYER_MARK);
                        int score = minimax(depth + 1, true);
                        board[i][j].setText("");
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private boolean checkWinningMove(String mark) {
        // Check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            int count = 0;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j].getText().equals(mark)) {
                    count++;
                }
            }
            if (count == BOARD_SIZE) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < BOARD_SIZE; j++) {
            int count = 0;
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i][j].getText().equals(mark)) {
                    count++;
                }
            }
            if (count == BOARD_SIZE) {
                return true;
            }
        }

        // Check diagonals
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][i].getText().equals(mark)) {
                count1++;
            }
            if (board[i][BOARD_SIZE - 1 - i].getText().equals(mark)) {
                count2++;
            }
        }

        return count1 == BOARD_SIZE || count2 == BOARD_SIZE;
    }

    private class CellClickListener implements ActionListener {
        private int row;
        private int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (playerTurn) {
                markCell(row, col, PLAYER_MARK);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGame());
    }
}

