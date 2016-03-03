package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * SidePanel Class
 *
 * @authors Brendan Jones, arrangements by: luisfelipesv y melytc
 *
 * Luis Felipe Salazar A00817158 Melissa Janet Treviño A00816715
 *
 * 1/MAR/16
 * @version 2.0
 *
 * The {@code SidePanel} class is responsible for displaying various information
 * on the game such as the next piece, the score and current level, and
 * controls.
 *
 */
public class SidePanel extends JPanel {

    //Serial Version UID.
    private static final long lSerialVersionUID = 2181495598854992747L;

    // The dimensions of each tile on the next piece preview.
    private static final int iTileSize = BoardPanel.iTileSize >> 1;

    // The width of the shading on each tile on the next piece preview.
    private static final int iShadeWidth = BoardPanel.iShadeWidth >> 1;

    // The number of rows and columns in the preview window. 
    // Set to 5 because we can show any piece with some sort of padding.
    private static final int iTiles = 5;

    // The center x of the next piece preview box.
    private static final int iSquareCenterX = 130;

    // The center y of the next piece preview box.
    private static final int iSquareCenterY = 65;

    // The size of the next piece preview box.
    private static final int iSquareSize = (iTileSize * iTiles >> 1);

    // The number of pixels used on a small insets.
    private static final int iSmallInset = 20;

    // The number of pixels used on a large insets.
    private static final int iLargeInset = 40;

    // The y coordinate of the stats category.
    private static final int iStatsInset = 135;

    // The y coordinate of the controls category.
    private static final int iControlsInset = 250;

    // The number of pixels to offset between each string.
    private static final int iTextStride = 25;

    // The small font.
    private static final Font fSmallFont = new Font("Tahoma", Font.BOLD, 11);

    // The large font.
    private static final Font fLargeFont = new Font("Tahoma", Font.BOLD, 13);

    // The color to draw the text and preview box in.
    private static final Color cDrawColor = new Color(128, 192, 128);

    // The Tetris instance.
    private Tetris tetris;

    /**
     * SidePanel
     * 
     * Creates a new SidePanel and sets it's display properties.
     *
     * @param tetris The Tetris instance to use.
     */
    public SidePanel(Tetris tetris) {
        this.tetris = tetris;

        setPreferredSize(new Dimension(200, BoardPanel.iPanelHeight));
        setBackground(new Color(245, 245, 245));
    }

    @Override
    public void paintComponent(Graphics graGraphic) {
        super.paintComponent(graGraphic);
        //Set the color for drawing.
        graGraphic.setColor(cDrawColor);
        //This variable stores the current y coordinate of the string.
        // This way we can re-order, add, or remove new strings if necessary
        // without needing to change the other strings.
        int iOffset;
        // Draw the "Stats" category.
        graGraphic.setFont(fLargeFont);
        graGraphic.drawString("Stats", iSmallInset, iOffset = iStatsInset);
        graGraphic.setFont(fSmallFont);
        graGraphic.drawString("Level: " + tetris.getLevel(), iLargeInset, iOffset += iTextStride);
        graGraphic.drawString("Score: " + tetris.getScore(), iLargeInset, iOffset += iTextStride);
        drawControls(graGraphic, iOffset); // Draw the "Controls" category.
        graGraphic.setFont(fSmallFont); // Draw music theme playing.
        graGraphic.drawString("Music: " + tetris.getMusic(), 
                iLargeInset * 2, 15);
        // Draw the next piece preview box.
        graGraphic.setFont(fLargeFont);
        graGraphic.drawString("Next Piece", iSmallInset, 70);
        graGraphic.drawRect(iSquareCenterX - iSquareSize, iSquareCenterY 
                - iSquareSize, iSquareSize * 2, iSquareSize * 2);
        drawPreview(graGraphic);
    }

    /**
     * drawControls
     * 
     * Method that draws the controls instructions in the SidePanel.
     * 
     * @param graGraphic is the <code>Graphic</code> of the game.
     * @param iOffset is the <code>integer</code> value with the offset.
     */
    public void drawControls(Graphics graGraphic, int iOffset) {
        graGraphic.setFont(fLargeFont);
        graGraphic.drawString("Controls", iSmallInset, iOffset = iControlsInset);
        graGraphic.setFont(fSmallFont);
        graGraphic.drawString("<  Move Left", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString(">  Move Right", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("Z  Rotate Anticlockwise", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("X  Rotate Clockwise", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("v  Total Drop", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("P  Pause Game", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("S  Save Game", iLargeInset, 
                iOffset += iTextStride);
        graGraphic.drawString("L  Load Game", iLargeInset, 
                iOffset += iTextStride);
    }
    
    /**
     * drawPreview
     * 
     * Method that draws preview tile in the SidePanel.
     * Draw a preview of the next piece that will be spawned. The code is 
     * pretty much identical to the drawing code on the board, just smaller 
     * and centered, rather than constrained to a grid.
     * 
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    public void drawPreview(Graphics graGraphic) {
        TileType type = tetris.getNextPieceType();
        if (!tetris.isGameOver() && type != null) {
            //Get the size properties of the current piece.
            int cols = type.getCols();
            int rows = type.getRows();
            int dimension = type.getDimension();
            // Calculate the top left corner (origin) of the piece.
            int startX = (iSquareCenterX - (cols * iTileSize / 2));
            int startY = (iSquareCenterY - (rows * iTileSize / 2));
            // Get the insets for the preview. The default
            // rotation is used for the preview, so we just use 0.
            int top = type.getTopInset(0);
            int left = type.getLeftInset(0);
            //Loop through the piece and draw it's tiles onto the preview.
            for (int row = 0; row < dimension; row++) {
                for (int col = 0; col < dimension; col++) {
                    if (type.isTile(col, row, 0)) {
                        drawTile(type, startX + ((col - left) * iTileSize), 
                                startY + ((row - top) * iTileSize), graGraphic);
                    }
                }
            }
        }
    }
    
    /**
     * Draws a tile onto the preview window.
     *
     * @param ttType is the <code>TileType</code> of the piece to draw.
     * @param iX is the <code>integer</code> with the column.
     * @param iY is the <code>integer</code> with the row.
     * @param graGraphic is the <code>Graphic</code> of the game.
     */
    private void drawTile(TileType ttType, int iX, int iY, 
            Graphics graGraphic) {

        // Fill the entire tile with the light color.
        graGraphic.setColor(ttType.getLightColor());
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);
        graGraphic.fillRect(iX, iY, iTileSize, iTileSize);

        // Fill the bottom and right edges of the tile with the dark shading color.
        graGraphic.setColor(ttType.getBaseColor());
        int xPoints[] = {iX, iX, iX + (iTileSize / 2)};
        int yPoints[] = {iY + iTileSize, iY, iY + (iTileSize / 2)};
        int xPoints2[] = {iX + (iTileSize / 2), iX + iTileSize, iX + iTileSize};
        int yPoints2[] = {iY + (iTileSize / 2), iY + iTileSize, iY};
        graGraphic.fillPolygon(xPoints, yPoints, 3);
        graGraphic.fillPolygon(xPoints2, yPoints2, 3);

        graGraphic.setColor(ttType.getDarkColor());
        int xPoints3[] = {iX, iX + iTileSize, iX + (iTileSize / 2)};
        int yPoints3[] = {iY + iTileSize, iY + iTileSize, iY + (iTileSize / 2)};
        graGraphic.fillPolygon(xPoints3, yPoints3, 3);
    }
}
