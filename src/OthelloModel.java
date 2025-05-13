import java.util.ArrayList;
import java.util.List;

public class OthelloModel {
    public static final int BOARD_SIZE = 8;
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int[][] board;
    private int currentPlayer;
    private boolean gameOver;

    public OthelloModel() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
        currentPlayer = BLACK; // Black always starts
        gameOver = false;
    }

    private void initializeBoard() {
        // Clear the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
        
        // Set up initial pieces
        int mid = BOARD_SIZE / 2;
        board[mid-1][mid-1] = WHITE;
        board[mid-1][mid] = BLACK;
        board[mid][mid-1] = BLACK;
        board[mid][mid] = WHITE;
    }

    public boolean isValidMove(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != EMPTY) {
            return false;
        }

        return getFlippablePieces(row, col).size() > 0;
    }

    private List<int[]> getFlippablePieces(int row, int col) {
        List<int[]> flippablePieces = new ArrayList<>();
        int opponent = (currentPlayer == BLACK) ? WHITE : BLACK;

        // Check in all 8 directions
        int[][] directions = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
        
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            List<int[]> temp = new ArrayList<>();
            
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == opponent) {
                temp.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
            
            if (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == currentPlayer && !temp.isEmpty()) {
                flippablePieces.addAll(temp);
            }
        }
        
        return flippablePieces;
    }

    public boolean makeMove(int row, int col) {
        if (!isValidMove(row, col)) {
            return false;
        }

        List<int[]> flippablePieces = getFlippablePieces(row, col);
        board[row][col] = currentPlayer;
        
        // Flip pieces
        for (int[] piece : flippablePieces) {
            board[piece[0]][piece[1]] = currentPlayer;
        }

        switchPlayer();
        
        // If next player has no valid moves, switch back
        if (!hasValidMoves()) {
            switchPlayer();
            // If other player also has no valid moves, game is over
            if (!hasValidMoves()) {
                gameOver = true;
            }
        }
        
        return true;
    }

    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (isValidMove(i, j)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    public boolean hasValidMoves() {
        return !getValidMoves().isEmpty();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    public int[][] getBoard() {
        int[][] boardCopy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, BOARD_SIZE);
        }
        return boardCopy;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int[] getScore() {
        int blackCount = 0;
        int whiteCount = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == BLACK) blackCount++;
                else if (board[i][j] == WHITE) whiteCount++;
            }
        }
        return new int[]{blackCount, whiteCount};
    }

    // Computer move using Greedy Algorithm
    public void makeComputerMove() {
        if (!hasValidMoves()) return;

        List<int[]> validMoves = getValidMoves();
        int[] bestMove = null;
        int maxPieces = -1;

        // Find move that flips the most pieces
        for (int[] move : validMoves) {
            List<int[]> flippable = getFlippablePieces(move[0], move[1]);
            if (flippable.size() > maxPieces) {
                maxPieces = flippable.size();
                bestMove = move;
            }
        }

        if (bestMove != null) {
            makeMove(bestMove[0], bestMove[1]);
        }
    }
} 