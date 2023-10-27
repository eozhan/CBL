import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Timer;

import javax.management.openmbean.OpenDataException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class TopControlPanel extends JPanel {

    MinesweeperMain mainControl;

    JLabel lblMineCounter;
    JLabel lblTimer;
    JButton btnRestart;

    JButton btnBeginner;
    JButton btnIntermediate;
    JButton btnExpert;
    
    private ImageIcon ICN_SMILEY;
    private ImageIcon ICN_SMILEY_COOL;
    private ImageIcon ICN_SMILEY_SAD;

    private ImageIcon ICN_ONE;
    private ImageIcon ICN_TWO;
    private ImageIcon ICN_THREE;

    public TopControlPanel(MinesweeperMain mainControl){
        this.mainControl = mainControl;
        initIcons();
        initialize();
    }

    public void setTime(int seconds){
        lblTimer.setText("Time: "+seconds);
    }

    public void setMinesLeft(int minesLeft){
        lblMineCounter.setText("Mines: " + minesLeft);        
    }   
    
    private void initIcons(){
        String iconPath = mainControl.ICON_PATH;
        String suffix = mainControl.ICON_SUFFIX;
        ICN_SMILEY = new ImageIcon(iconPath + "smiley" + suffix, "Restart");
        ICN_SMILEY_COOL = new ImageIcon(iconPath + "smiley-cool" + suffix, "You won!");
        ICN_SMILEY_SAD = new ImageIcon(iconPath + "smiley-sad" + suffix, "Bad Luck!");

        ICN_ONE = new ImageIcon(iconPath + "one" + suffix);
        ICN_TWO = new ImageIcon(iconPath + "two" + suffix);
        ICN_THREE = new ImageIcon(iconPath + "three" + suffix);
    }

    public void initialize(){
        lblMineCounter = new JLabel();
        lblMineCounter.setText("Mines: "+mainControl.getMinesLeft());
        lblTimer = new JLabel("Time: ");
        btnRestart = new JButton(ICN_SMILEY);
        btnRestart.setToolTipText("Restart Game");        
        btnRestart.setSize(40,40);
        btnRestart.setPreferredSize(new Dimension(40,40));
        btnRestart.setMaximumSize(new Dimension(38,38));
        btnRestart.setOpaque(false);
        btnRestart.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent evt){
                mainControl.restartGame();
            }
        });
    
        JRadioButton rbtnBeginner = new JRadioButton("", ICN_ONE, true);
        rbtnBeginner.setToolTipText("Beginner");
        rbtnBeginner.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent evt){
                mainControl.restartGame(MinesweeperMain.Difficulty.BEGINNER);
            }
        });
        JRadioButton rbtnIntermediate = new JRadioButton("", ICN_TWO, true);
        rbtnIntermediate.setToolTipText("Intermediate");
        rbtnIntermediate.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent evt){
                mainControl.restartGame(MinesweeperMain.Difficulty.INTERMEDIATE);
            }
        });
        JRadioButton rbtnExpert = new JRadioButton("", ICN_THREE, true);
        rbtnExpert.setToolTipText("Expert");
        rbtnExpert.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mousePressed(java.awt.event.MouseEvent evt){
                mainControl.restartGame(MinesweeperMain.Difficulty.EXPERT);
            }
        });
        
        ButtonGroup btgDifficulty = new ButtonGroup();
        btgDifficulty.add(rbtnBeginner);
        btgDifficulty.add(rbtnIntermediate);
        btgDifficulty.add(rbtnExpert);
        JPanel pnlDifficulty = new JPanel();
        pnlDifficulty.setLayout(new BoxLayout(pnlDifficulty, BoxLayout.X_AXIS));
        pnlDifficulty.add(rbtnBeginner);
        pnlDifficulty.add(rbtnIntermediate);
        pnlDifficulty.add(rbtnExpert);

        setLayout(new GridBagLayout());        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(lblMineCounter, gbc);

        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnRestart, gbc);

        gbc.weightx = 1;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(pnlDifficulty, gbc);

        gbc.weightx = 1;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;       
        add(lblTimer, gbc);
    
        //add(Box.createRigidArea(new Dimension(0,5)));
    }

    public void gameWon(){
        btnRestart.setIcon(ICN_SMILEY_COOL);
  //    lblMineCounter.setText("0");
       // timer.cancel();
  //    repaint();
    }
    public void gameLost(){
        btnRestart.setIcon(ICN_SMILEY_SAD);
//        lblMineCounter.setText("0");
       // repaint();
    }

    public void prepareStart(){
        btnRestart.setIcon(ICN_SMILEY);
        setMinesLeft(mainControl.getMinesLeft());
        setTime(0);         
    }
}
