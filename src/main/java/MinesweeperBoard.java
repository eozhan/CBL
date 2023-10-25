import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * The main View component of the mine sweeper game application. It maintains
 * the mine field. Although the model data structure is a single array, the 
 * view is rendered as a two dimensional mine field. Each cell kind has its
 * own icon. The MinesweeperMain object set in the constructor is the main 
 * Control object. Besides the main minefield panel, there is also a small
 * statu panel at the bottom.
 */
public class MinesweeperBoard extends JPanel {
    private ImageIcon ICN_TILE_EMPTY;
    private ImageIcon ICN_TILE_COVERED;
    private ImageIcon ICN_MINE;
    private ImageIcon ICN_MINE_EXPL;
    private ImageIcon ICN_FLAG;
    private ImageIcon ICN_FLAG_CROSSED;
    private ImageIcon ICN_ONE;
    private ImageIcon ICN_TWO;
    private ImageIcon ICN_THREE;
    private ImageIcon ICN_FOUR;
    private ImageIcon ICN_FIVE;
    private ImageIcon ICN_SIX;
    private ImageIcon ICN_SEVEN;
    private ImageIcon ICN_EIGHT;

    private int BOARD_WIDTH = 0;
    private int BOARD_HEIGHT = 0;
    // private boolean inGame;
    private int minesLeft;
    private final JLabel statusbar;

    private MinesweeperMain mainControl;

    /*
     * The constructor that takes in the main Control object and the status bat
     */
    public MinesweeperBoard(JLabel statusbar, MinesweeperMain mainControl) {
        this.statusbar = statusbar;
        this.mainControl = mainControl;
        addMouseListener(new MinesAdapter());
        initIcons();
        initBoard();
        initGUI();
    }

    /*
     * Creates the icons for the cells
     */
    private void initIcons(){
        String iconPath = mainControl.ICON_PATH;
        String suffix = mainControl.ICON_SUFFIX;
        ICN_TILE_EMPTY = new ImageIcon(iconPath + "tile-empty" + suffix);
        ICN_TILE_COVERED = new ImageIcon(iconPath + "tile-covered" + suffix);
        ICN_MINE = new ImageIcon(iconPath + "mine" + suffix);        
        ICN_MINE_EXPL = new ImageIcon(iconPath + "mine-exploded" + suffix);
        ICN_FLAG = new ImageIcon(iconPath + "flag" + suffix);
        ICN_FLAG_CROSSED = new ImageIcon(iconPath + "flag-crossed" + suffix);
        ICN_ONE = new ImageIcon(iconPath + "one" + suffix);
        ICN_TWO = new ImageIcon(iconPath + "two" + suffix);
        ICN_THREE = new ImageIcon(iconPath + "three" + suffix);
        ICN_FOUR = new ImageIcon(iconPath + "four" + suffix);
        ICN_FIVE = new ImageIcon(iconPath + "five" + suffix);
        ICN_SIX = new ImageIcon(iconPath + "six" + suffix);
        ICN_SEVEN = new ImageIcon(iconPath + "seven" + suffix);
        ICN_EIGHT = new ImageIcon(iconPath + "eight" + suffix);
    }
    
    /*
     * Initializes the game board control variables
     */
    public void initBoard() { 
        minesLeft = mainControl.getMinesLeft();
        statusbar.setText(Integer.toString(minesLeft));            
    }

    /*
     * Initializes the GUI of the minefield panel. The preferred size
     * is dynamically calculated from the row, column and cell size 
     * information taken from the Controller object
     */
    public void initGUI(){
        BOARD_WIDTH = mainControl.N_COLS * this.mainControl.CELL_SIZE_PIX + 1;
        BOARD_HEIGHT = mainControl.N_ROWS * this.mainControl.CELL_SIZE_PIX + 1;
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }

    /*
     * Restarts the game through board and GUI initializations. This method is 
     * called by the Controller object
     */
    public void  restartGame(){
        initBoard();
        initGUI();
        repaint();
    }

    // @Override
    /*
     * The overriding paintComponent method that performa one of the most 
     * critical tasks in the game. Traverses the cells one by one and renders
     * them on the the mine field GUI. If there are no more cells left to 
     * undiscover, then inform the Controller that the game is won .
     */
    public void paintComponent(Graphics g) {
        int cellsYetToDiscover = 0;
        Image imageToDraw = null;
        for (int i = 0; i < mainControl.N_ROWS; i++) {
            for (int j = 0; j < mainControl.N_COLS; j++) {
                MineFieldCell cell = mainControl.getCell((i * mainControl.N_COLS) + j);
                switch (cell.getIconType()) {
                    case TILE_EMPTY:
                        imageToDraw = ICN_TILE_EMPTY.getImage();
                        break;
                    case NUMBER: 
                        imageToDraw = getIconForNumberCell(cell);            
                        break;
                    case TILE_COVERED:
                        cellsYetToDiscover++;
                        imageToDraw = ICN_TILE_COVERED.getImage();;
                        break;
                    case MINE:
                        imageToDraw = ICN_MINE.getImage();
                        break;
                    case MINE_EXPL:
                        imageToDraw = ICN_MINE_EXPL.getImage();;
                        break;                        
                    case FLAG:
                        imageToDraw = ICN_FLAG.getImage();;
                        break;
                    case REVERT_FLAG:
                        imageToDraw = ICN_TILE_COVERED.getImage();
                        break;

                }
                //If the game is not ongoing (i.e., won or lost), we can reveal the underlying mines
                if (mainControl.getGameStatus()!=MinesweeperMain.GameStatus.ONGOING){
                    if (cell.getContentType()==MinefieldModel.ContentType.MINE){
                        if (cell.getIconType()!=MinefieldModel.IconType.MINE_EXPL){
                            imageToDraw = ICN_MINE.getImage();
                        }
                    }else if (cell.getContentType()!=MinefieldModel.ContentType.MINE && 
                                cell.getIconType()==MinefieldModel.IconType.FLAG){
                        imageToDraw = ICN_FLAG_CROSSED.getImage();
                    }else if (cell.getIconType()==MinefieldModel.IconType.TILE_COVERED){
                            imageToDraw = getIconForNumberCell(cell);
                    }
                }
                g.drawImage(imageToDraw, (j * mainControl.CELL_SIZE_PIX), (i * mainControl.CELL_SIZE_PIX), this);
            }
        }
        // If the game is on, but there are no more cells left to undiscover,
        // then inform the Controller that the game is won by the user. The 
        // status bar is further updated with the outcome of the game.
        if (mainControl.getGameStatus()==MinesweeperMain.GameStatus.ONGOING){
            if (cellsYetToDiscover == 0){
                mainControl.gameWon();
                statusbar.setText("Game won");
            }
        }else if (mainControl.getGameStatus()==MinesweeperMain.GameStatus.LOST){
            statusbar.setText("Game lost");
        }else if (mainControl.getGameStatus()==MinesweeperMain.GameStatus.WON){
            statusbar.setText("Game won");
        }
    }


    // This method is called to return the icon for a number cell.
    // If the cell was not a number (unlkely case), it return empty tile
    private Image getIconForNumberCell(MineFieldCell cell) {
        Image image = null;
        switch (cell.getContentType()){
            case ONE:
                image = ICN_ONE.getImage();
                break;
            case TWO:
                image = ICN_TWO.getImage();
                break;
            case THREE:
                image = ICN_THREE.getImage();
                break;
            case FOUR:
                image = ICN_FOUR.getImage();
                break;
            case FIVE:
                image = ICN_FIVE.getImage();
                break;
            case SIX:
                image = ICN_SIX.getImage();
                break;
            case SEVEN:
                image = ICN_SEVEN.getImage();
                break;
            case EIGHT:
                image = ICN_EIGHT.getImage();
                break;
            default:
                image = ICN_TILE_EMPTY.getImage();
                break;
        }
        return image;
    }   

    /*
     * Together with the paintComponent(), this MouseAdapter is one of the most
     * important methods of the game. It handles the left mouse click and
     * right mouse click events on the mine cells. The clicked cell is 
     * determined by dividing the coordinates to the cell pixel sizes.
     */
    private class MinesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int cCol = x / mainControl.CELL_SIZE_PIX;
            int cRow = y / mainControl.CELL_SIZE_PIX;

            if ((x < mainControl.N_COLS * mainControl.CELL_SIZE_PIX) && (y < mainControl.N_ROWS * mainControl.CELL_SIZE_PIX)) {
                MineFieldCell cell = mainControl.getCell((cRow * mainControl.N_COLS) + cCol);
                if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) {
                    if (cell.getContentType() == MinefieldModel.ContentType.MINE) {
                        if (cell.getIconType() == MinefieldModel.IconType.TILE_COVERED) {
                            if (minesLeft > 0) {
                                cell.setIconType(MinefieldModel.IconType.FLAG);
                                minesLeft--;
                                statusbar.setText(minesLeft + "");
                                mainControl.setMinesLeft(minesLeft);
                            } else {
                                statusbar.setText("Game won!");
                                mainControl.gameWon();
                            }
                        } else if (cell.getIconType() == MinefieldModel.IconType.FLAG) {
                            cell.setIconType(MinefieldModel.IconType.TILE_COVERED);
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                            mainControl.setMinesLeft(minesLeft);
                        }

                    } else {
                        // The user has right clicked to mark a cell that is not mine in reality
                        // The same behavour as above. But check it
                        if (cell.getIconType() == MinefieldModel.IconType.TILE_COVERED) {
                            if (minesLeft > 0) {
                                cell.setIconType(MinefieldModel.IconType.FLAG);
                                minesLeft--;
                                statusbar.setText(minesLeft + "");
                                mainControl.setMinesLeft(minesLeft);
                            } else {// Normally this case should not happen
                                statusbar.setText("No mines left");
                            }
                        } else if (cell.getIconType() == MinefieldModel.IconType.FLAG) {
                            cell.setIconType(MinefieldModel.IconType.TILE_COVERED);
                            minesLeft++;
                            statusbar.setText(minesLeft + "");
                            mainControl.setMinesLeft(minesLeft);
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (MinefieldModel.isContentTypeNumber(cell.getContentType())) {
                        cell.setIconType(MinefieldModel.IconType.NUMBER);
                    }
                    if (cell.getContentType() == MinefieldModel.ContentType.MINE) {
                        cell.setIconType(MinefieldModel.IconType.MINE_EXPL);
                        mainControl.gameLost();
                    }
                    if (cell.getContentType() == MinefieldModel.ContentType.ZERO) {
                        mainControl.discoverConnectedEmptyCells((cRow * mainControl.N_COLS) + cCol);
                    }
                }
            }
            repaint();
        }
    }
}
