import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * This class provides a GUI for solitaire games related to Elevens.
 */
public class CardGameGUI extends JFrame implements ActionListener {
    
    /** Height of the game frame. (302) */
    private static final int DEFAULT_HEIGHT = 816;
    /** Width of the game frame. (800) */
    private static final int DEFAULT_WIDTH = 1459;
    /** Width of a card.*/
    private static final int CARD_WIDTH = 73;
    /** Height of a card.*/
    private static final int CARD_HEIGHT = 97;
    /** Row (y coord) of the upper left corner of the first card. */
    private static final int LAYOUT_TOP = 30;
    /** Column (x coord) of the upper left corner of the first card. */
    private static final int LAYOUT_LEFT = 30;
    /** Distance between the upper left x coords of
     *  two horizonally adjacent cards. */
    private static final int LAYOUT_WIDTH_INC = 100;
    /** Distance between the upper left y coords of
     *  two vertically adjacent cards. (125)*/
    private static final int LAYOUT_HEIGHT_INC = 30;
    /** y coord of the "Replace" button. */
    private static final int BUTTON_TOP = 30;
    /** x coord of the "Replace" button. (570)*/
    private static final int BUTTON_LEFT = 1070;
    /** Distance between the tops of the "Replace" and "Restart" buttons. */
    private static final int BUTTON_HEIGHT_INC = 50;
    /** y coord of the "n undealt cards remain" label. */
    private static final int LABEL_TOP = 160;
    /** x coord of the "n undealt cards remain" label. (540)*/
    private static final int LABEL_LEFT = 1040;
    /** Distance between the tops of the "n undealt cards" and
     *  the "You lose/win" labels. */
    private static final int LABEL_HEIGHT_INC = 35;

    /** The board (Board subclass). */
    private Board board;

    /** The main panel containing the game components. */
    private JPanel panel;
    /** The Replace button. */
    private JButton replaceButton;
    /** The Restart button. */
    private JButton restartButton;
    /** The Deal button. */
    private JButton dealButton;
    /** The "number of undealt cards remain" message. */
    private JLabel statusMsg;
    /** The "you've won n out of m games" message. */
    private JLabel totalsMsg;
    /** The card displays for tableau. */
    private JLabel[][] tDisplayCards;
    /** The card displays for foundation. */
    private JLabel[] fDisplayCards;
    /** The card displays for deck. */
    private JLabel dDisplayCards;
    /** The win message. */
    private JLabel winMsg;
    /** The loss message. */
    private JLabel lossMsg;
    /** The coordinates of the card displays on tableau. */
    private Point[][] tCardCoords;
    /** The coordinates of the card displays on foundation. */
    private Point[] fCardCoords;
    /** The coordinates of the card display on deck. */
    private Point dCardCoords;
    

    /** kth element is true iff the user has selected card #k. */
    private int[][] selections;
    /** The number of games won. */
    private int totalWins;
    /** The number of games played. */
    private int totalGames;
    
    /**
     * Flag used to control debugging print statements.
     */
    private static final boolean I_AM_DEBUGGING = true;


    /**
     * Initialize the GUI.
     * @param gameBoard is a <code>Board</code> subclass.
     */
    public CardGameGUI(Board gameBoard) {
        board = gameBoard;
        totalWins = 0;
        totalGames = 0;

        // Initialize tcardCoords using 7 cards per row
        tCardCoords = new Point[board.tabRowSize()][board.tabColSize()];
        int x = LAYOUT_LEFT;
        int y = LAYOUT_TOP;
        for (int r = 0; r < board.tabRowSize(); r++) {
            for(int c = 0; c < board.tabColSize(); c++){
                x = LAYOUT_LEFT + LAYOUT_WIDTH_INC * c;
                y = 500 - (LAYOUT_HEIGHT_INC * r);
                tCardCoords[r][c] = new Point(x, y);
                if (I_AM_DEBUGGING) {
                    //System.out.println(x + " " + y);
                    //System.out.println(cardCoords[r][c]);
                    //System.out.println(LAYOUT_LEFT + LAYOUT_WIDTH_INC * c);
                    //System.out.println("\n");
                }
            }
        }
        
        // Initialize fCardCoords using 4 cards per row
        fCardCoords = new Point[4];
        for (int fPileIndex = 0; fPileIndex < 4; fPileIndex++) {
            x = LAYOUT_LEFT + LAYOUT_WIDTH_INC * fPileIndex;
            y = LAYOUT_TOP;
            fCardCoords[fPileIndex] = new Point(x, y);
        }
        
        // Initialize dCardCoords
        dCardCoords = new Point(LAYOUT_LEFT, 700 - LAYOUT_TOP);

        selections = new int[board.tabRowSize()][board.tabColSize()];
        initDisplay();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        repaint();
        if (I_AM_DEBUGGING) {
            //System.out.println(board.tabCardAt(0,0));
            //System.out.println(board.tabCardAt(1,0));
        }
    }

    /**
     * Run the game.
     */
    public void displayGame() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    /**
     * Draw the display (cards and messages).
     */
    public void repaint() {
        for (int r = 0; r < board.tabRowSize(); r++) {
            for (int c = 0; c < board.tabColSize(); c++) {
                String cardImageFileName =
                    imageFileName(board.tabCardAt(r,c), selections[r][c]);
                    URL imageURL = getClass().getResource(cardImageFileName);
                if (I_AM_DEBUGGING) {
                    //System.out.println(displayCards[r][c]);
                    //System.out.println(selections[r][c]);
                    //System.out.println(cardImageFileName);
                    //System.out.println(board.tabCardAt(r,c));
                    //System.out.println(imageURL);
                }
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    tDisplayCards[r][c].setIcon(icon);
                    tDisplayCards[r][c].setVisible(true);
                    if (I_AM_DEBUGGING) {
                        //System.out.println(icon);
                    }
                } else {
                    throw new RuntimeException(
                    "Card image not found: \""
                    + cardImageFileName + "\"");
                }
            }
        }
        if (I_AM_DEBUGGING) {
            //System.out.println(displayCards[0][0]);
        }
        for (int fPileIndex = 0; fPileIndex < 4; fPileIndex++) {
            String cardImageFileName =
                imageFileName(board.fCardAt(fPileIndex), 0);
                URL imageURL = getClass().getResource(cardImageFileName);
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                fDisplayCards[fPileIndex].setIcon(icon);
                fDisplayCards[fPileIndex].setVisible(true);
            } else {
                throw new RuntimeException(
                "Card image not found: \""
                + cardImageFileName + "\"");
            }
        }
        
        String cardImageFileName =
                imageFileName(board.dCardAt(), 0);
                URL imageURL = getClass().getResource(cardImageFileName);
        if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                dDisplayCards.setIcon(icon);
                dDisplayCards.setVisible(true);
            } else {
                throw new RuntimeException(
                "Card image not found: \""
                + cardImageFileName + "\"");
            }
        
        statusMsg.setText(board.deckSize()
            + " undealt cards remain.");
        statusMsg.setVisible(true);
        totalsMsg.setText("You've won " + totalWins
             + " out of " + totalGames + " games.");
        totalsMsg.setVisible(true);
        pack();
        panel.repaint();
    }

    /**
     * Initialize the display.
     */
    private void initDisplay()  {
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };

        // If board object's class name follows the standard format
        // of ...Board or ...board, use the prefix for the JFrame title
        String className = board.getClass().getSimpleName();
        int classNameLen = className.length();
        int boardLen = "Board".length();
        String boardStr = className.substring(classNameLen - boardLen);
        if (boardStr.equals("Board") || boardStr.equals("board")) {
            int titleLength = classNameLen - boardLen;
            setTitle(className.substring(0, titleLength));
        }

        // Calculate number of rows of cards (5 cards per row)
        // and adjust JFrame height if necessary
        int numCardRows = board.tabColSize();
        int height = DEFAULT_HEIGHT;
        if (numCardRows > 2) {
            height += (numCardRows - 2) * LAYOUT_HEIGHT_INC;
        }

        this.setSize(new Dimension(DEFAULT_WIDTH, height));
        panel.setLayout(null);
        panel.setPreferredSize(
            new Dimension(DEFAULT_WIDTH - 20, height - 20));
        tDisplayCards = new JLabel[board.tabRowSize()][board.tabColSize()];
        for (int r = 0; r < board.tabRowSize(); r++) {
            for (int c = 0; c < board.tabColSize(); c++){
                tDisplayCards[r][c] = new JLabel();
                panel.add(tDisplayCards[r][c]);
                if (I_AM_DEBUGGING) {
                    //System.out.println(displayCards[r][c]);
                }
                tDisplayCards[r][c].setBounds(tCardCoords[r][c].x, tCardCoords[r][c].y,
                                        CARD_WIDTH, CARD_HEIGHT);
                if (I_AM_DEBUGGING) {
                    //System.out.println(displayCards[r][c] + "\n");
                    //System.out.println(cardCoords[r][c]);
                }
                tDisplayCards[r][c].addMouseListener(new MyMouseListener());
                selections[r][c] = 0;
            }
        }
        fDisplayCards = new JLabel[4];
        for (int fPileIndex = 0; fPileIndex < 4; fPileIndex++) {
            fDisplayCards[fPileIndex] = new JLabel();
            panel.add(fDisplayCards[fPileIndex]);
            fDisplayCards[fPileIndex].setBounds(fCardCoords[fPileIndex].x, fCardCoords[fPileIndex].y,
                                CARD_WIDTH, CARD_HEIGHT);
            fDisplayCards[fPileIndex].addMouseListener(new MyMouseListener());
        }
        dDisplayCards = new JLabel();
        panel.add(dDisplayCards);
        dDisplayCards.setBounds(dCardCoords.x, dCardCoords.y,
                                CARD_WIDTH, CARD_HEIGHT);
        dDisplayCards.addMouseListener(new MyMouseListener());
        
        replaceButton = new JButton();
        replaceButton.setText("Replace");
        panel.add(replaceButton);
        replaceButton.setBounds(BUTTON_LEFT, BUTTON_TOP, 100, 30);
        replaceButton.addActionListener(this);

        restartButton = new JButton();
        restartButton.setText("Restart");
        panel.add(restartButton);
        restartButton.setBounds(BUTTON_LEFT, BUTTON_TOP + BUTTON_HEIGHT_INC,
                                        100, 30);
        restartButton.addActionListener(this);
        
        dealButton = new JButton();
        dealButton.setText("Deal");
        panel.add(dealButton);
        dealButton.setBounds(BUTTON_LEFT, BUTTON_TOP + 2 * BUTTON_HEIGHT_INC,
                                        100, 30);
        dealButton.addActionListener(this);

        statusMsg = new JLabel(
            board.deckSize() + " undealt cards remain.");
        panel.add(statusMsg);
        statusMsg.setBounds(LABEL_LEFT, LABEL_TOP + BUTTON_HEIGHT_INC, 250, 30);

        winMsg = new JLabel();
        winMsg.setBounds(LABEL_LEFT, LABEL_TOP + LABEL_HEIGHT_INC + BUTTON_HEIGHT_INC, 200, 30);
        winMsg.setFont(new Font("SansSerif", Font.BOLD, 25));
        winMsg.setForeground(Color.GREEN);
        winMsg.setText("You win!");
        panel.add(winMsg);
        winMsg.setVisible(false);

        lossMsg = new JLabel();
        lossMsg.setBounds(LABEL_LEFT, LABEL_TOP + LABEL_HEIGHT_INC + BUTTON_HEIGHT_INC, 200, 30);
        lossMsg.setFont(new Font("SanSerif", Font.BOLD, 25));
        lossMsg.setForeground(Color.RED);
        lossMsg.setText("Sorry, you lose.");
        panel.add(lossMsg);
        lossMsg.setVisible(false);

        totalsMsg = new JLabel("You've won " + totalWins
            + " out of " + totalGames + " games.");
        totalsMsg.setBounds(LABEL_LEFT, LABEL_TOP + 2 * LABEL_HEIGHT_INC + BUTTON_HEIGHT_INC,
                                  250, 30);
        panel.add(totalsMsg);

        if (!board.anotherPlayIsPossible()) {
            signalLoss();
        }

        pack();
        getContentPane().add(panel);
        getRootPane().setDefaultButton(replaceButton);
        panel.setVisible(true);
    }

    /**
     * Deal with the user clicking on something other than a button or a card.
     */
    private void signalError() {
        Toolkit t = panel.getToolkit();
        t.beep();
    }

    /**
     * Returns the image that corresponds to the input card.
     * Image names have the format "[Rank][Suit].GIF" or "[Rank][Suit]S.GIF",
     * for example "aceclubs.GIF" or "8heartsS.GIF". The "S" indicates that
     * the card is selected.
     *
     * @param c Card to get the image for
     * @param isSelected flag that indicates if the card is selected
     * @return String representation of the image
     */
    private String imageFileName(Card c, int isSelected) {
        String str = "cards/";
        if (c == null) {
            return "cards/back1.GIF";
        }
        str += c.rank() + c.suit();
        if (isSelected != 0) {
            str += "S";
        }
        str += ".GIF";
        return str;
    }

    /**
     * Respond to a button click (on either the "Replace" button
     * or the "Restart" button).
     * @param e the button click action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(replaceButton)) {
            // Gather all the selected cards.
            List<Point> selection = new ArrayList<Point>();
            for (int r = 0; r < board.tabRowSize(); r++) {
                for (int c = 0; c < board.tabColSize(); c++)
                if (selections[r][c] == 1) {
                    selection.add(new Point(r,c));
                }
            }
            for (int r = 0; r < board.tabRowSize(); r++) {
                for (int c = 0; c < board.tabColSize(); c++)
                if (selections[r][c] == 2) {
                    selection.add(new Point(r,c));
                }
            }
            // Make sure that the selected cards represent a legal replacement.
            if (!board.isLegal(selection)) {
                signalError();
                return;
            }
            for (int r = 0; r < board.tabRowSize(); r++) {
                for (int c = 0; c < board.tabColSize(); c++) {
                    selections[r][c] = 0;
                }
            }
            // Do the replace.
            if (I_AM_DEBUGGING) {
                //System.out.println(board);
                System.out.println(selection);
            }
            board.moveSelectedTabCard(selection);
            if (I_AM_DEBUGGING) {
                //System.out.println(board);
            }
            if (board.isEmpty()) {
                signalWin();
            } else if (!board.anotherPlayIsPossible()) {
                signalLoss();
            }
            repaint();
        } else if (e.getSource().equals(restartButton)) {
            board.newGame();
            getRootPane().setDefaultButton(replaceButton);
            winMsg.setVisible(false);
            lossMsg.setVisible(false);
            if (!board.anotherPlayIsPossible()) {
                signalLoss();
                lossMsg.setVisible(true);
            }
            for (int r = 0; r < selections.length; r++) {
                for (int c = 0; c < selections[r].length; c++) {
                    selections[r][c] = 0;
                }
            }
            for (int fPileIndex = 0; fPileIndex < 4; fPileIndex++) {
                board.fPileReset(fPileIndex);
            }
            board.deckReset();
            repaint();
        } else if (e.getSource().equals(dealButton)) {
            board.deal();
            repaint();
        } else {
            signalError();
            return;
        }
    }

    /**
     * Display a win.
     */
    private void signalWin() {
        getRootPane().setDefaultButton(restartButton);
        winMsg.setVisible(true);
        totalWins++;
        totalGames++;
    }

    /**
     * Display a loss.
     */
    private void signalLoss() {
        getRootPane().setDefaultButton(restartButton);
        lossMsg.setVisible(true);
        totalGames++;
    }

    /**
     * Receives and handles mouse clicks.  Other mouse events are ignored.
     */
    private class MyMouseListener implements MouseListener {

        /**
         * Handle a mouse click on a card by toggling its "selected" property.
         * Each card is represented as a label.
         * @param e the mouse event.
         */
        public void mouseClicked(MouseEvent e) {
            for (int r = 0; r < board.tabRowSize(); r++) {
                for (int c = 0; c < board.tabColSize(); c++) {
                    if (board.tabCardAt(r,c) != null) {
                        if (e.getSource().equals(dDisplayCards)) {
                                if (selections[r][c] != 0
                                && has1(selections)
                                && !has2(selections)
                                && board.dealMoveIsPossible()) {
                                    board.moveDeckCardToTableau(r-1,c);
                                }
                            }
                        for (int fPileIndex = 0; fPileIndex < 4; fPileIndex++) {
                            if (e.getSource().equals(fDisplayCards[fPileIndex])) {
                                if (selections[r][c] != 0
                                && has1(selections)
                                && !has2(selections)
                                && board.canMoveToFoundation(r,c,fPileIndex))
                                    board.moveCardToFoundation(r, c, fPileIndex);
                            }
                        }
                        if (e.getSource().equals(tDisplayCards[r][c])) {
                            boolean selectionHas2 = has2(selections);
                            boolean selectionHas1 = has1(selections);
                            if(selections[r][c] == 0) {
                                if (!selectionHas1) {
                                    selections[r][c] = 1;
                                } else if (!selectionHas2 && selectionHas1) {
                                    setSmaller1Greater2(selections, r, c);
                                }
                            } else if(selections[r][c] == 1){
                                if (!selectionHas2)
                                selections[r][c] = 0;
                                if (selectionHas2) {
                                    selections[r][c] = 0;
                                    if2Make1(selections);
                                }
                            } else if(selections[r][c] == 2) {
                                selections[r][c] = 0;
                            }
                            repaint();
                            if (I_AM_DEBUGGING) {
                                System.out.println(board.tabCardAt(r,c));
                            }
                            return;
                        }
                    }
                }
            }
            signalError();
        }
        
        /**
         * Method to help organize mouseClicked()
         */
        private void if2Make1(int[][] selections) {
            for (int r1 = 0; r1 < board.tabRowSize(); r1++) {
                for (int c1 = 0; c1 < board.tabColSize(); c1++) {
                    if (selections[r1][c1] == 2)
                        selections[r1][c1] = 1;
                }
            }
        }
        
        /**
         * Method to help organize mouseClicked()
         */
        private void setSmaller1Greater2(int[][] selections, int r, int c) {
            for (int r1 = 0; r1 < board.tabRowSize(); r1++) {
                for (int c1 = 0; c1 < board.tabColSize(); c1++) {
                    if(selections[r1][c1] == 1) {
                        if(board.tabCardAt(r1,c1).pointValue()
                        > board.tabCardAt(r,c).pointValue()) {
                            selections[r][c] = 1;
                            selections[r1][c1] = 2;
                            return;
                        } else if(board.tabCardAt(r,c).pointValue()
                        > board.tabCardAt(r1,c1).pointValue()) {
                            selections[r][c] = 2;
                            return;
                        }
                    }
                }
            }
            selections[r][c] = 1;
        }
        
        /**
         * Method to help organize mouseClicked()
         */
        private boolean has2(int[][] selections) {
            for (int r1 = 0; r1 < board.tabRowSize(); r1++) {
                for (int c1 = 0; c1 < board.tabColSize(); c1++) {
                    if(selections[r1][c1] == 2) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        /**
         * Method to help organize mouseClicked()
         */
        private boolean has1(int[][] selections) {
            for (int r1 = 0; r1 < board.tabRowSize(); r1++) {
                for (int c1 = 0; c1 < board.tabColSize(); c1++) {
                    if(selections[r1][c1] == 1) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Ignore a mouse exited event.
         * @param e the mouse event.
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * Ignore a mouse released event.
         * @param e the mouse event.
         */
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * Ignore a mouse entered event.
         * @param e the mouse event.
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * Ignore a mouse pressed event.
         * @param e the mouse event.
         */
        public void mousePressed(MouseEvent e) {
        }
    }
}
