import java.util.Random;

/*
 * This class is the main Model of of the mine field. It consists of an array 
 * of MineFieldCells. The model initializes the minefield with mines (the
 * number provided by the Contoller) that are randomly distributed. When the 
 * mines are created, number cells that neighbour the mine cell are calculated
 * 
 */
public class MinefieldModel {
    /*
     * Enum type for the (real) contents of a cell
     */
    public enum ContentType {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, MINE
    };
    /*
     * Enum type for the (display) icons of a cell
     */
    public enum IconType {
        TILE_COVERED, TILE_EMPTY, NUMBER, MINE, MINE_EXPL, FLAG, REVERT_FLAG
    };

    // The main data structure that models the minefield
    private MineFieldCell[] field;
    // private int allCells;
    // private int minesLeft;
    private Random random;
    // Reference to the Controller object
    private MinesweeperMain mainController;

    /*
     * Constructor that takes in the main Conroller object and initializes the
     * random number generator
     */
    public MinefieldModel(MinesweeperMain mainController) {
        this.mainController = mainController;
        // The random number generator that will be used in selecting cells with mines
        random = new Random();
    }

    /*
     * Utility method to check wether the given content type is a number.
     */
    public static boolean isContentTypeNumber(ContentType contentType) {
        switch (contentType) {
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
            case FIVE:
            case SIX:
            case SEVEN:
            case EIGHT:
                return true;
            default:
                return false;
        }
    }

    /*
     * Increments the mine threat count of the neighbour of a newly created mine
     * cell by one, provided that the neighbour itself is not a mine cell.
     */
    private void updateMineNeighbourThreatCount(int neighbourPosition) {
        if (neighbourPosition >= 0 && neighbourPosition < field.length) {
            MineFieldCell neighbourCell = field[neighbourPosition];
            if (neighbourCell.getContentType() != ContentType.MINE) {
                switch (neighbourCell.getContentType()) {
                    case ZERO:
                        neighbourCell.setContentType(ContentType.ONE);
                        break;
                    case ONE:
                        neighbourCell.setContentType(ContentType.TWO);
                        break;
                    case TWO:
                        neighbourCell.setContentType(ContentType.THREE);
                        break;
                    case THREE:
                        neighbourCell.setContentType(ContentType.FOUR);
                        break;
                    case FOUR:
                        neighbourCell.setContentType(ContentType.FIVE);
                        break;
                    case FIVE:
                        neighbourCell.setContentType(ContentType.SIX);
                        break;
                    case SIX:
                        neighbourCell.setContentType(ContentType.SEVEN);
                        break;
                    case SEVEN:
                        neighbourCell.setContentType(ContentType.EIGHT);
                        break;
                    case MINE:
                        // cell.setContentType(ContentType.ONE);
                        break;
                    default:
                        // cell.setContentType(ContentType.ONE);
                        break;
                }
            }
        }
    }

    /*
     * Initializes the mine field by randomly determining a new mine cell 
     * position and update its 8 neigbours' mine threat situation.
     */
    public void initializeMinefield() {
        int numberOfCells = mainController.N_ROWS * mainController.N_COLS;
        field = new MineFieldCell[numberOfCells];
        for (int i = 0; i < numberOfCells; i++) {
            field[i] = new MineFieldCell();
        }
        // Randomly determine a new mine cell position and update its 8 neigbours'
        // mine threat situation.
        int minesCreated = 0;
        while (minesCreated < mainController.N_MINES) {
            int position = (int) ((numberOfCells - 1) * random.nextDouble());
            if ((field[position].getContentType() != ContentType.MINE)) {
                field[position].setContentType(ContentType.MINE);
                field[position].setIconType(IconType.TILE_COVERED);
                int current_col = position % mainController.N_COLS;
                if (current_col > 0) {
                    updateMineNeighbourThreatCount(position - 1 - mainController.N_COLS);
                    updateMineNeighbourThreatCount(position - 1);
                    updateMineNeighbourThreatCount(position + mainController.N_COLS - 1);
                }
                updateMineNeighbourThreatCount(position - mainController.N_COLS);
                updateMineNeighbourThreatCount(position + mainController.N_COLS);
                if (current_col < (mainController.N_COLS - 1)) {
                    updateMineNeighbourThreatCount(position - mainController.N_COLS + 1);
                    updateMineNeighbourThreatCount(position + mainController.N_COLS + 1);
                    updateMineNeighbourThreatCount(position + 1);
                }
                minesCreated++;
            }
        }
    }

    /*
     * Uncovers the visited cell with the 'empty cell icon' if its content is empty.
     * For numbered cells, updates the icon to open (numbered cell), but do not
     * continue further dicovering as the cell is at the boundary of the search.
     * For mine cells, does nothing.
     */
    private void updateIconAndContinueDiscovery(int cellPosition) {
        // Sanity check for valid indices
        if (cellPosition >= 0 && cellPosition < field.length) {
            MineFieldCell cell = field[cellPosition];
            // Continue only if the cell has not been 'opened/discovered' before:
            switch (cell.getContentType()) {
                case ZERO:
                    // Continue marking and further discovery if the icon was not already set to
                    // EMPTY (i.e. already visited).
                    if (cell.getIconType() != MinefieldModel.IconType.TILE_EMPTY) {
                        cell.setIconType(MinefieldModel.IconType.TILE_EMPTY);
                        discoverConnectedEmptyCells(cellPosition);
                    }
                    break;
                case ONE:
                case TWO:
                case THREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                    // For numbered cells, we update the icon (to open, numbered cell), but do not
                    // continue further dicovering
                    cell.setIconType(MinefieldModel.IconType.NUMBER);
                    break;
                case MINE:
                    // For mine cells, we neither update the icon (to open, mine cell), nor continue
                    // further dicovering.
                    // Actually, starting orignally from an emty cell, discovery process should
                    // never end up discovering mine cells
                    // The process should stop at numbered cells that neighbour mine cells.
                    // cell.setIconType(MinefieldModel.IconType.NUMBER);
                    break;
            }
        }
    }

    /*
     * If an empty cell is clicked by the user, all empty and numbered cells
     * that are reacheable by the cell are recursively uncovered. Mine cells
     * are avoided. Each cell has 8 immediate neighbours; namely, NW,N,NE,E
     * SE,S,SW,W. 
     */
    public void discoverConnectedEmptyCells(int emptyCell) {
        int current_col = emptyCell % mainController.N_COLS;
        if (current_col > 0) {
            updateIconAndContinueDiscovery(emptyCell - mainController.N_COLS - 1);
            updateIconAndContinueDiscovery(emptyCell - 1);
            updateIconAndContinueDiscovery(emptyCell + mainController.N_COLS - 1);
        }
        updateIconAndContinueDiscovery(emptyCell - mainController.N_COLS);
        updateIconAndContinueDiscovery(emptyCell + mainController.N_COLS);
        if (current_col < (mainController.N_COLS - 1)) {
            updateIconAndContinueDiscovery(emptyCell - mainController.N_COLS + 1);
            updateIconAndContinueDiscovery(emptyCell + mainController.N_COLS + 1);
            updateIconAndContinueDiscovery(emptyCell + 1);
        }

    }

    /*
     * Returns the cell that is at the given position in the minefield array.
     */
    public MineFieldCell getCell(int position) {
        if (position >= 0 && position < field.length) {
            return field[position];
        } else {
            return null;
        }
    }
}
