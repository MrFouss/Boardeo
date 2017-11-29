package fr.fouss.boardeo.data;

import android.content.Intent;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    public final static String CONTENT_FIELD = "commentContent";
    public final static String TIMESTAMP_FIELD = "commentTimestamp";
    public final static String AUTHOR_UID_FIELD = "commentAuthorUid";
    public final static String POST_KEY_FIELD = "commentPostKey";

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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(CONTENT_FIELD, getContent());
        result.put(TIMESTAMP_FIELD, getTimestamp());
        result.put(AUTHOR_UID_FIELD, getAuthorUid());
        result.put(POST_KEY_FIELD, getPostKey());

        return result;
    }

    /**
     * Set data from intent
     *
     * @param intent the intent containing the comment's info
     */
    public void setFromIntent(Intent intent) {
        this.content = intent.getStringExtra(CONTENT_FIELD);
        this.timestamp = intent.getLongExtra(TIMESTAMP_FIELD, 0);
        this.authorUid = intent.getStringExtra(AUTHOR_UID_FIELD);
        this.postKey = intent.getStringExtra(POST_KEY_FIELD);
    }

    /**
     * Translate to an intent
     *
     * @param intent the intent to fill
     * @return the same intent, but filled with the board's info
     */
    public Intent fillIntentExtras(Intent intent) {
        intent.putExtra(CONTENT_FIELD, getContent());
        intent.putExtra(TIMESTAMP_FIELD, getTimestamp());
        intent.putExtra(AUTHOR_UID_FIELD, getAuthorUid());
        intent.putExtra(POST_KEY_FIELD, getPostKey());

        return intent;
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
