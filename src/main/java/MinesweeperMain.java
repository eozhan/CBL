import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 * CBL Project - Minesweeper Game
 * 
 * Usage:
 * DESCRIPTION:
 *      This project implements the nostalgic minesweeper game of Windows in
 *      a more modern GUI with extended features. The project uses advanced 
 *      Swing constructs (JFrame, JPanel, JButton, JRadioButton, 
 *      GridbagLayout, BoxLayout), event handling (ActionListener,
 *      MouseAdapter), and game configuration via properties file. The design
 *      also follows the most widely used GUI interaction design pattern, 
 *      Model-View-Controller.
 *      <p>
 *      The game starts by taking into account the difficulty level and the
 *      related parameters in properties file. There are 3 dfficulty levels:
 *      beginner (9x9, 40 mines), intermediate (16x16, 40 mines) and expert
 *      (16x30, 99 mines). The user can restart the game any time by clicking
 *      the restart button seen as the smiley icon. As the game unfolds, status
 *      on the remaining mines (as user flags assumed mine cells) and the time
 *      elapsed are displayed. If the user wins, the smiley icon turns to 'cool
 *      smiley' and if the user loses by stepping on a mine, the icon turns to
 *      'sad smiley'. In either case of win or lose, the minefield is displayed
 *      to the user in open form. The user can any time switch to a different 
 *      difficulty level by clicking on the 1,2,3, buttons on the top control 
 *      panel. The same difficulty level is kept in subsequent restarts.
 *
 * INPUT/OUTPUT SPECIFICATIONS:
 *      The user specifies configuration parameters in the config.properties
 *      file and launches the main application that starts the Minesweeper
 *      game GUI. The user inputs are provided by mouse clicks on the mine
 *      field and on the top control panel. Output responses are instantly
 *      provided in the GUI. The game ends by user clicking on the close (x)
 *      button at the top right corner of the main frame.
 * 
 * @author Ece Özhan
 * @id 1958232
 * @author Eliza Oborzyńska
 * @id 1992368
 */

 /*
  * This is the main Controller class of the game. It manages the Views
  * (minesweeperBoard and TopControlPanel) and the Models (MinefieldModel
  * and MinefieldCell). Access and population of the game configuration 
  * parameters from the config.properties file are also done in thie class.
  */
public class MinesweeperMain {
    /*
     * enum for keeping the game status, which can be in ongoing, won or 
     * lost at any given time.
     */
    public enum GameStatus {ONGOING, WON, LOST};
    /*
     * enum for keeping the game difficulty level, which can be beginner
     * (9x9, 10 mines), intermediate (16x16, 40 mines) or expert (16x30,
     * 99 mines), as parametrized in the properties file.
     */
    public enum Difficulty {BEGINNER, INTERMEDIATE, EXPERT};

    // The main Model object for the minefield representation.
    private MinefieldModel minefieldModel;   

    public MinefieldModel getMinefieldModel() {
        return minefieldModel;
    }

    // Gme status instance variable
    private GameStatus gameStatus;  
    public GameStatus getGameStatus() {
        return gameStatus;
    }
    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
  
    // Keeps track of how many mines are left. This value is communicated
    // with the model and view objects. It is one of the key instruments
    // in determining the end of the game
    private int minesLeft;
    public int getMinesLeft(){
        return minesLeft;
    }
    public void setMinesLeft(int minesLeft){
        this.minesLeft = minesLeft;
        if (pnlTopControl!=null){
            pnlTopControl.setMinesLeft(minesLeft);
        }
    }

    // The main frame that all the GUI elements reside in
    private JFrame frmMineSweeper;
    // The minefield game board panel
    private MinesweeperBoard pnlMineSweeperBoard;
    // The status bar at the bottom
    private JLabel lblStatusbar;
    // The control panel at the top. It conatins the mine count, restart 
    // button (smiley icon), game difficulty level buttons and the timer
    private TopControlPanel pnlTopControl;
    // The timer used to tick the timer counter every second.
    private MinesweeperTimerTask timerTask;
    // The following configuration parameters are read/overrridden from the config.properties
    public String ICON_PATH = "src/main/resources/icons/";
    public String ICON_SUFFIX = ".png";     
    public Difficulty DIFFICULTY = Difficulty.BEGINNER;
    public int N_MINES = 10;
    public int N_ROWS = 9;
    public int N_COLS = 9;
    public int CELL_SIZE_PIX = 32;

    private int N_MINES_BEGIN = 10;
    private int N_ROWS_BEGIN = 9;
    private int N_COLS_BEGIN = 9;
    private int N_MINES_INT = 10;
    private int N_ROWS_INT = 9;
    private int N_COLS_INT = 9;
    private int N_MINES_EXP = 10;
    private int N_ROWS_EXP = 9;
    private int N_COLS_EXP = 9;

    // Reads key configuration prameters from the properties file
    private void readProperties(){
        //Read the game configuration parameters from the config.properties file
        String configPath =  "src/main/resources/config.properties";
        try (InputStream input = new FileInputStream(configPath)) {
            // Create a new Properties object
            Properties prop = new Properties();
            // Use the Properties objects to load the properties file
            prop.load(input);
            // get the property values and print them out
            N_MINES_BEGIN = Integer.parseInt(prop.getProperty("mine.count.beginner"));
            N_ROWS_BEGIN = Integer.parseInt(prop.getProperty("minefield.dim.rows.beginner"));
            N_COLS_BEGIN = Integer.parseInt(prop.getProperty("minefield.dim.cols.beginner"));
            N_MINES_INT = Integer.parseInt(prop.getProperty("mine.count.intermediate"));
            N_ROWS_INT = Integer.parseInt(prop.getProperty("minefield.dim.rows.intermediate"));
            N_COLS_INT = Integer.parseInt(prop.getProperty("minefield.dim.cols.intermediate"));
            N_MINES_EXP = Integer.parseInt(prop.getProperty("mine.count.expert"));
            N_ROWS_EXP = Integer.parseInt(prop.getProperty("minefield.dim.rows.expert"));
            N_COLS_EXP = Integer.parseInt(prop.getProperty("minefield.dim.cols.expert"));
            int difficulty =  Integer.parseInt(prop.getProperty("difficulty.level"));
            DIFFICULTY = Difficulty.values()[difficulty];
            setParametersBasedOnDifficulty(DIFFICULTY);
            CELL_SIZE_PIX = Integer.parseInt(prop.getProperty("cell.size.pixels"));
        } catch (IOException ex) {
            System.out.println("Error occuurred while reading config.properties file. Keeping default values.");
            ex.printStackTrace();
        }
    }

    // Sets the number of mines, rows and columns parameters based on the given
    // difficulty level. All values are nitially read from the properties file
    private void setParametersBasedOnDifficulty(Difficulty dif){
        switch (dif){
            case BEGINNER:
                N_MINES = N_MINES_BEGIN;
                N_ROWS = N_ROWS_BEGIN;
                N_COLS = N_COLS_BEGIN;
                break;
            case INTERMEDIATE:
                N_MINES = N_MINES_INT;
                N_ROWS = N_ROWS_INT;
                N_COLS = N_COLS_INT;
                break;
            case EXPERT:
                N_MINES = N_MINES_EXP;
                N_ROWS = N_ROWS_EXP;
                N_COLS = N_COLS_EXP;
                break;
            }
    }

    // Initializes the minefield Model object
    private void initModel() {
        setMinesLeft(N_MINES);
        minefieldModel = new MinefieldModel(this);
        minefieldModel.initializeMinefield();
    }

    // Initializes the GUI View objects
    private void initGUI() {
        frmMineSweeper = new JFrame();
        pnlTopControl = new TopControlPanel(this);
        frmMineSweeper.add(pnlTopControl, BorderLayout.NORTH);

        lblStatusbar = new JLabel("");
        frmMineSweeper.add(lblStatusbar, BorderLayout.SOUTH);

        pnlMineSweeperBoard = new MinesweeperBoard(lblStatusbar, this);
        frmMineSweeper.add(pnlMineSweeperBoard);

        frmMineSweeper.setResizable(false);
        frmMineSweeper.pack();
        frmMineSweeper.setTitle("Minesweeper");
        frmMineSweeper.setLocationRelativeTo(null);
        frmMineSweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    // Returns the cell at the given index position in the minefield Model
    public MineFieldCell getCell(int position){
        return getMinefieldModel().getCell(position);
    }

    // Triggered by the View to initiate the discovery of the empty cells
    // in the Model, upon user click
    public void discoverConnectedEmptyCells(int position){
        getMinefieldModel().discoverConnectedEmptyCells(position);
    }

    // Handles the chores for winning the game. Specifically, the displays
    // in the top control panel and the minefield are updated accordingly
    public void gameWon(){
        gameStatus = GameStatus.WON;
        pnlTopControl.gameWon();
        redrawFrame();
    }

    // Handles the chores for loss of the game. Specifically, the displays
    // in the top control panel and the minefield are updated accordingly
    public void gameLost(){
        gameStatus = GameStatus.LOST;
        pnlTopControl.gameLost();
        redrawFrame(); 
    }

    // Restarts the game with the existing difficulty. Triggred by clicking 
    // on the smiley icon on the top control panal.
    public void restartGame(){
        restartGame(DIFFICULTY);
    }

    // Restarts the game with the selected difficulty. Triggred by clicking 
    // on the difficu;ty level buttons in the top control panal.
    public void restartGame(Difficulty level){
        DIFFICULTY = level;
        setParametersBasedOnDifficulty(level);
        initModel();
        gameStatus = GameStatus.ONGOING;
        pnlMineSweeperBoard.restartGame();
        pnlTopControl.prepareStart();          
        timerTask.seconds = 0;
        // pnlMineSweeperBoard.repaint();
        redrawFrame();  
    }

    // Forces a redraw of the top-level container. Used multiple times 
    // in response to changes of the game status and user actions/clicks
    private void redrawFrame(){
        frmMineSweeper.pack();
        frmMineSweeper.repaint(); 
    }
    
    // Launches the game for the first time. Used by the main method.
    private void launchGame(){
        readProperties();
        initModel();
        initGUI();
        gameStatus = GameStatus.ONGOING;
        //Start the timer
        timerTask = new MinesweeperTimerTask();
        new Timer().schedule(timerTask, 0, 1000);

        EventQueue.invokeLater(() -> {
            var ex = frmMineSweeper; //new MinesweeperFrame(minefieldModel);
            ex.setVisible(true);        
        });
    }


    public static void main(String[] args) {        
        new MinesweeperMain().launchGame();       
    }

    /*
     * Inner class that extend TimerTask. Used for time counting. 
     * Updates the timer label on the top control panel every second.
     */
    public class MinesweeperTimerTask extends TimerTask{
        public static int seconds = 0;
        public void run() {
            pnlTopControl.setTime(seconds++);
        } 
    }

}
