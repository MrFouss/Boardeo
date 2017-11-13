package fr.fouss.boardeo;

/**
 * Encapsulate the data of a board item
 */
public class BoardData {
    public final static String BOARD_NAME_FIELD = "boardName";
    public final static String BOARD_AUTHOR_FIELD = "boardAuthor";
    public final static String BOARD_SHORT_DESCRIPTION_FIELD = "boardShortDescription";
    public final static String BOARD_FULL_DESCRIPTION_FIELD = "boardFullDescription";

    private String boardName;
    private String boardAuthor;
    private String boardShortDescription;
    private boolean subscribed;

    public BoardData(String boardName, String boardAuthor, String boardShortDescription, boolean subscribed) {
        this.boardName = boardName;
        this.boardAuthor = boardAuthor;
        this.boardShortDescription = boardShortDescription;
        this.subscribed = subscribed;
    }

    public String getBoardName() {
        return boardName;
    }

    public String getBoardAuthor() {
        return boardAuthor;
    }

    public String getBoardShortDescription() {
        return boardShortDescription;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
