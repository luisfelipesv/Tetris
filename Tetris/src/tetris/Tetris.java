package tetris;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.io.RandomAccessFile;
import java.io.IOException;
import javax.swing.JFrame;

/**
 *
 * @authors Brendan Jones, arrangements by: luisfelipesv y melytc
 *
 * Luis Felipe Salazar A00817158 Melissa Janet Treviño A00816715
 *
 * Tetris is a Russian tile-matching puzzle video game. The goal is to form a
 * horizontal line with the tiles, so that line can disappear and clear out the
 * space of the game.
 *
 * 1/MAR/16
 * @version 1.0
 *
 */
public class Tetris extends JFrame {

    // The Serial Version UID.
    private static final long serialVersionUID = -4722429764792514382L;

    // The number of milliseconds per frame.
    private static final long FRAME_TIME = 1000L / 50L;

    // The number of pieces that exist.
    private static final int TYPE_COUNT = TileType.values().length;

    // The BoardPanel instance.
    private BoardPanel board;

    // The SidePanel instance.
    private SidePanel side;

    // Boolean to know whether or not the game is paused.
    private boolean bPaused;

    // Boolean to know whether or not we've played a game yet.
    private boolean isNewGame;

    // Boolean to know whether or not the game is over.
    private boolean isGameOver;

    // Boolean to know wheter or not the music is playing.
    private boolean bMusicOn;

    // Integer with the current level we're on.
    private int level;

    // Integer with the current score.
    private int score;

    // The random number generator. 
    // This is used to spit out pieces randomly.
    private Random random;

    // The clock that handles the update logic.
    private Clock logicTimer;

    // The current type of tile.
    private TileType currentType;

    // The next type of tile.
    private TileType nextType;

    // Integer with the current column of our tile.
    private int currentCol;

    // Integer with the current row of our tile.
    private int currentRow;

    // Integer with the current rotation of our tile.
    private int currentRotation;

    // Integer that ensures that a certain amount of time passes after 
    // a piece is spawned before we can drop it.
    private int dropCooldown;

    // Integer with the speed of the game.
    private float gameSpeed;

    // SoundClip with the Game theme selected.
    private SoundClip audioGame;
    
    // SoundClip with sound of a horizontal line popped.
    private SoundClip soundPopped;
    
    // SoundClip with sound of a tile at the bottom.
    private SoundClip soundBottom;

    // Creates a new Tetris instance. Sets up the window's properties,
    // and adds a controller listener.
    private Tetris() {
        /*
        * Set the basic properties of the window.
        */
        super("Tetris");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        /*
        * Initialize the BoardPanel and SidePanel instances.
        */
        this.board = new BoardPanel(this);
        this.side = new SidePanel(this);

        /*
        * Add the BoardPanel and SidePanel instances to the window.
        */
        add(board, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);

        /*
        * Adds a custom anonymous KeyListener to the frame.
        */
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    /*
                    * Drop - When pressed, we check to see that the game is not
                    * paused and that there is no drop cooldown, then set the
                    * logic timer to run at a speed of 25 cycles per second.
                    */
                    case KeyEvent.VK_DOWN:
                        if (!bPaused && dropCooldown == 0) {
                            logicTimer.setCyclesPerSecond(25.0f);
                        }
                        break;

                    /*
                    * Drop - When pressed, we check to see that the game is not
                    * paused and that there is no drop cooldown, then set the
                    * logic timer to run at a speed of 25 cycles per second.
                    */
                    case KeyEvent.VK_SPACE:
                        if (!bPaused && dropCooldown == 0) {
                            logicTimer.setCyclesPerSecond(25.0f);
                        }
                        break;

                    /*
                    * Move Left - When pressed, we check to see that the game is
                    * not paused and that the position to the left of the current
                    * position is valid. If so, we decrement the current column by 1.
                    */
                    case KeyEvent.VK_LEFT:
                        if (!bPaused && board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
                            currentCol--;
                        }
                        break;

                    /*
                    * Move Right - When pressed, we check to see that the game is
                    * not paused and that the position to the right of the current
                    * position is valid. If so, we increment the current column by 1.
                    */
                    case KeyEvent.VK_RIGHT:
                        if (!bPaused && board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
                            currentCol++;
                        }
                        break;

                    /*
                    * Rotate Anticlockwise - When pressed, check to see that the game is not paused
                    * and then attempt to rotate the piece anticlockwise. Because of the size and
                    * complexity of the rotation code, as well as it's similarity to clockwise
                    * rotation, the code for rotating the piece is handled in another method.
                    */
                    case KeyEvent.VK_Z:
                        if (!bPaused) {
                            rotatePiece((currentRotation == 0) ? 3 : currentRotation - 1);
                        }
                        break;

                    /*
                    * Rotate Clockwise - When pressed, check to see that the game is not paused
                    * and then attempt to rotate the piece clockwise. Because of the size and
                    * complexity of the rotation code, as well as it's similarity to anticlockwise
                    * rotation, the code for rotating the piece is handled in another method.
                    */
                    case KeyEvent.VK_X:
                    case KeyEvent.VK_UP:
                        if (!bPaused) {
                            rotatePiece((currentRotation == 3) ? 0 : currentRotation + 1);
                        }
                        break;

                    /*
                    * Pause Game - When pressed, check to see that we're currently playing a game.
                    * If so, toggle the pause variable and update the logic timer to reflect this
                    * change, otherwise the game will execute a huge number of updates and essentially
                    * cause an instant game over when we unpause if we stay paused for more than a
                    * minute or so.
                    */
                    case KeyEvent.VK_P:
                        if (!isGameOver && !isNewGame) {
                            bPaused = !bPaused;
                            logicTimer.setPaused(bPaused);
                        }
                        break;

                    /*
                    * Start Game - When pressed, check to see that we're in either a game over or new
                    * game state. If so, reset the game.
                    */
                    case KeyEvent.VK_ENTER:
                        if (isGameOver || isNewGame) {
                            resetGame();
                        }
                        break;

                    case KeyEvent.VK_1:
                        audioGame.stop();
                        audioGame = new SoundClip("Classic.wav");
                        audioGame.play();
                        audioGame.setLooping(true);
                        bMusicOn = true;
                        break;

                    case KeyEvent.VK_2:
                        audioGame.stop();
                        audioGame = new SoundClip("Horror.wav");
                        audioGame.play();
                        audioGame.setLooping(true);
                        bMusicOn = true;
                        break;

                    case KeyEvent.VK_3:
                        audioGame.stop();
                        audioGame = new SoundClip("Move.wav");
                        audioGame.play();
                        audioGame.setLooping(true);
                        bMusicOn = true;
                        break;

                    case KeyEvent.VK_4:
                        audioGame.stop();
                        audioGame = new SoundClip("Pop.wav");
                        audioGame.play();
                        audioGame.setLooping(true);
                        bMusicOn = true;
                        break;

                    case KeyEvent.VK_5:
                        audioGame.stop();
                        audioGame = new SoundClip("Relaxed.wav");
                        audioGame.play();
                        audioGame.setLooping(true);
                        bMusicOn = true;
                        break;

                    case KeyEvent.VK_0:
                        if (bMusicOn) {
                            audioGame.pause();
                            bMusicOn = false;
                        } else if (!bMusicOn) {
                            audioGame.unpause();
                            bMusicOn = true;
                        }
                        break;
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

                switch (e.getKeyCode()) {

                    /*
                    * Drop - When released, we set the speed of the logic timer
                    * back to whatever the current game speed is and clear out
                    * any cycles that might still be elapsed.
                     */
                    case KeyEvent.VK_DOWN:
                        logicTimer.setCyclesPerSecond(gameSpeed);
                        logicTimer.reset();
                        break;

                    case KeyEvent.VK_S:
                        try {
                            saveGame();
                        } catch (IOException ex) {
                        }
                        break;

                    case KeyEvent.VK_L:
                        try {
                            loadGame();
                        } catch (IOException ex) {
                        }
                        break;
                }

            }

        });

        /*
        * Here we resize the frame to hold the BoardPanel and SidePanel instances,
        * center the window on the screen, and show it to the user.
        */
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Starts the game running. Initializes everything and enters the game loop.
     */
    private void startGame() {
        // Initialize our random number generator, logic timer, 
        // music, and new game variables.
        this.random = new Random();
        this.isNewGame = true;
        this.gameSpeed = 1.0f;
        this.bMusicOn = true;
        audioGame = new SoundClip("Classic.wav");
        audioGame.play();
        soundPopped = new SoundClip("popped.wav");
        soundPopped.setLooping(false);
        soundBottom = new SoundClip("bottom.wav");
        soundBottom.setLooping(false);

        /*
        * Setup the timer to keep the game from running before the user presses enter
        * to start it.
        */
        this.logicTimer = new Clock(gameSpeed);
        logicTimer.setPaused(true);

        while (true) {
            // Get the time that the frame started.
            long start = System.nanoTime();

            // Update the logic timer.
            logicTimer.update();

            /*
            * If a cycle has elapsed on the timer, we can update the game and
            * move our current piece down.
            */
            if (logicTimer.hasElapsedCycle()) {
                updateGame();
            }

            // Decrement the drop cool down if necessary.
            if (dropCooldown > 0) {
                dropCooldown--;
            }

            // Display the window to the user.
            renderGame();

            /*
            * Sleep to cap the framerate.
            */
            long delta = (System.nanoTime() - start) / 1000000L;
            if (delta < FRAME_TIME) {
                try {
                    Thread.sleep(FRAME_TIME - delta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * updateGame
     * 
     * Method that updates the game and handles the bulk of it's logic.
     */
    private void updateGame() {
        /*
        * Check to see if the piece's position can move down to the next row.
        */
        if (board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
            // Increment the current row if it's safe to do so.
            currentRow++;
        } else {
            /*
            * We've either reached the bottom of the board, or landed on another piece, so
            * we need to add the piece to the board.
            */
            board.addPiece(currentType, currentCol, currentRow, currentRotation);

            /*
            * Check to see if adding the new piece resulted in any cleared lines. If so,
            * increase the player's score. (Up to 4 lines can be cleared in a single go;
            * [1 = 100pts, 2 = 200pts, 3 = 400pts, 4 = 800pts]).
            */
            int cleared = board.checkLines();
            if (cleared > 0) {
                score += 50 << cleared;
                soundPopped.play();
            } else {
                soundBottom.play();
            }

            /*
            * Increase the speed slightly for the next piece and update the game's timer
            * to reflect the increase.
            */
            gameSpeed += 0.035f;
            logicTimer.setCyclesPerSecond(gameSpeed);
            logicTimer.reset();

            /*
            * Set the drop cooldown so the next piece doesn't automatically come flying
            * in from the heavens immediately after this piece hits if we've not reacted
            * yet. (~0.5 second buffer).
            */
            dropCooldown = 25;

            /*
            * Update the difficulty level. This has no effect on the game, and is only
            * used in the "Level" string in the SidePanel.
            */
            level = (int) (gameSpeed * 1.70f);

            /*
            * Spawn a new piece to control.
            */
            spawnPiece();
        }
    }

    /**
     * renderGame
     * 
     * Method that forces the BoardPanel and SidePanel to repaint.
     */
    private void renderGame() {
        board.repaint();
        side.repaint();
    }

    /**
     * resetGame
     * 
     * Method that resets the game variables to their default values at the start of a new
     * game.
     */
    private void resetGame() {
        this.level = 1;
        this.score = 0;
        this.gameSpeed = 1.0f;
        this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
        this.isNewGame = false;
        this.isGameOver = false;
        this.bMusicOn = true;
        board.clear();
        logicTimer.reset();
        logicTimer.setCyclesPerSecond(gameSpeed);
        spawnPiece();
    }

    /**
     * spawnPiece
     * 
     * Method that spawns a new piece and resets our piece's variables to their default
     * values.
     */
    private void spawnPiece() {
        /*
        * Poll the last piece and reset our position and rotation to
        * their default variables, then pick the next piece to use.
         */
        this.currentType = nextType;
        this.currentCol = currentType.getSpawnColumn();
        this.currentRow = currentType.getSpawnRow();
        this.currentRotation = 0;
        this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];

        /*
        * If the spawn point is invalid, we need to pause the game and flag that we've lost
        * because it means that the pieces on the board have gotten too high.
         */
        if (!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
            this.isGameOver = true;
            logicTimer.setPaused(true);
        }
    }

    /**
     * rotatePiece
     * 
     * Method that attempts to set the rotation of the current piece to newRotation.
     *
     * @param newRotation The rotation of the new piece.
     */
    private void rotatePiece(int newRotation) {
        /*
        * Sometimes pieces will need to be moved when rotated to avoid clipping
        * out of the board (the I piece is a good example of this). Here we store
        * a temporary row and column in case we need to move the tile as well.
         */
        int newColumn = currentCol;
        int newRow = currentRow;

        /*
        * Get the insets for each of the sides. These are used to determine how
        * many empty rows or columns there are on a given side.
         */
        int left = currentType.getLeftInset(newRotation);
        int right = currentType.getRightInset(newRotation);
        int top = currentType.getTopInset(newRotation);
        int bottom = currentType.getBottomInset(newRotation);

        /*
        * If the current piece is too far to the left or right, move the piece away from the edges
        * so that the piece doesn't clip out of the map and automatically become invalid.
         */
        if (currentCol < -left) {
            newColumn -= currentCol - left;
        } else if (currentCol + currentType.getDimension() - right >= BoardPanel.iColumns) {
            newColumn -= (currentCol + currentType.getDimension() - right) - BoardPanel.iColumns + 1;
        }

        /*
        * If the current piece is too far to the top or bottom, move the piece away from the edges
        * so that the piece doesn't clip out of the map and automatically become invalid.
         */
        if (currentRow < -top) {
            newRow -= currentRow - top;
        } else if (currentRow + currentType.getDimension() - bottom >= BoardPanel.iRows) {
            newRow -= (currentRow + currentType.getDimension() - bottom) - BoardPanel.iRows + 1;
        }

        /*
        * Check to see if the new position is acceptable. If it is, update the rotation and
        * position of the piece.
         */
        if (board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
            currentRotation = newRotation;
            currentRow = newRow;
            currentCol = newColumn;
        }
    }

    /**
     * isPaused
     * 
     * Method that checks to see whether or not the game is paused.
     *
     * @return Whether or not the game is paused.
     */
    public boolean isPaused() {
        return bPaused;
    }

    /**
     * isGameOver
     * 
     * Method that checks to see whether or not the game is over.
     *
     * @return Whether or not the game is over.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * isNewGame
     * 
     * Method that checks to see whether or not we're on a new game.
     *
     * @return Whether or not this is a new game.
     */
    public boolean isNewGame() {
        return isNewGame;
    }

    /**
     * getScore
     * 
     * Method that returns the current score.
     *
     * @return is the <code>integer</code> score.
     */
    public int getScore() {
        return score;
    }

    /**
     * getLevel
     * 
     * Method that returns the current level.
     *
     * @return is the <code>integer</code> level.
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * getMusic
     * 
     * Method that returns the name of the theme.
     * @return sTheme is the <code>string</code> with the name of the theme.
     */
    public String getMusic() {
        String sTheme = this.audioGame.getFilename();
        int iLength = sTheme.length();
        sTheme = (String) sTheme.subSequence(0, iLength-4);
        return sTheme;
    }

    /**
     * getPieceType
     * 
     * Method that returns the current type of piece we're using.
     *
     * @return The piece type.
     */
    public TileType getPieceType() {
        return currentType;
    }

    /**
     * getNextPieceType
     * 
     * Method that returns the next type of piece we're using.
     *
     * @return The next piece.
     */
    public TileType getNextPieceType() {
        return nextType;
    }

    /**
     * getPieceCol
     * 
     * Method that returns the column of the current piece.
     *
     * @return The column.
     */
    public int getPieceCol() {
        return currentCol;
    }

    /**
     * getPieceRow
     * 
     * Method that returns the row of the current piece.
     *
     * @return The row.
     */
    public int getPieceRow() {
        return currentRow;
    }

    /**
     * getPieceRotation
     * 
     * Method that gets the rotation of the current piece.
     *
     * @return The rotation.
     */
    public int getPieceRotation() {
        return currentRotation;
    }

    /**
     * saveGame
     * 
     * Method that saves the game in a file.
     * @throws IOException 
     */
    public void saveGame() throws IOException {
        RandomAccessFile rafFile = new RandomAccessFile("Game.dat", "rw");
        rafFile.writeInt(this.level);
        rafFile.writeInt(this.score);
        rafFile.writeInt(this.currentCol);
        rafFile.writeInt(this.currentRow);
        rafFile.writeInt(this.currentRotation);
        rafFile.writeInt(currentType.getType());
        rafFile.writeInt(nextType.getType());
        rafFile.writeFloat(this.gameSpeed);
        rafFile.writeBoolean(this.isGameOver);
        rafFile.writeBoolean(this.isNewGame);

        int matStatus[][] = board.getState();

        rafFile.writeInt(matStatus.length);
        rafFile.writeInt(matStatus[0].length);
        for (int iR = 0; iR < matStatus.length; iR++) {
            for (int iC = 0; iC < matStatus[0].length; iC++) {
                rafFile.writeInt(matStatus[iR][iC]);
            }
        }
    }

    /**
     * loadGame
     * 
     * Method that loads a previous game.
     * @throws IOException 
     */
    public void loadGame() throws IOException {
        RandomAccessFile rafFile = new RandomAccessFile("Game.dat", "rw");
        this.level = rafFile.readInt();
        this.score = rafFile.readInt();
        this.currentCol = rafFile.readInt();
        this.currentRow = rafFile.readInt();
        this.currentRotation = rafFile.readInt();
        this.currentType = TileType.values()[rafFile.readInt()];
        this.nextType = TileType.values()[rafFile.readInt()];
        this.gameSpeed = rafFile.readFloat();
        this.isGameOver = rafFile.readBoolean();
        this.isNewGame = rafFile.readBoolean();

        logicTimer.reset();
        logicTimer.setCyclesPerSecond(gameSpeed);

        int iI = rafFile.readInt();
        int iJ = rafFile.readInt();
        int matBoard[][] = new int[iI][iJ];

        for (int iR = 0; iR < iI; iR++) {
            for (int iC = 0; iC < iJ; iC++) {
                matBoard[iR][iC] = rafFile.readInt();
            }
        }

        board.clear();
        board.setState(matBoard);
    }

    /**
     * Entry-point of the game. 
     * Responsible for creating and starting a new game instance.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        Tetris tetris = new Tetris();
        tetris.startGame();
    }

}
