package fr.fouss.boardeo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Adapter that handle the translation from brut data to displayable view
 * Automatize the updates between data and view
 */
public class BoardItemAdapter extends RecyclerView.Adapter<BoardItemAdapter.BoardItemViewHolder> {

    /**
     * List of board item data
     */
    private LinkedList<BoardData> itemData = new LinkedList<>();

    /**
     * list of selected data
     */
    private LinkedList<BoardData> selectedData = new LinkedList<>();

    /**
     * Are items selectable (selection mode)
     */
    private boolean selectionMode = false;

    /**
     * list of listener of the mode
     */
    private ModeListener modeListener = null;

    /**
     * Interface to implement to be notified when the mode changes
     */
    public interface ModeListener {
        void onSelectionModeToggle(boolean selectionMode);
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

        private BoardData data = null;

        public BoardItemViewHolder(View itemView) {
            super(itemView);

            // store reference to view components
            textName = itemView.findViewById(R.id.boardNameText);
            textAuthor = itemView.findViewById(R.id.boardAuthorText);
            textShortDescription = itemView.findViewById(R.id.boardShortDescText);
            checkBoxSelection = itemView.findViewById(R.id.checkBoxSelect);
            checkBoxSubscription = itemView.findViewById(R.id.checkBoxSubscription);

            // setup listeners
            checkBoxSelection.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> onSelectionCheckedChanged(buttonView, isChecked)
            );
            checkBoxSubscription.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> onSubscriptionCheckedChanged(buttonView, isChecked)
            );
            itemView.setOnClickListener(v -> onItemClick(v));
            itemView.setOnLongClickListener(v -> onItemLongClick(v));
        }

        public void set(BoardData data) {
            Log.d("Board", "set item");
            // texts
            this.textName.setText(data.getBoardName());
            this.textAuthor.setText(data.getBoardAuthor());
            this.textShortDescription.setText(data.getBoardShortDescription());

            // subscription checkbox
            checkBoxSubscription.setChecked(data.isSubscribed());

            // selection
            checkBoxSelection.setChecked(selectedData.contains(data));

            // selection mode
            if (isSelectionMode()) {
                // set selection checkbox visible
                checkBoxSelection.setVisibility(View.VISIBLE);
            } else {
                // set selection checkbox gone
                checkBoxSelection.setVisibility(View.GONE);
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
            setSelectionMode(true);
            return true;
        }

        public void onSubscriptionCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // update data subscription status
            itemData.get(this.getAdapterPosition()).setSubscribed(isChecked);
        }

        public void onSelectionCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // add/remove item to the selection list
            if (isChecked) {
                selectedData.add(itemData.get(this.getAdapterPosition()));
            } else {
                selectedData.remove(itemData.get(this.getAdapterPosition()));
            }
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
        holder.set(itemData.get(position));
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        // set mode
        this.selectionMode = selectionMode;

        // if setting non-select mode, remove selection
        if (!selectionMode) {
            selectedData.clear();
        }

        // notify data change
        notifyDataSetChanged();

        // notify mode listener
        if (modeListener != null) {
            modeListener.onSelectionModeToggle(selectionMode);
        }
    }

    public void setModeListener(ModeListener modeListener) {
        this.modeListener = modeListener;
    }

    /**
     * Set all selected data as subscription status
     * @param setSubscribed true if setting data as subscribed
     */
    public void setSubscriptionOnSelection(boolean setSubscribed) {
        for (BoardData data : selectedData) {
            data.setSubscribed(setSubscribed);
        }
        notifyDataSetChanged();
    }

    public BoardItemAdapter() {
        // add 100 items
        for (int i = 0; i < 10; ++i) {
            itemData.add(
                    new BoardData("name"+i, "author"+i, "description"+i, false)
            );
        }
    }

    public void removeSelection() {
        for (BoardData data : selectedData) {
            itemData.remove(data);
        }
        selectedData.clear();
        notifyDataSetChanged();
    }

    public void addBoardItem(BoardData data) {
        this.itemData.add(data);
        notifyDataSetChanged();
    }
}
