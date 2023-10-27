This project implements the nostalgic Microsoft game Minesweeper as part of the the CBL Assignment.
 @author Ece Özhan
 @id 1958232
 @author Eliza Oborzyńska
 @id 1992368


 * DESCRIPTION:
 *      This project implements the nostalgic minesweeper game of Windows in
 *      a modern GUI with extended features. The project uses rich set of 
 *      Swing components (JFrame, JPanel, JButton, JRadioButton, 
 *      GridbagLayout, BoxLayout), event handling (ActionListener,
 *      MouseAdapter), and game configuration via properties file. It also
 *      features Object-Oriented techniques including inheritance (on Swing
 *      classes and TimerTask), encapsulation (via a access modifiers and 
 *      getter and setters) and polymorphism (via overriding paintComponent(),
 *      actionPerformed(), run())
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
