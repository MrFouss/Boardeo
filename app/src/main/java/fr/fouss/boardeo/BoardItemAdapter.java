package fr.fouss.boardeo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Adapter that handle the translation from brut data to displayable view
 * Automatize the updates between data and view
 */
public class BoardItemAdapter extends RecyclerView.Adapter<BoardItemAdapter.BoardItemViewHolder> {

    /**
     * List of board item data
     */
    private BoardItemData[] itemData = {
            new BoardItemData("name1", "author1", "description1", true),
            new BoardItemData("name2", "author2", "description2", false),
            new BoardItemData("name3", "author3", "description3", true)
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
        private boolean subscribed;

        public BoardItemData(String boardName, String boardAuthor, String boardShortDescription, boolean subscribed) {
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

    /**
     * Encapsulate the useful views within a board item
     * This allows for quick access when a change is necessary
     */
    public class BoardItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;
        private TextView textAuthor;
        private TextView textShortDescription;
        private CheckBox checkBoxSelection;
        private CheckBox checkBoxSubscription;
        private TableRow tableRaw;

        public BoardItemViewHolder(View itemView) {
            super(itemView);
            // store reference to view components
            textName = itemView.findViewById(R.id.boardNameText);
            textAuthor = itemView.findViewById(R.id.boardAuthorText);
            textShortDescription = itemView.findViewById(R.id.boardShortDescText);
            checkBoxSelection = itemView.findViewById(R.id.checkBoxSelect);
            checkBoxSubscription = itemView.findViewById(R.id.checkBoxSubscription);
            tableRaw =itemView.findViewById(R.id.tableRaw);

            // setup listeners
            checkBoxSubscription.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> onSubscriptionCheckedChanged(buttonView, isChecked)
            );
            itemView.setOnClickListener(v -> onItemClick(v));
            itemView.setOnLongClickListener(v -> onItemLongClick(v));
        }

        public void set(BoardItemData data) {
            // texts
            this.textName.setText(data.getBoardName());
            this.textAuthor.setText(data.getBoardAuthor());
            this.textShortDescription.setText(data.getBoardShortDescription());

            // subscription checkbox
            checkBoxSubscription.setChecked(data.isSubscribed());

            // selection mode
            if (isSelectionMode()) {
                // add selection checkbox and remove subscription checkbox
                if (tableRaw == checkBoxSubscription.getParent()) {
                    tableRaw.removeView(checkBoxSubscription);
                }
                if (tableRaw != checkBoxSelection.getParent()) {
                    tableRaw.addView(checkBoxSelection, 0);
                }
            } else {
                // add subscription checkbox and remove selection checkbox
                // set un-check selection checkbox
                if (tableRaw != checkBoxSubscription.getParent()) {
                    tableRaw.addView(checkBoxSubscription, 0);
                }
                if (tableRaw == checkBoxSelection.getParent()) {
                    checkBoxSelection.setChecked(false);
                    tableRaw.removeView(checkBoxSelection);
                }
            }
        }

        public void onItemClick(View v) {
            // check (select) item if in selection mode
            if (isSelectionMode()) {
                checkBoxSelection.toggle();
            }
        }

        public boolean onItemLongClick(View v) {
            // toggle selection mode
            setSelectionMode(!isSelectionMode());
            notifyDataSetChanged();
            return true;
        }

        public void onSubscriptionCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // update data
            itemData[this.getAdapterPosition()].setSubscribed(isChecked);
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
