import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a Board that can be used in a collection
 * of solitaire games similar to Elevens.  The variants differ in
 * card removal and the board size.
 */
public abstract class Board {

    /**
     * The cards on this board.
     */
    private Card[][] tableau;
    
    private ArrayList<ArrayList<Card>> foundation;
    
    private ArrayList<Card> deckPile;

    /**
     * The deck of cards being used to play the current game.
     */
    private Deck deck;

    /**
     * Flag used to control debugging print statements.
     */
    private static final boolean I_AM_DEBUGGING = true;

    /**
     * Creates a new <code>Board</code> instance.
     * @param size the number of cards in the board
     * @param ranks the names of the card ranks needed to create the deck
     * @param suits the names of the card suits needed to create the deck
     * @param pointValues the integer values of the cards needed to create
     *                    the deck
     */
    public Board(String[] ranks, String[] suits, int[] pointValues) {
        int columns = 7;
        int rows = 12;
        tableau = new Card[rows][columns];
        foundation = new ArrayList<ArrayList<Card>>();
        deckPile = new ArrayList<Card>();
        for (int fPile = 0; fPile < 4; fPile++) {
            foundation.add(new ArrayList<Card>());
        }
        deck = new Deck(ranks, suits, pointValues);
        if (I_AM_DEBUGGING) {
            System.out.println(deck);
            System.out.println("----------");
        }
        dealMyCards();
    }

    /**
     * Start a new game by shuffling the deck and
     * dealing some cards to this board.
     */
    public void newGame() {
        deck.shuffle();
        int numCards = tableau.length;
        dealMyCards();
    }

    /**
     * Accesses the size of the board.
     * Note that this is not the number of cards it contains,
     * which will be smaller near the end of a winning game.
     * @return the size of the board
     */
    public int tabRowSize() {
        return tableau.length;
    }
    
    public int tabColSize() {
        return tableau[0].length;
    }
    
    public int fPileSize(int pileIndex) {
        return foundation.get(pileIndex).size();
    }

    /**
     * Determines if the board is empty (has no cards).
     * @return true if this board is empty; false otherwise.
     */
    public boolean isEmpty() {
        for (int r = 0; r < tableau.length; r++) {
            for (int c = 0; c < tableau[0].length; c++) {
                if (tableau[r][c] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deal a card to the kth position in this board.
     * If the deck is empty, the kth card is set to null.
     * @param k the index of the card to be dealt.
     */
    public void deal(int r, int c, boolean flag) {
        if (r == 11 || flag) {
            tableau[r][c] = deck.deal();
        } else {
            tableau[r][c] = null;
        }
    }
    
    public void deal() {
        deckPile.add(deck.deal());
    }
    
    public void tabSetCard(Card move, int r, int c) {
        tableau[r][c] = move;
    }
    
    public void deckReset() {
        deckPile.clear();
    }
    
    public void fAddCard(Card move, int fPileIndex) {
        foundation.get(fPileIndex).add(move);
    }
    
    public void fPileReset(int fPileIndex) {
        foundation.get(fPileIndex).clear();
    }

    /**
     * Accesses the deck's size.
     * @return the number of undealt cards left in the deck.
     */
    public int deckSize() {
        return deck.size();
    }
    
    /**
     * Accesses a card on the board.
     * @return the card at position k on the board.
     * @param k is the board position of the card to return.
     */
    public Card tabCardAt(int r, int c) {
        try {
            return tableau[r][c];
        }
        catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public Card fCardAt(int fPileIndex) {
        try {
            return foundation.get(fPileIndex).get(fPileSize(fPileIndex)-1);
        }
        catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public Card dCardAt() {
        try {
            return deckPile.get(deckPile.size()-1);
        }
        catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public boolean tabHasCard(int r, int c) {
        if (tabCardAt(r,c) != null)
            return true;
        return false;
    }

    /**
     * Replaces selected cards on the board by dealing new cards.
     * @param selectedCards is a list of the indices of the
     *        cards to be replaced.
     */
    public void moveSelectedTabCard(List<Point> selectedCards) {
        int rMove = (int) selectedCards.get(0).getX();
        int cMove = (int) selectedCards.get(0).getY();
        int rTarget = (int) selectedCards.get(1).getX();
        int cTarget = (int) selectedCards.get(1).getY();
        tableau[rTarget - 1][cTarget] = tabCardAt(rMove, cMove);
        deal(rMove, cMove, false);
        if(I_AM_DEBUGGING)
            //System.out.println(tabCardAt(0,0) + "\n" + tabCardAt(1,0));
        for (int r = 1; r <= rMove; r++) {
            if (tabHasCard(rMove - r, cMove)) {
                tableau[rTarget - 1 - r][cTarget] = tabCardAt(rMove - r, cMove);
                deal(rMove - r, cMove, false);
            }
        }
    }
    
    public void moveCardToFoundation(int r, int c, int fPile) {
        Card move = tabCardAt(r,c);
        foundation.get(fPile).add(move);
        deal(r,c, false);
    }
    
    public void moveDeckCardToTableau(int r, int c) {
        tabSetCard(dCardAt(), r, c);
        deal();
    }

    /**
     * Gets the indexes of the actual (non-null) cards on the board.
     *
     * @return a List that contains the locations (indexes)
     *         of the non-null entries on the board.
     */
    public List<Point> cardIndexes() {
        List<Point> selected = new ArrayList<Point>();
        for (int r = 0; r < tableau.length; r++) {
            for (int c = 0; c < tableau[0].length; c++){
                if (tableau[r][c] != null) {
                    selected.add(new Point(r,c));
                }
            }
        }
        return selected;
    }

    /**
     * Generates and returns a string representation of this board.
     * @return the string version of this board.
     */
    public String toString() {
        String s = "";
        for (int r = 0; r < tableau.length; r++) {
            for (int c = 0; c < tableau[0].length; c++) {
                int k = 7*r+c;
                s += k + ": " + tableau[r][c] + "\n";
            }
        }
        return s;
    }

    /**
     * Determine whether or not the game has been won,
     * i.e. neither the board nor the deck has any more cards.
     * @return true when the current game has been won;
     *         false otherwise.
     */
    public boolean gameIsWon() {
        if (deck.isEmpty()) {
            for (Card[] c : tableau) {
                if (c[1] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method to be completed by the concrete class that determines
     * if the selected cards form a valid group for removal.
     * @param selectedCards the list of the indices of the selected cards.
     * @return true if the selected cards form a valid group for removal;
     *         false otherwise.
     */
    public abstract boolean isLegal(List<Point> selectedCards);

    /**
     * Method to be completed by the concrete class that determines
     * if there are any legal plays left on the board.
     * @return true if there is a legal play left on the board;
     *         false otherwise.
     */
    public abstract boolean anotherPlayIsPossible();
    
    public abstract boolean canMoveToFoundation(int r, int c, int fPile);
    
    public abstract boolean canDeal();
    
    public abstract boolean dealMoveIsPossible();

    /**
     * Deal cards to this board to start the game.
     */
    private void dealMyCards() {
        for (int r = 0; r < tableau.length; r++) {
            for (int c = 0; c < tableau[0].length; c++) {
                if (r == 11)
                    tableau[r][c] = deck.deal();
                else
                    tableau[r][c] = null;
            }
        }
    }
}
