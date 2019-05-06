import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

/**
 * The ElevensBoard class represents the board in a game of Elevens.
 */
public class SolitaireBoard extends Board {

    /**
     * The ranks of the cards for this game to be sent to the deck.
     */
    private static final String[] RANKS =
        {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

    /**
     * The suits of the cards for this game to be sent to the deck.
     */
    private static final String[] SUITS =
        {"spades", "hearts", "diamonds", "clubs"};

    /**
     * The values of the cards for this game to be sent to the deck.
     */
    private static final int[] POINT_VALUES =
        {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

    /**
     * Flag used to control debugging print statements.
     */
    private static final boolean I_AM_DEBUGGING = true;


    /**
     * Creates a new <code>SolitaireBoard</code> instance.
     */
     public SolitaireBoard() {
        super(RANKS, SUITS, POINT_VALUES);
     }

    /**
     * Determines if the selected cards form a valid group for removal.
     * In Elevens, the legal groups are (1) a pair of non-face cards
     * whose values add to 11, and (2) a group of three cards consisting of
     * a jack, a queen, and a king in some order.
     * @param selectedCards the list of the indices of the selected cards.
     * @return true if the selected cards form a valid group for removal;
     *         false otherwise.
     */
    @Override
    public boolean isLegal(List<Point> selectedCards) {
        if (selectedCards.size() == 2) {
            return findStackingMove(selectedCards).size() > 0;
        } else {
            return false;
        }
    }

    /**
     * Determine if there are any legal plays left on the board.
     * In Elevens, there is a legal play if the board contains
     * (1) a pair of non-face cards whose values add to 11, or (2) a group
     * of three cards consisting of a jack, a queen, and a king in some order.
     * @return true if there is a legal play left on the board;
     *         false otherwise.
     */
    @Override
    public boolean anotherPlayIsPossible() {
        List<Point> cIndexes = cardIndexes();
        if (I_AM_DEBUGGING) {
            System.out.println("intentCoords: " + findStackingMove(cIndexes));
            //System.out.println("cIndexes: " + cIndexes);
        }
        return findStackingMove(cIndexes).size() > 0
        || findFoundationMove(cIndexes).getX() != -1;
             //|| findJQK(cIndexes).size() > 0;
    }

    /**
     * Look for an 11-pair in the selected cards.
     * @param selectedCards selects a subset of this board.  It is list
     *                      of indexes into this board that are searched
     *                      to find an 11-pair.
     * @return a list of the indexes of an 11-pair, if an 11-pair was found;
     *         an empty list, if an 11-pair was not found.
     */
    public Point findFoundationMove(List<Point> selectedCards) {
        for (int tCard = 0; tCard < selectedCards.size(); tCard++) {
            int r = (int) selectedCards.get(tCard).getX();
            int c = (int) selectedCards.get(tCard).getY();
            for (int fPile = 0; fPile < 4; fPile++) {
                if (tabCardAt(r,c).pointValue() == 1
                && fCardAt(fPile) == null) {
                    return new Point(r,c);
                } else if (fCardAt(fPile) != null
                && sameSuit(r,c,fPile)
                && oneLess(r,c,fPile)) {
                    return new Point(r,c);
                }
            }
        }
        return new Point(-1,-1);
    }
    
    public boolean canMoveToFoundation(int r, int c, int fPile) {
        if (tabCardAt(r,c).pointValue() == 1
        && fCardAt(fPile) == null) {
            return true;
        } else if (fCardAt(fPile) != null
        && sameSuit(r,c,fPile)
        && oneLess(r,c,fPile)) {
            return true;
        }
        return false;
    }
    
    public List<Point> findStackingMove(List<Point> selectedCards) {
        if (I_AM_DEBUGGING)
            //System.out.println("selectedCards" + selectedCards);
        for (int sp1 = 0; sp1 < selectedCards.size(); sp1++) {
            int r1 = (int) selectedCards.get(sp1).getX();
            int c1 = (int) selectedCards.get(sp1).getY();
            for (int sp2 = sp1 + 1; sp2 < selectedCards.size(); sp2++) {
                int r2 = (int) selectedCards.get(sp2).getX();
                int c2 = (int) selectedCards.get(sp2).getY();
                if (canMove(r1,c1,r2,c2).size() > 0) {
                    if (I_AM_DEBUGGING) {
                        System.out.println("Card at (r1,c1): " + tabCardAt(r1,c1) + " at " + r1 + " " + c1);
                        System.out.println("Card at (r2,c2): " + tabCardAt(r2,c2) + " at " + r2 + " " + c2);
                    }
                    return canMove(r1,c1,r2,c2);
                }
            }
        }
        return new ArrayList<Point>();
    }
    
    private List<Point> canMove(int r1, int c1, int r2, int c2) {
        List<Point> intentCoords = new ArrayList<Point>();
        if (I_AM_DEBUGGING)
            //System.out.println("Before opsuit/diffRow check: r1, c1, r2, c2: " + r1 + " " + c1 + " " + r2 + " " + c2);
        if (opSuit(r1,c1,r2,c2) && (c1 != c2)) {
            if (I_AM_DEBUGGING)
                //System.out.println("After opsuit/diffRow check: r1, c1, r2, c2: " + r1 + " " + c1 + " " + r2 + " " + c2);
            if ((tabCardAt(r1, c1).pointValue() - tabCardAt(r2, c2).pointValue() == 1)
            && tabCardAt(r1 + 1, c1) == null) {
                intentCoords.add(new Point(r2,c2));
                //^coords of "move"; aka smaller
                intentCoords.add(new Point(r1+1,c1));
                //^coords of "target"; aka one below larger
            } else if ((tabCardAt(r2, c2).pointValue() - tabCardAt(r1, c1).pointValue() == 1)
            && tabCardAt(r2 + 1, c2) == null) {
                intentCoords.add(new Point(r1,c1));
                //^coords of "move"; aka smaller
                intentCoords.add(new Point(r2+1,c2));
                //^coords of "target"; aka one below larger
            }
        }
        return intentCoords;
    }
    
    private boolean opSuit(int r1, int c1, int r2, int c2) {
        if ((tabCardAt(r1, c1).suit() == "hearts"
        || tabCardAt(r1, c1).suit() == "diamonds")
        && (tabCardAt(r2, c2).suit() == "spades"
        || tabCardAt(r2, c2).suit() == "clubs")) {
            return true;
        } else if ((tabCardAt(r2, c2).suit() == "hearts"
        || tabCardAt(r2, c2).suit() == "diamonds")
        && (tabCardAt(r1, c1).suit() == "spades"
        || tabCardAt(r1, c1).suit() == "clubs")) {
            return true;
        }
        return false;
    }
    
    private boolean sameSuit(int r, int c, int fPile) {
        if (tabCardAt(r,c).suit() == fCardAt(fPile).suit()) {
            return true;
        }
        return false;
    }
    
    private boolean oneLess(int r, int c, int fPile) {
        if (tabCardAt(r,c).pointValue() - fCardAt(fPile).pointValue() == 1) {
            return true;
        }
        return false;
    }
    
    /*
    private List<Point> findPairSum11(List<Point> selectedCards) {
        List<Point> foundCoords = new ArrayList<Point>();
        for (int sk1 = 0; sk1 < selectedCards.size(); sk1++) {
            int x1 = (int) selectedCards.get(sk1).getX();
            int y1 = (int) selectedCards.get(sk1).getY();
            for (int sk2 = sk1 + 1; sk2 < selectedCards.size(); sk2++) {
                int x2 = (int) selectedCards.get(sk2).getX();
                int y2 = (int) selectedCards.get(sk2).getY();
                if (tabCardAt(x1, y1).pointValue() + tabCardAt(x2, y2).pointValue() == 11) {
                    foundCoords.add(new Point(x1, y1));
                    foundCoords.add(new Point(x2, y2));
                    return foundCoords;
                }
            }
        }
        return foundCoords;
    }
    */
    
    /**
     * Look for a JQK in the selected cards.
     * @param selectedCards selects a subset of this board.  It is list
     *                      of indexes into this board that are searched
     *                      to find a JQK group.
     * @return a list of the indexes of a JQK, if a JQK was found;
     *         an empty list, if a JQK was not found.
     */
    private List<Point> findJQK(List<Point> selectedCards) {
        List<Point> foundIndexes = new ArrayList<Point>();
        Point jackIndex = new Point(-1, -1);
        Point queenIndex = new Point(-1, -1);
        Point kingIndex = new Point(-1, -1);
        for (Point coordObj : selectedCards) {
            int r = (int) coordObj.getX();
            int c = (int) coordObj.getY();
            if (tabCardAt(r,c).rank().equals("jack")) {
                jackIndex.setLocation(r,c);
            } else if (tabCardAt(r,c).rank().equals("queen")) {
                queenIndex.setLocation(r,c);
            } else if (tabCardAt(r,c).rank().equals("king")) {
                kingIndex.setLocation(r,c);
            }
        }
        if (jackIndex.getX() != -1 && queenIndex.getX() != -1 && kingIndex.getX() != -1) {
            foundIndexes.add(jackIndex);
            foundIndexes.add(queenIndex);
            foundIndexes.add(kingIndex);
        }
        return foundIndexes;
    }

    /**
     * Looks for a legal play on the board.  If one is found, it plays it.
     * @return true if a legal play was found (and made); false othewise.
     */
    public boolean playIfPossible() {
        return StackIfPossible();
        //|| playJQKIfPossible();
    }

    /**
     * Looks for a pair of non-face cards whose values sum to 11.
     * If found, replace them with the next two cards in the deck.
     * The simulation of this game uses this method.
     * @return true if an 11-pair play was found (and made); false othewise.
     */
    private boolean StackIfPossible() {
        List<Point> foundIndexes = cardIndexes();
        List<Point> cardsToReplace = findStackingMove(foundIndexes);
        if (cardsToReplace.size() > 0) {
            moveSelectedTabCard(cardsToReplace);
            if (I_AM_DEBUGGING) {
                System.out.println("Card Moved.\n");
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Looks for a group of three face cards JQK.
     * If found, replace them with the next three cards in the deck.
     * The simulation of this game uses this method.
     * @return true if a JQK play was found (and made); false othewise.
     */
    private boolean playJQKIfPossible() {
        List<Point> foundIndexes = cardIndexes();
        List<Point> cardsToReplace = findJQK(foundIndexes);
        if (cardsToReplace.size() > 0) {
            moveSelectedTabCard(cardsToReplace);
            if (I_AM_DEBUGGING) {
                System.out.println("JQK-Triplet removed.\n");
            }
            return true;
        } else {
            return false;
        }
    }
}
