import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OthelloModelTest {
    private OthelloModel model;

    @BeforeEach
    void setUp() {
        model = new OthelloModel();
    }

    @Test
    void testInitialBoard() {
        int[][] board = model.getBoard();
        int mid = OthelloModel.BOARD_SIZE / 2;

        // Check initial pieces
        assertEquals(OthelloModel.WHITE, board[mid-1][mid-1]);
        assertEquals(OthelloModel.BLACK, board[mid-1][mid]);
        assertEquals(OthelloModel.BLACK, board[mid][mid-1]);
        assertEquals(OthelloModel.WHITE, board[mid][mid]);

        // Check that first player is black
        assertEquals(OthelloModel.BLACK, model.getCurrentPlayer());

        // Check initial score
        int[] score = model.getScore();
        assertEquals(2, score[0]); // Black count
        assertEquals(2, score[1]); // White count
    }

    @Test
    void testValidMoves() {
        // Initial valid moves for black
        var validMoves = model.getValidMoves();
        assertEquals(4, validMoves.size());

        // Test specific valid moves
        int mid = OthelloModel.BOARD_SIZE / 2;
        assertTrue(model.isValidMove(mid-2, mid-1));
        assertTrue(model.isValidMove(mid-1, mid-2));
        assertTrue(model.isValidMove(mid+1, mid));
        assertTrue(model.isValidMove(mid, mid+1));

        // Test invalid moves
        assertFalse(model.isValidMove(0, 0));
        assertFalse(model.isValidMove(mid-1, mid-1)); // Occupied space
        assertFalse(model.isValidMove(-1, -1)); // Out of bounds
        assertFalse(model.isValidMove(OthelloModel.BOARD_SIZE, OthelloModel.BOARD_SIZE)); // Out of bounds
    }

    @Test
    void testMakeMove() {
        int mid = OthelloModel.BOARD_SIZE / 2;

        // Make a valid move
        assertTrue(model.makeMove(mid-2, mid-1));

        // Verify the piece was placed
        int[][] board = model.getBoard();
        assertEquals(OthelloModel.BLACK, board[mid-2][mid-1]);

        // Verify pieces were flipped
        assertEquals(OthelloModel.BLACK, board[mid-1][mid]);

        // Verify current player switched
        assertEquals(OthelloModel.WHITE, model.getCurrentPlayer());

        // Try to make an invalid move
        assertFalse(model.makeMove(0, 0));
    }

    @Test
    void testGameOver() {
        assertFalse(model.isGameOver());

        // Play until no more moves available
        while (model.hasValidMoves()) {
            model.makeComputerMove();
        }

        // Verify game is over
        assertTrue(model.isGameOver());
    }

    @Test
    void testComputerMove() {
        // Verify initial state
        int[] initialScore = model.getScore();

        // Make computer move
        model.makeComputerMove();

        // Verify that the state changed
        int[] newScore = model.getScore();
        assertNotEquals(initialScore[0] + initialScore[1],
                       newScore[0] + newScore[1]);

        // Verify it's now whites's turn (computer was white)
        assertEquals(OthelloModel.WHITE, model.getCurrentPlayer());
    }
}
