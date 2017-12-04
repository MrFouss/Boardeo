package fr.fouss.boardeo.data;

public class Post {
    public final static String KEY_FIELD = "POST_KEY";

    private String title;
    private String content;
    private Long timestamp;
    private String authorUid;
    private String boardKey;

    public Post() {}

    public Post(String title,
                String content,
                Long timestamp,
                String authorUid,
                String boardKey) {

        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.authorUid = authorUid;
        this.boardKey = boardKey;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getBoardKey() {
        return boardKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public void setBoardKey(String boardKey) {
        this.boardKey = boardKey;
    }
}
