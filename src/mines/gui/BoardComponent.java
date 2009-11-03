package mines.gui;

import mines.model.*;
import mines.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.awt.*;

/**
 * MinesPanel class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @since 24-jun-2006
 */
public class BoardComponent extends BackBufferComponent {
    private static final int PRESSED = 0;
    private static final int FLAG = 13;
    private static final int WRONG_FLAG = 11;
    private static final int EXPLOTED_MINE = 9;
    private static final int MINE = 10;
    private static final int COVERED = 12;

    private ClientSession session;
    int boardWidth, boardHeight;
    int cellWidth, cellHeight;

    final MouseInputListener mouseInputListener;
    Insets borderInsets;
    Image[][] fields = new Image[2][];

    private Cell pressed;
    private boolean adjacentsPressed;

    public BoardComponent(ClientSession session, ImageLoader images) {
        this.session = session;
        this.fields[0] = images.getImages("fields_white");
        this.fields[1] = images.getImages("fields");
        // Add listeners
        mouseInputListener = getMouseInputListener();
        addMouseListener(mouseInputListener);
        addMouseMotionListener(mouseInputListener);
    }

    boolean isCellPressed() {
        return pressed != null;
    }

    public void setSize(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.cellWidth = boardWidth / session.getBoard().getXSize();
        this.cellHeight = boardHeight / session.getBoard().getYSize();
        Border border = BorderFactory.createLoweredBevelBorder();
        setBorder(border);
        this.borderInsets = border.getBorderInsets(this);
        super.setSize(boardWidth + borderInsets.left + borderInsets.right,
                boardHeight + borderInsets.top + borderInsets.bottom);
        clearBackBuffer();
    }

    private Cell getCell(MouseEvent e) {
        return session.getBoard().getCellInRange(
                (e.getX() - borderInsets.left) / cellWidth,
                (e.getY() - borderInsets.top) / cellHeight);
    }


    private MouseInputListener getMouseInputListener() {
        return new MouseInputAdapter() {

            private boolean isBothMouseButtonDown(MouseEvent e) {
                return (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0 &&
                        (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0;
            }

            private boolean isAnyMouseButtonDown(MouseEvent e) {
                return (e.getModifiersEx() & (InputEvent.BUTTON1_DOWN_MASK + InputEvent.BUTTON3_DOWN_MASK)) != 0;
            }

            public void mouseDragged(MouseEvent e) {
                Cell cellPressed = getCell(e);
                if (pressed != null && !pressed.equals(cellPressed)) {
                    pressed = cellPressed;
                    repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
                Cell cellPressed = getCell(e);
                if (cellPressed != null) {
                    if (SwingUtilities.isMiddleMouseButton(e) || isBothMouseButtonDown(e)) {
                        pressed = cellPressed;
                        adjacentsPressed = true;
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        pressed = cellPressed;
                        adjacentsPressed = false;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        session.toggleFlag(cellPressed);
                        pressed = null;
                        adjacentsPressed = false;
                    }

                    repaint();
                    ((MinesPanel)getParent()).scoreBoardComponent.repaint();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (pressed != null) {
                    if (SwingUtilities.isMiddleMouseButton(e) || isAnyMouseButtonDown(e)) {
                        session.uncoverAdjacentsIfMinesEqualsFlags(pressed);
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        session.uncoverByMyself(pressed);
                    }
                    pressed = null;

                    repaint();
                    ((MinesPanel)getParent()).scoreBoardComponent.repaint();
                }
            }
        };
    }

    protected void restoreBackBuffer() {
        session.addAllToQueue();
    }

    protected void renderBackBuffer() {
        Graphics2D g = createBackBufferGraphics();
        g.translate(borderInsets.left, borderInsets.top);
        renderBoard(g);
        g.dispose();
    }

    private void renderBoard(Graphics2D g) {
        for(Cell cell : session.drainQueue()) {
            renderCell(g, cell);
        }
        if (session.getMyself().isReady() && pressed != null) {
            renderPressedCells(g);
        }
    }

    private void renderPressedCells(Graphics g) {
        renderPressedCell(g, pressed);
        if (adjacentsPressed) {
            for(Cell adj : pressed.getAdjacents()) {
                renderPressedCell(g, adj);
            }
        }
    }

    private void renderPressedCell(Graphics g, Cell cell) {
        if (cell.isCovered() && !cell.hasFlag()) {
            drawImage(g, fields[1][PRESSED], cell);
            session.getQueue().add(cell);
        }
    }


    private void renderCell(Graphics g, Cell cell) {
        Player player = cell.isUncovered()? cell.getUncoveredBy() : cell.getSetFlagBy();
        int index = session.getMyself().equals(player)? 1 : 0;
        drawImage(g, fields[index][getImageNumber(cell)], cell);
    }


    private static void drawImage(Graphics g, Image image, Cell cell) {
        g.drawImage(image, cell.getX() * image.getWidth(null),
                cell.getY() * image.getHeight(null) , null);
    }


    private int getImageNumber(Cell cell) {
        if (cell.isCovered()) {
            if (!session.getMyself().isReady()) {
                if (cell.hasFlag() && !cell.hasMine()) {
                    return WRONG_FLAG;
                }
                if (session.getWinner() != null || session.isStandaloneGameMode()) {
                    if (!cell.hasFlag() && cell.hasMine()) {
                        return MINE;
                    }
                }
            }
            return cell.hasFlag()? FLAG : COVERED;
        }
        return cell.hasMine()? EXPLOTED_MINE : cell.getNumAdjacentMines();
    }


}
