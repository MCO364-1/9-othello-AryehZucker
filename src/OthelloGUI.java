import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OthelloGUI extends JFrame {
    private static final int CELL_SIZE = 60;
    private static final Color BOARD_COLOR = new Color(0, 100, 0);
    private static final Color GRID_COLOR = Color.BLACK;
    private static final Color VALID_MOVE_COLOR = new Color(0, 150, 0);
    private static final int COMPUTER_MOVE_DELAY = 750;
    
    private OthelloModel model;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JButton restartButton;
    
    public OthelloGUI() {
        model = new OthelloModel();
        setupGUI();
        setVisible(true);
    }
    
    private void setupGUI() {
        setTitle("Othello");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create board panel
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(CELL_SIZE * OthelloModel.BOARD_SIZE, 
                                                 CELL_SIZE * OthelloModel.BOARD_SIZE));
        boardPanel.setBackground(BOARD_COLOR);
        
        // Add mouse listener for moves
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
        
        // Create status panel with restart button
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusLabel = new JLabel("Black's turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel = new JLabel("Black: 2  White: 2");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> restartGame());
        
        statusPanel.add(statusLabel);
        statusPanel.add(scoreLabel);
        statusPanel.add(restartButton);
        
        // Add components to main panel
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Set up the frame
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid lines
        g2d.setColor(GRID_COLOR);
        for (int i = 0; i <= OthelloModel.BOARD_SIZE; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, 
                        i * CELL_SIZE, OthelloModel.BOARD_SIZE * CELL_SIZE);
            g2d.drawLine(0, i * CELL_SIZE, 
                        OthelloModel.BOARD_SIZE * CELL_SIZE, i * CELL_SIZE);
        }
        
        // Draw pieces and valid moves
        int[][] board = model.getBoard();
        for (int row = 0; row < OthelloModel.BOARD_SIZE; row++) {
            for (int col = 0; col < OthelloModel.BOARD_SIZE; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                
                // Draw valid moves
                if (model.getCurrentPlayer() == OthelloModel.BLACK && 
                    model.isValidMove(row, col)) {
                    g2d.setColor(VALID_MOVE_COLOR);
                    g2d.fillOval(x + CELL_SIZE/4, y + CELL_SIZE/4, 
                                CELL_SIZE/2, CELL_SIZE/2);
                }
                
                // Draw pieces
                if (board[row][col] != OthelloModel.EMPTY) {
                    g2d.setColor(board[row][col] == OthelloModel.BLACK ? 
                                Color.BLACK : Color.WHITE);
                    g2d.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    if (board[row][col] == OthelloModel.WHITE) {
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    }
                }
            }
        }
    }
    
    private void restartGame() {
        model = new OthelloModel();
        updateGame();
    }
    
    private void handleMouseClick(int x, int y) {
        if (model.isGameOver() || model.getCurrentPlayer() != OthelloModel.BLACK) {
            return;
        }
        
        int row = y / CELL_SIZE;
        int col = x / CELL_SIZE;
        
        if (model.makeMove(row, col)) {
            updateGame();
            
            // Computer's turn
            if (!model.isGameOver()) {
                restartButton.setEnabled(false);
                Timer timer = new Timer(COMPUTER_MOVE_DELAY, e -> {
                    model.makeComputerMove();
                    updateGame();
                    restartButton.setEnabled(true);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    private void updateGame() {
        // Update score
        int[] score = model.getScore();
        scoreLabel.setText(String.format("Black: %d  White: %d", score[0], score[1]));
        
        // Update status
        if (model.isGameOver()) {
            String winner = score[0] > score[1] ? "Black" : 
                          score[0] < score[1] ? "White" : "Draw";
            statusLabel.setText("Game Over! " + 
                              (winner.equals("Draw") ? "It's a draw!" : winner + " wins!"));
        } else {
            statusLabel.setText(model.getCurrentPlayer() == OthelloModel.BLACK ? 
                              "Black's turn" : "White's turn (Computer thinking...)");
        }
        
        // Repaint board
        boardPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(OthelloGUI::new);
    }
}
