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
 * Luis Felipe Salazar A00817158 Melissa Janet Treviño A00816715
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

    // Minimum color component values for TTtiles.
    public static final int iColorMin = 35;

    // Maximum color component values for TTtiles. 
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
    public static final int iTileSize = 24;

    // The width of the shading on the TTtiles.
    public static final int iShadeWidth = 4;

    // The central x coordinate on the game board.
    private static final int iCenterX = iColumns * iTileSize / 2;

    // The central y coordinate on the game board.
    private static final int iCenterY = iRowsVisible * iTileSize / 2;

    // The total width of the panel.
    public static final int iPanelWidth = iColumns * iTileSize 
            + iBorderWidth * 2;

    // The total height of the panel.
    public static final int iPanelHeight = iRowsVisible * iTileSize 
            + iBorderWidth * 2;

    // The larger font to display.
    private static final Font fLargeFont = new Font("Tahoma", Font.BOLD, 16);

    // The smaller font to display.
    private static final Font fSmallFont = new Font("Tahoma", Font.BOLD, 12);

    // The Tetris instance.
    private Tetris tetris;

    // The TTtiles that make up the board.
    private TileType[][] TTtiles;

    // Int to manage animation.
    private int iAnim;
    private int iCont;

    /**
     * BoardPanel
     * 
     * Method that creates a new GameBoard instance.
     *
     * @param tetris is the <code>Tetris</code> instance to use.
     */
    public BoardPanel(Tetris tetris) {
        this.tetris = tetris;
        this.TTtiles = new TileType[iRows][iColumns];
        this.iAnim = 0;
        this.iCont = 0;

        setPreferredSize(new Dimension(iPanelWidth, iPanelHeight));
        setBackground(Color.WHITE);
    }

    /**
     * clear
     * 
     * Method that resets the board and clears away any tiles.
     */
    public void clear() {
        /*
        * Loop through every tile index and set it's value
        * to null to clear the board.
         */
        for (int i = 0; i < iRows; i++) {
            for (int j = 0; j < iColumns; j++) {
                TTtiles[i][j] = null;
            }
        }
    }

    /**
     * isValidAndEmpty
     * 
     * Method that determines whether or not a piece can be placed at the
     * coordinates.
     *
     * @param ttType is the <code>TileType</code> of the piece to use.
     * @param iX is the <code>integer</code> with the x coordinate of the piece.
     * @param iY is the <code>integer</code> with the y coordinate of the piece.
     * @param iRotation is the <code>integer</code> with the rotation of the
     * piece.
     *
     * @return A <code>boolean</code> value to know whether or not the position
     * is valid.
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
                if (ttType.isTile(col, row, iRotation)
                        && (isOccupied(iX + col, iY + row))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * addPiece
     * 
     * Method that adds a piece to the game board. 
     * Note: Doesn't check for existing pieces, 
     * and will overwrite them if they exist.
     *
     * @param ttType is the <code>TileType</code> of the piece to use.
     * @param iX is the <code>integer</code> with the x coordinate of the piece.
     * @param iY is the <code>integer</code> with the y coordinate of the piece.
     * @param iRotation is the <code>integer</code> with the rotation of the
     * piece.
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
     * checkLines
     * 
     * Method that checks the board to see if any lines have been cleared, and
     * removes them from the game.
     *
     * @return An <code>integer</code> value of the number of lines that were
     * cleared.
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
     * checkLine
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
     * isOccupied
     * 
     * Method that checks to see if the tile is already occupied.
     *
     * @param iX is the <code>integer</code> with the x coordinate to check.
     * @param iY is the <code>integer</code> with the y coordinate to check.
     * @return A <code>boolean</code> value of whether or not the tile is
     * occupied.
     */
    private boolean isOccupied(int iX, int iY) {
        return TTtiles[iY][iX] != null;
    }

    /**
     * setTile
     * 
     * Method that sets a tile located at the desired column and row.
     *
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param ttType is the <code>TileType</code> of the piece to set.
     */
    private void setTile(int iX, int iY, TileType TTtype) {
        TTtiles[iY][iX] = TTtype;
    }

    /**
     * getTile
     * 
     * Method that gets a tile by it's column and row.
     *
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @return the tile.
     */
    private TileType getTile(int x, int y) {
        return TTtiles[y][x];
    }

    /**
     * paintComponent
     * 
     * Method that paints the board depending on the current game state.
     * 
     * @param graGraphic is a <code>Graphic object</code>.
     */
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
        graGraphic.drawRect(0, 0, iTileSize * iColumns,
                iTileSize * iRowsVisible);
    }

    /**
     * drawPauseInterface
     * 
     * Method that paints the interface when the game is paused.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawPauseInterface(Graphics graGraphic) {
        graGraphic.setFont(fLargeFont);
        graGraphic.setColor(Color.DARK_GRAY);
        String msg = "PAUSED";
        graGraphic.drawString(msg, iCenterX- graGraphic.getFontMetrics()
                .stringWidth(msg) / 2, iCenterY / 2);
        
        graGraphic.setFont(fSmallFont);
        String msg0 = "Press a number to switch the song";
        graGraphic.drawString(msg0, iCenterX
                - graGraphic.getFontMetrics().stringWidth(msg0) / 2,320 );
        String msg1 = "5 - Relaxed Game";
        int iX = iCenterX - graGraphic.getFontMetrics().stringWidth(msg1) / 2;
        graGraphic.drawString("1 - Classic Tetris", iX ,350 );
        graGraphic.drawString("2 - Horror Theme", iX, 370);
        graGraphic.drawString("3 - Move Fast", iX, 390);
        graGraphic.drawString("4 - Pop Music", iX, 410);
        graGraphic.drawString(msg1, iX, 430);
        
    }

    /**
     * drawNewGame
     * 
     * Method that paints the interface for a new game.
     * Because both the game over and new game screens are nearly 
     * identical, we can handle them together and just use a ternary 
     * operator to change the messages that are displayed.
     * 
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawNewGame(Graphics graGraphic) {
        graGraphic.setFont(fLargeFont);
        graGraphic.setColor(Color.DARK_GRAY);

        String msg = tetris.isNewGame() ? "TETRIS" : "GAME OVER";
        graGraphic.drawString(msg, iCenterX
                - graGraphic.getFontMetrics().stringWidth(msg) / 2, 100);
        
        graGraphic.setFont(fSmallFont);
        msg = "Press Enter to Play" + (tetris.isNewGame() ? "" : " Again");
        graGraphic.drawString(msg, iCenterX
                - graGraphic.getFontMetrics().stringWidth(msg) / 2, iCenterY);
        
        String msg0 = "Press a numbert to switch the song";
        graGraphic.drawString(msg0, iCenterX
                - graGraphic.getFontMetrics().stringWidth(msg0) / 2,320 );
        String msg1 = "5 - Relaxed Game";
        int iX = iCenterX - graGraphic.getFontMetrics().stringWidth(msg1) / 2;
        graGraphic.drawString("1 - Classic Tetris", iX ,350 );
        graGraphic.drawString("2 - Horror Theme", iX, 370);
        graGraphic.drawString("3 - Move Fast", iX, 390);
        graGraphic.drawString("4 - Pop Music", iX, 410);
        graGraphic.drawString(msg1, iX, 430);
    }

    /**
     * drawBoard
     * 
     * Method that paints the board when playing.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawBoard(Graphics graGraphic) {
        // Draw the titles onto the board.
        drawTiles(graGraphic);
        
        // Controls the animations.
        if (iAnim < 25) {
            iAnim++;
        } else {
            iAnim = 0;
        }

        // Draw the current piece. 
        drawCurrentTile(graGraphic);

        // Draw the background grid above the pieces (serves as a useful visual
        // for players, and makes the pieces look nicer by breaking them up.
        graGraphic.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < iColumns; x++) {
            for (int y = 0; y < iRowsVisible; y++) {
                graGraphic.drawLine(0, y * iTileSize, iColumns * iTileSize, y * iTileSize);
                graGraphic.drawLine(x * iTileSize, 0, x * iTileSize, iRowsVisible * iTileSize);
            }
        }
    }

    /**
     * drawTiles
     * 
     * Method that paints the tiles.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawTiles(Graphics graGraphic) {
        for (int x = 0; x < iColumns; x++) {
            for (int y = iRowsHidden; y < iRows; y++) {
                TileType tile = getTile(x, y);
                if (tile != null) {
                    drawTile(tile, x * iTileSize, (y - iRowsHidden)
                            * iTileSize, graGraphic);
                }
            }
        }
    }

    /**
     * drawCurrentTile
     * 
     * Method that paints the current tile falling.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
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
                    drawCurrentTile(type, (pieceCol + col) * iTileSize,
                            (pieceRow + row - iRowsHidden) * iTileSize,
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

    /**
     * drawGhost
     * 
     * Method that paints the current tile falling ghost or shadow.
     *
     * @param graGraphic is the <code>Graphic</code> of the game.
     * @param TTtype is the <code>TileType</code> of the piece to draw.
     * @param iPieceCol is the <code>integer</code> with the actual column
     * of the tile.
     * @param iPieceRow is the <code>integer</code> with the actual row
     * of the tile.
     * @param iRotation is the <code>integer</code> with the rotation value.
     */
    public void drawGhost(Graphics graGraphic, TileType TTtype, int iPieceCol,
            int iPieceRow, int iRotation) {
        Color base = TTtype.getBaseColor();
        base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 40);
        for (int lowest = iPieceRow; lowest < iRows; lowest++) {
            // If no collision is detected, try the next row.
            if (isValidAndEmpty(TTtype, iPieceCol, lowest, iRotation))
                continue;

            // Draw the ghost one row higher than the row of collision.
            lowest--;

            // Draw the ghost piece.
            for (int col = 0; col < TTtype.getDimension(); col++) {
                for (int row = 0; row < TTtype.getDimension(); row++) {
                    if (lowest + row >= 2 && TTtype.isTile(col, row, iRotation)) {
                        drawTile(base, base.brighter(), base.darker(),
                                (iPieceCol + col) * iTileSize,
                                (lowest + row - iRowsHidden) * iTileSize,
                                graGraphic);
                    }
                }
            }
            break;
        }
    }

    /**
     * drawTile
     * 
     * Method that draws a tile onto the board.
     *
     * @param ttType is the <code>TileType</code> of the piece to draw.
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    private void drawTile(TileType TTtype, int iX, int iY, 
            Graphics graGraphic) {
        drawTile(TTtype.getBaseColor(), TTtype.getLightColor(), 
                TTtype.getDarkColor(), iX, iY, graGraphic);
    }
    
    /**
     * drawCurrentTile
     * 
     * Method that draws the current tile onto the board.
     *
     * @param ttType is the <code>TileType</code> of the piece to draw.
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    private void drawCurrentTile(TileType ttType, int iX, int iY, 
            Graphics graGraphic) {
        drawCurrentTile(ttType.getBaseColor(), ttType.getLightColor(), 
                ttType.getDarkColor(), iX, iY, graGraphic);
    }

    /**
     * drawCurrentTile
     * 
     * Method that draws the current tile onto the board with the definition
     * of colors and shadows.
     *
     * @param cBase is the <code>base color</code> of tile.
     * @param cLight is the <code>light color</code> of the tile.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    private void drawCurrentTile(Color cBase, Color cLight, Color cDark, 
            int iX, int iY, Graphics graGraphic) {

        // Fill the entire tile with the base color.
        graGraphic.setColor(cLight);
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);

        // Fill the triangles inside the tile
        graGraphic.setColor(cBase);
        if (iAnim == 0) {
            drawTile0(iX, iY, cDark, graGraphic);
            
        } else if (iAnim == 13) {
            drawTile13(iX, iY, cDark, graGraphic);
            
        } else if (iAnim < 13) {
            drawTile13minus(iX, iY, cDark, graGraphic);
            
        } else {
            drawTile13plus(iX, iY, cDark, graGraphic);
        }
    }
    /**
     * drawTile0
     * 
     * Method that draws the first case of the method drawing the current tile.
     * 
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawTile0(int iX, int iY, Color cDark, Graphics graGraphic) {
        int xPoints[] = {iX, iX, iX + (iTileSize / 2)};
            int yPoints[] = {iY + iTileSize, iY, iY + (iTileSize / 2)};
            int xPoints2[] = {iX + (iTileSize / 2), iX + iTileSize, iX + 
                    iTileSize};
            int yPoints2[] = {iY + (iTileSize / 2), iY + iTileSize, iY};
            graGraphic.fillPolygon(xPoints, yPoints, 3);
            graGraphic.fillPolygon(xPoints2, yPoints2, 3);
            graGraphic.setColor(cDark);
            int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
            int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY + 
                    (iTileSize / 2)};
            graGraphic.fillPolygon(xPoints3, yPoints3, 3);
    }

    /**
     * drawTile13
     * 
     * Method that draws the second case of the method drawing the current tile.
     * 
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param graGraphic is the <code>Graphic</code> of the game. 
     */
    public void drawTile13(int iX, int iY, Color cDark, Graphics graGraphic) {
        int xPoints[] = {iX, iX, iX + (iTileSize / 2)};
            int yPoints[] = {iY + iTileSize, iY, iY};
            int xPoints2[] = {iX + (iTileSize / 2), iX + iTileSize, 
                iX + iTileSize};
            int yPoints2[] = {iY, iY + iTileSize, iY};
            graGraphic.fillPolygon(xPoints, yPoints, 3);
            graGraphic.fillPolygon(xPoints2, yPoints2, 3);
            graGraphic.setColor(cDark);
            int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
            int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY};
            graGraphic.fillPolygon(xPoints3, yPoints3, 3);
    }
    
    /**
     * drawTile13minus
     * 
     * Method that draws the third case of the method drawing the current tile.
     * 
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawTile13minus(int iX, int iY, Color cDark, 
            Graphics graGraphic){
    int xPoints[] = {iX, iX, iX + (iTileSize / 2), iX + iAnim};
            int yPoints[] = {iY, iY + iTileSize, iY + (iTileSize / 2) - iAnim, iY};
            int xPoints2[] = {iX + iTileSize, iX + iTileSize, iX + (iTileSize / 2), iX + iTileSize - iAnim};
            int yPoints2[] = {iY, iY + iTileSize, iY + (iTileSize / 2) - iAnim, iY};
            graGraphic.fillPolygon(xPoints, yPoints, 4);
            graGraphic.fillPolygon(xPoints2, yPoints2, 4);
            graGraphic.setColor(cDark);
            int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
            int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY + (iTileSize / 2) - iAnim};
            graGraphic.fillPolygon(xPoints3, yPoints3, 3);
}
    
    /**
     * drawTile13plus
     * 
     * Method that draws the fourth case of the method drawing the current tile.
     * 
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawTile13plus(int iX, int iY, Color cDark, 
            Graphics graGraphic) {
        int iAnm = 26 - iAnim; // Variable para Animación
            int xPoints[] = {iX, iX, iX + (iTileSize / 2), iX + iAnm};
            int yPoints[] = {iY, iY + iTileSize, iY + (iTileSize / 2) - iAnm, iY};
            int xPoints2[] = {iX + iTileSize, iX + iTileSize, iX + (iTileSize / 2), iX + iTileSize - iAnm};
            int yPoints2[] = {iY, iY + iTileSize, iY + (iTileSize / 2) - iAnm, iY};
            graGraphic.fillPolygon(xPoints, yPoints, 4);
            graGraphic.fillPolygon(xPoints2, yPoints2, 4);
            graGraphic.setColor(cDark);
            int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
            int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY + (iTileSize / 2) - iAnm};
            graGraphic.fillPolygon(xPoints3, yPoints3, 3);
    }
    
    
    /**
     * drawTile
     * 
     * Method that draws a shadow tile onto the board.
     *
     * @param cBase is the <code>base color</code> of tile.
     * @param cLight is the <code>light color</code> of the tile.
     * @param cDark is the <code>dark color</code> of the tile.
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param graGraphic is the <code>Graphic</code> of the game
     */
    private void drawTile(Color cBase, Color cLight, Color cDark, 
            int iX, int iY, Graphics graGraphic) {

        // Fill the entire tile with the base color.
        graGraphic.setColor(cLight);
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);

        // Fill the triangles inside the tile
        graGraphic.setColor(cBase);

        int xPoints[] = {iX, iX, iX + (iTileSize / 2)};
        int yPoints[] = {iY + iTileSize, iY, iY + (iTileSize / 2)};
        int xPoints2[] = {iX + (iTileSize / 2), iX + iTileSize, iX + iTileSize};
        int yPoints2[] = {iY + (iTileSize / 2), iY + iTileSize, iY};
        graGraphic.fillPolygon(xPoints, yPoints, 3);
        graGraphic.fillPolygon(xPoints2, yPoints2, 3);
        graGraphic.setColor(cDark);
        int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
        int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY + (iTileSize / 2)};
        graGraphic.fillPolygon(xPoints3, yPoints3, 3);

    }

    /**
     * setState
     * 
     * Method that modifies the state of the tile.
     * 
     * @param iState is an <code>integer matrix</code>.
     */
    public void setState(int[][] iState) {

        TTtiles = new TileType[iState.length][iState[0].length];

        for (int iC = 0; iC < iState.length; iC++) {
            for (int iJ = 0; iJ < iState[0].length; iJ++) {

                if (iState[iC][iJ] != -1) {
                    TTtiles[iC][iJ] = TileType.values()[iState[iC][iJ]];
                } else {
                    TTtiles[iC][iJ] = null;
                }
            }
        }
    }

    /**
     * getState
     * 
     * Method that access the state of the tile.
     * 
     * @return iState is an <code>integer matrix</code>.
     */
    public int[][] getState() {
        int iState[][] = new int[TTtiles.length][TTtiles[0].length];

        for (int iI = 0; iI < TTtiles.length; iI++) {
            for (int iJ = 0; iJ < TTtiles[0].length; iJ++) {

                if (TTtiles[iI][iJ] != null) {
                    iState[iI][iJ] = TTtiles[iI][iJ].getType();
                } else {
                    iState[iI][iJ] = -1;
                }
            }
        }
        return iState;
    }
}
