package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Class BoardPanel
 *
 * @authors Brendan Jones, arrangements by: luisfelipesv y melytc
 *
 * Luis Felipe Salazar A00817158 
 * Melissa Janet Treviño A00816715
 *
 * 1/MAR/16
 * @version 2.0
 *
 * The {@code BoardPanel} class is responsible for displaying the game grid and
 * handling things related to the game board.
 */
public class BoardPanel extends JPanel {

    // Serial Version UID.
    private static final long lSerialVersionUID = 5055679736784226108L;

    // Minimum color component values for TTtitles.
    public static final int iColorMin = 35;

    // Maximum color component values for TTtitles. 
    public static final int iColorMax = 255 - iColorMin;

    // The width of the border around the game board.
    private static final int iBorderWidth = 5;

    // The number of columns on the board.
    public static final int iColumns = 10;

    // The number of visible rows on the board.
    private static final int iRowsVisible = 20;

    // The number of rows that are hidden from view.
    private static final int iRowsHidden = 2;

    // The total number of rows that the board contains.
    public static final int iRows = iRowsVisible + iRowsHidden;

    // The number of pixels that a tile takes up.
    public static final int iTitleSize = 24;

    // The width of the shading on the TTtitles.
    public static final int iShadeWidth = 4;

    // The central x coordinate on the game board.
    private static final int iCenterX = iColumns * iTitleSize / 2;

    // The central y coordinate on the game board.
    private static final int iCenterY = iRowsVisible * iTitleSize / 2;

    // The total width of the panel.
    public static final int iPanelWidth = iColumns * iTitleSize + iBorderWidth * 2;

    // The total height of the panel.
    public static final int iPanelHeight = iRowsVisible * iTitleSize + iBorderWidth * 2;

    // The larger font to display.
    private static final Font fLargeFont = new Font("Tahoma", Font.BOLD, 16);

    // The smaller font to display.
    private static final Font fSmallFont = new Font("Tahoma", Font.BOLD, 12);

    // The Tetris instance.
    private Tetris tetris;

    // The TTtitles that make up the board.
    private TileType[][] TTtitles;

    /**
     * Method that creates a new GameBoard instance.
     *
     * @param tetris is the <code>Tetris</code> instance to use.
     */
    public BoardPanel(Tetris tetris) {
        this.tetris = tetris;
        this.TTtitles = new TileType[iRows][iColumns];

        setPreferredSize(new Dimension(iPanelWidth, iPanelHeight));
        setBackground(Color.WHITE);
    }

    /**
     * Method that resets the board and clears away any TTtitles.
     */
    public void clear() {
        /*
        * Loop through every tile index and set it's value
        * to null to clear the board.
        */
        for (int i = 0; i < iRows; i++) {
            for (int j = 0; j < iColumns; j++) {
                TTtitles[i][j] = null;
            }
        }
    }

    /**
     * Method that determines whether or not a piece can be placed at the coordinates.
     *
     * @param ttType is the <code>TileType</code> of the piece to use.
     * @param iX is the <code>integer</code> with the x coordinate of the piece.
     * @param iY is the <code>integer</code> with the y coordinate of the piece.
     * @param iRotation is the <code>integer</code> with the rotation 
     * of the piece.
     * 
     * @return A <code>boolean</code> value to know whether or not 
     * the position is valid.
     */
    public boolean isValidAndEmpty(TileType ttType, int iX, int iY, 
            int iRotation) {

        // Ensure the piece is in a valid column.
        if (iX < -ttType.getLeftInset(iRotation) || iX + ttType.getDimension() 
                - ttType.getRightInset(iRotation) >= iColumns) {
            return false;
        } 

        // Ensure the piece is in a valid row.
        if (iY < -ttType.getTopInset(iRotation) || iY + ttType.getDimension() 
                - ttType.getBottomInset(iRotation) >= iRows) {
            return false;
        }
        
        // Loop through every tile in the piece and see if it conflicts with 
        // an existing tile. Valid location of the tile checked previously.
        for (int col = 0; col < ttType.getDimension(); col++) {
            for (int row = 0; row < ttType.getDimension(); row++) {
                if(ttType.isTile(col, row, iRotation) && 
                        (isOccupied(iX+col,iY+row))) 
                    return false;
            }
        }
        return true;
    }

    /**
     * Method that adds a piece to the game board. 
     * Note: Doesn't check for existing pieces,
     * and will overwrite them if they exist.
     *
     * @param ttType is the <code>TileType</code> of the piece to use.
     * @param iX is the <code>integer</code> with the x coordinate of the piece.
     * @param iY is the <code>integer</code> with the y coordinate of the piece.
     * @param iRotation is the <code>integer</code> with the rotation 
     * of the piece.
     */
    public void addPiece(TileType ttType, int iX, int iY, int iRotation) {
        /*
		 * Loop through every tile within the piece and add it
		 * to the board only if the boolean that represents that
		 * tile is set to true.
         */
        for (int iCol = 0; iCol < ttType.getDimension(); iCol++) {
            for (int iRow = 0; iRow < ttType.getDimension(); iRow++) {
                if (ttType.isTile(iCol, iRow, iRotation)) {
                    setTile(iCol + iX, iRow + iY, ttType);
                }
            }
        }
    }

    /**
     * Method that checks the board to see if any lines have been cleared, 
     * and removes them from the game.
     *
     * @return An <code>integer</code> value of the number of lines 
     * that were cleared.
     */
    public int checkLines() {
        int iCompletedLines = 0;

        /*
        * Here we loop through every line and check it to see if
        * it's been cleared or not. If it has, we increment the
        * number of completed lines and check the next row.
        * 
        * The checkLine function handles clearing the line and
        * shifting the rest of the board down for us.
        */
        for (int iRow = 0; iRow < iRows; iRow++) {
            if (checkLine(iRow)) {
                iCompletedLines++;
            }
        }
        return iCompletedLines;
    }

    /**
     * Method that checks whether or not {@code row} is full.
     *
     * @param iLine is an <code>integer</code> with the row to check.
     * @return A <code>boolean</code> value of whether or not this row is full.
     */
    private boolean checkLine(int iLine) {
        /*
        * Iterate through every column in this row. If any of them are
        * empty, then the row is not full.
        */
        for (int iCol = 0; iCol < iColumns; iCol++) {
            if (!isOccupied(iCol, iLine)) {
                return false;
            }
        }

        /*
        * Since the line is filled, we need to 'remove' it from the game.
        * To do this, we simply shift every row above it down by one.
        */
        for (int iRow = iLine - 1; iRow >= 0; iRow--) {
            for (int iCol = 0; iCol < iColumns; iCol++) {
                setTile(iCol, iRow + 1, getTile(iCol, iRow));
            }
        }
        return true;
    }

    /**
     * Method that checks to see if the tile is already occupied.
     *
     * @param iX is the <code>integer</code> with the x coordinate to check.
     * @param iY is the <code>integer</code> with the y coordinate to check.
     * @return A <code>boolean</code> value of whether or not 
     * the tile is occupied.
     */
    private boolean isOccupied(int iX, int iY) {
        return TTtitles[iY][iX] != null;
    }

    /**
     * Method that sets a tile located at the desired column and row.
     *
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param type The value to set to the tile to.
     */
    private void setTile(int iX, int iY, TileType type) {
        TTtitles[iY][iX] = type;
    }

    /**
     * Method that gets a tile by it's column and row.
     *
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @return the tile.
     */
    private TileType getTile(int x, int y) {
        return TTtitles[y][x];
    }

    @Override
    public void paintComponent(Graphics graGraphic) {
        super.paintComponent(graGraphic);

        // This helps simplify the positioning of things.
        graGraphic.translate(iBorderWidth, iBorderWidth);

        // Draw the board differently depending on the current game state.
        if (tetris.isPaused()) {
            drawPauseInterface(graGraphic);
        } else if (tetris.isNewGame() || tetris.isGameOver()) {
            drawNewGame(graGraphic);
        } else {
            drawBoard(graGraphic);
        }

        // Draw the outline.
        graGraphic.setColor(Color.DARK_GRAY);
        graGraphic.drawRect(0, 0, iTitleSize * iColumns, 
                iTitleSize * iRowsVisible);
    }

    /**
     * Method that paints the interface when the game is paused.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawPauseInterface(Graphics graGraphic) {
        graGraphic.setFont(fLargeFont);
            graGraphic.setColor(Color.DARK_GRAY);
            String msg = "PAUSED";
            graGraphic.drawString(msg, iCenterX - 
                    graGraphic.getFontMetrics().stringWidth(msg) / 2, iCenterY);
    }
    
    /**
     * Method that paints the interface for a new game.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawNewGame(Graphics graGraphic) {
        graGraphic.setFont(fLargeFont);
        graGraphic.setColor(Color.DARK_GRAY);

        /*
        * Because both the game over and new game screens are nearly 
        * identical, we can handle them together and just use a ternary 
        * operator to change the messages that are displayed.
        */
        String msg = tetris.isNewGame() ? "TETRIS" : "GAME OVER";
        graGraphic.drawString(msg, iCenterX - 
                graGraphic.getFontMetrics().stringWidth(msg) / 2, 150);
        graGraphic.setFont(fSmallFont);
        msg = "Press Enter to Play" + (tetris.isNewGame() ? "" : " Again");
        graGraphic.drawString(msg, iCenterX - 
                graGraphic.getFontMetrics().stringWidth(msg) / 2, 300);
    }
    
    /**
     * Method that paints the board when playing.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawBoard(Graphics graGraphic) {
        // Draw the titles onto the board.
        drawTiles(graGraphic);

        /*
        * Draw the current piece. This cannot be drawn like the rest of the
        * pieces because it's still not part of the game board. If it were
        * part of the board, it would need to be removed every frame which
        * would just be slow and confusing.
        */
        drawCurrentTile(graGraphic);

        /*
        * Draw the background grid above the pieces (serves as a useful visual
        * for players, and makes the pieces look nicer by breaking them up.
        */
        graGraphic.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < iColumns; x++) {
            for (int y = 0; y < iRowsVisible; y++) {
                graGraphic.drawLine(0, y * iTitleSize, iColumns * iTitleSize, y * iTitleSize);
                graGraphic.drawLine(x * iTitleSize, 0, x * iTitleSize, iRowsVisible * iTitleSize);
            }
        }
    }
    
    public void drawTiles(Graphics graGraphic) {
        for (int x = 0; x < iColumns; x++) {
            for (int y = iRowsHidden; y < iRows; y++) {
                TileType tile = getTile(x, y);
                if (tile != null) {
                    drawTile(tile, x * iTitleSize, (y - iRowsHidden)
                            * iTitleSize, graGraphic);
                }
            }
        }
    }
    
    public void drawCurrentTile(Graphics graGraphic) {
        TileType type = tetris.getPieceType();
        int pieceCol = tetris.getPieceCol();
        int pieceRow = tetris.getPieceRow();
        int rotation = tetris.getPieceRotation();
        
        // Draw the piece onto the board.
        for (int col = 0; col < type.getDimension(); col++) {
            for (int row = 0; row < type.getDimension(); row++) {
                if (pieceRow + row >= 2 && type.isTile(col, row,
                        rotation)) {
                    drawTile(type, (pieceCol + col) * iTitleSize,
                            (pieceRow + row - iRowsHidden) * iTitleSize,
                            graGraphic);
                }
            }
        }
        /*
        * Draw the ghost (semi-transparent piece that shows where the 
        * current piece will land). 
        * We simply take the current position and move
        * down until we hit a row that would cause a collision.
        */
        drawGhost(graGraphic, type, pieceCol, pieceRow, rotation);
    }
    
    public void drawGhost(Graphics graGraphic, TileType type, int pieceCol,
            int pieceRow, int rotation) {
        Color base = type.getBaseColor();
        base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 40);
        for (int lowest = pieceRow; lowest < iRows; lowest++) {
            // If no collision is detected, try the next row.
            if (isValidAndEmpty(type, pieceCol, lowest, rotation)) {
                continue;
            }

            // Draw the ghost one row higher than the one the collision took place at.
            lowest--;

            // Draw the ghost piece.
            for (int col = 0; col < type.getDimension(); col++) {
                for (int row = 0; row < type.getDimension(); row++) {
                    if (lowest + row >= 2 && type.isTile(col, row, rotation)) {
                        drawTile(base, base.brighter(), base.darker(),
                                (pieceCol + col) * iTitleSize,
                                (lowest + row - iRowsHidden) * iTitleSize,
                                graGraphic);
                    }
                }
            }
            break;
        }
    }
    
    /**
     * Draws a tile onto the board.
     *
     * @param type The type of tile to draw.
     * @param x The column.
     * @param y The row.
     * @param g The graphics object.
     */
    private void drawTile(TileType type, int x, int y, Graphics g) {
        drawTile(type.getBaseColor(), type.getLightColor(), type.getDarkColor(), x, y, g);
    }

    /**
     * Draws a tile onto the board.
     *
     * @param base The base color of tile.
     * @param light The light color of the tile.
     * @param dark The dark color of the tile.
     * @param x The column.
     * @param y The row.
     * @param g The graphics object.
     */
    private void drawTile(Color base, Color light, Color dark, int x, int y, Graphics g) {

        /*
		 * Fill the entire tile with the base color.
<<<<<<< Updated upstream
		 */
		g.setColor(light);
		g.fillRect(x, y, iTitleSize, iTitleSize);
                g.fillRect(x, y, iTitleSize, iTitleSize);
		
		/*
		 * Fill the bottom and right edges of the tile with the dark shading color.
		 */
		g.setColor(base);
		//g.fillRect(x, y + TILE_SIZE - SHADE_WIDTH, TILE_SIZE, SHADE_WIDTH);
		//g.fillRect(x + TILE_SIZE - SHADE_WIDTH, y, SHADE_WIDTH, TILE_SIZE);
                int xPoints[] = {x, x, x + (iTitleSize / 2)};
                int yPoints[] = {y + iTitleSize, y, y + (iTitleSize / 2)};
                int xPoints2[] = {x + (iTitleSize / 2), x + iTitleSize, x + iTitleSize};
                int yPoints2[] = {y + (iTitleSize / 2), y + iTitleSize, y};
                g.fillPolygon(xPoints, yPoints, 3);
                g.fillPolygon(xPoints2, yPoints2, 3);
                
                
                g.setColor(dark);
                int xPoints3[] = {x, x + iTitleSize, x + (iTitleSize / 2)};
                int yPoints3[] = {y + iTitleSize, y + iTitleSize, y + (iTitleSize / 2) };
                g.fillPolygon(xPoints3, yPoints3, 3);
		
		/*
		 * Fill the top and left edges with the light shading. We draw a single line
		 * for each row or column rather than a rectangle so that we can draw a nice
		 * looking diagonal where the light and dark shading meet.
		 */
                /*
		g.setColor(light);
		for(int i = 0; i < SHADE_WIDTH; i++) {
			g.drawLine(x, y + i, x + TILE_SIZE - i - 1, y + i);
			g.drawLine(x + i, y, x + i, y + TILE_SIZE - i - 1);
		}
                */
	}
}
