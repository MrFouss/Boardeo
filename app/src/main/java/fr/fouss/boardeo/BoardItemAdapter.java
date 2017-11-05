package fr.fouss.boardeo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Adapter that handle the translation from brut data to displayable view
 * Automatize the updates between data and view
 */
public class BoardItemAdapter extends RecyclerView.Adapter<BoardItemAdapter.BoardItemViewHolder> {

    // TODO retrieve firebase data
    /**
     * List of board item data
     */
    private BoardItemData[] itemData = {
            new BoardItemData("name1", "author1", "description1"),
            new BoardItemData("name2", "author2", "description2"),
            new BoardItemData("name3", "author3", "description3")
    };

    /**
     * Are items selectable (selection mode)
     */
    private boolean selectionMode = false;

    /**
     * Encapsulate the data of a board item
     */
    private static class BoardItemData {
        private String boardName;
        private String boardAuthor;
        private String boardShortDescription;

        public BoardItemData(String boardName, String boardAuthor, String boardShortDescription) {
            this.boardName = boardName;
            this.boardAuthor = boardAuthor;
            this.boardShortDescription = boardShortDescription;
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
    }

    /**
     * Encapsulate the useful views within a board item
     * This allows for quick access when a change is necessary
     */
    public class BoardItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener
    {

        private TextView boardNameText;
        private TextView boardAuthorText;
        private TextView boardShortDescText;
        private CheckBox checkBox;

        public BoardItemViewHolder(View itemView) {
            super(itemView);
            boardNameText = itemView.findViewById(R.id.boardNameText);
            boardAuthorText = itemView.findViewById(R.id.boardAuthorText);
            boardShortDescText = itemView.findViewById(R.id.boardShortDescText);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void set(BoardItemData data) {
            this.boardNameText.setText(data.getBoardName());
            this.boardAuthorText.setText(data.getBoardAuthor());
            this.boardShortDescText.setText(data.getBoardShortDescription());

            if (isSelectionMode()) {
                // show and enable checkbox if in selection mode
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setEnabled(true);
            } else {
                // hide, disable and un-check if not in selection mode
                checkBox.setVisibility(View.INVISIBLE);
                checkBox.setEnabled(false);
                checkBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View v) {
            // check (select) item if in selection mode
            if (isSelectionMode()) {
                checkBox.toggle();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            // toggle selection mode
            setSelectionMode(!isSelectionMode());
            notifyDataSetChanged();
            return true;
        }
    }

    /**
     * Called when the instantiation of a new board item is necessary
     * @param parent parent view (the recycler view)
     * @param viewType the type of the view to be created (not used, default to 0)
     * @return the view holder encapsulation the newly created view
     */
    @Override
    public BoardItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate (parse and create) view from layout file
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_item, parent, false);
        return new BoardItemViewHolder(newView);
    }

    /**
     * Called when the data displayed by holder needs to be changed
     * This is typically called when a view just has been created or is recycled
     * @param holder view holder encapsulation the view
     * @param position the position of the data to be displayed in the data list
     */
    @Override
    public void onBindViewHolder(BoardItemViewHolder holder, int position) {
        holder.set(itemData[position]);
    }

    @Override
    public int getItemCount() {
        return itemData.length;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }
}
