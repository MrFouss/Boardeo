package fr.fouss.boardeo.listing;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fr.fouss.boardeo.R;
import fr.fouss.boardeo.data.Post;
import fr.fouss.boardeo.utils.UserUtils;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    ///// FIELDS /////

    /**
     * Parent activity
     */
    private Activity activity;

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    /**
     * User utility class instance
     */
    private UserUtils userUtils;

    /**
     * Map of posts
     * A comparator can be specified to order them
     */
    private final TreeMap<String, Post> posts = new TreeMap<>();

    private ChildEventListener postListListener = null;
    private Map<String, ValueEventListener> postListenerMap = new HashMap<>();

    ///// CONSTRUCTOR /////

    public PostAdapter(Activity activity) {
        super();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUtils = new UserUtils(activity);
        this.activity = activity;
    }

    ///// NECESSARY IMPLEMENTATIONS /////

    /**
     * Called when the instantiation of a new view holder is necessary
     * @param parent parent view (the recycler view)
     * @param viewType the type of the view to be created (not used, default to 0)
     * @return the view holder encapsulating the newly created view
     */
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate (parse and create) view from layout file
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_item, parent, false);
        // TODO create post_list_item
        return new PostViewHolder(newView);
    }

    /**
     * Called when the data displayed by holder needs to be changed
     * This is typically called when a view just has been created or is recycled
     * @param holder view holder encapsulation the view
     * @param position the position of the data to be displayed in the data list
     */
    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Map.Entry<String, Post> entry = null;
        Iterator<Map.Entry<String, Post>> it = posts.entrySet().iterator();
        for (int i = 0; i <= position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            holder.set(entry.getKey(), entry.getValue());
        }
    }

    ///// DATA MANAGEMENT /////

    public void initPostListListener(String boardKey) {
        if (postListListener == null) {
            postListListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    setPost(key);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    removePost(key);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            };

            mDatabase.child("boards").child(boardKey).child("posts")
                    .addChildEventListener(postListListener);
        }
    }

    private void removePostListListener(String boardKey) {
        if (postListListener != null) {
            mDatabase.child("boards").child(boardKey).child("posts")
                    .removeEventListener(postListListener);
            postListListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPost(String postKey) {
        if (!postListenerMap.containsKey(postKey)) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String postKey = dataSnapshot.getKey();
                    Post post = dataSnapshot.getValue(Post.class);
                    posts.put(postKey, post);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            };

            postListenerMap.put(postKey, listener);
            mDatabase.child("posts").child(postKey).addValueEventListener(listener);
        }
    }

    private void removePostListener(String postKey) {
        if (postListenerMap.containsKey(postKey)) {
            mDatabase.child("posts").child(postKey)
                    .removeEventListener(postListenerMap.get(postKey));
            postListenerMap.remove(postKey);
        }
    }

    public void removePost(String postKey) {
        removePostListener(postKey);
        posts.remove(postKey);
        notifyDataSetChanged();
    }

    ///// VIEW HOLDER /////

    /**
     * Implement a basic view holder that requires a view with at least a checkbox to manage selection
     */
    public class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView titleLabel;
        private TextView contentLabel;
        private TextView dateLabel;
        private TextView authorLabel;
        private String key;

        public PostViewHolder(View itemView) {
            super(itemView);

            this.titleLabel = itemView.findViewById(R.id.post_title_label);
            this.contentLabel = itemView.findViewById(R.id.post_content_label);
            this.dateLabel = itemView.findViewById(R.id.post_date_label);
            this.authorLabel = itemView.findViewById(R.id.post_author_label);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onPostClicked(this.key);
                }
            });
        }

        public void set(String key, Post post) {
            this.key = key;
            this.titleLabel.setText(post.getTitle());
            this.contentLabel.setText(post.getContent());
            this.dateLabel.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(post.getTimestamp())));

            mDatabase.child("users").child(post.getAuthorUid()).child("username")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            authorLabel.setText(activity.getResources().getString(R.string.by_author, dataSnapshot.getValue(String.class)));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
        }
    }

    ///// LISTENERS /////

    private PostClickListener clickListener = null;

    public void setPostClickListener(PostClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface PostClickListener {
        void onPostClicked(String key);
    }
}
