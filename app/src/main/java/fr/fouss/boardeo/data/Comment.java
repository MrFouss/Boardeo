package fr.fouss.boardeo.data;

public class Comment {

    private String content;
    private Long timestamp;
    private String authorUid;
    private String postKey;

    public Comment() {}

    public Comment(String content,
            Long timestamp,
            String authorUid,
            String postKey) {

        this.content = content;
        this.timestamp = timestamp;
        this.authorUid = authorUid;
        this.postKey = postKey;
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

    public String getPostKey() {
        return postKey;
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

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }
}
