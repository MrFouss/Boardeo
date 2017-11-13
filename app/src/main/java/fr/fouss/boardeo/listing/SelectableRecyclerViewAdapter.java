package fr.fouss.boardeo.listing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Adapter that handle the translation from brut data to displayable view in recycler view
 * the user is responsible for updating the adapter when there is a data update
 */
public abstract class SelectableRecyclerViewAdapter<Data>
        extends RecyclerView.Adapter<SelectableRecyclerViewAdapter.SelectableItemViewHolder> {

    ///// FIELDS /////

    /**
     * List of item data
     */
    protected final ArrayList<Data> data = new ArrayList<>();

    /**
     * list of selected data
     */
    protected final LinkedList<Data> selectedData = new LinkedList<>();

    /**
     * Are items selectable (selection mode)
     */
    private boolean selectionMode = false;

    /**
     * list of listener of the mode (selection or not)
     */
    private ModeListener modeListener = null;

    /**
     * the id of the items layout used to inflate them
     */
    private int itemLayoutId;

    ///// CONSTRUCTOR /////


    public SelectableRecyclerViewAdapter(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }

    ///// NECESSARY IMPLEMENTATIONS /////

    /**
     * Called when the instantiation of a new view holder is necessary
     * @param parent parent view (the recycler view)
     * @param viewType the type of the view to be created (not used, default to 0)
     * @return the view holder encapsulating the newly created view
     */
    @Override
    public SelectableItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate (parse and create) view from layout file
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutId, parent, false);
        return createViewHolder(newView);
    }

    /**
     * Called when the data displayed by holder needs to be changed
     * This is typically called when a view just has been created or is recycled
     * @param holder view holder encapsulation the view
     * @param position the position of the data to be displayed in the data list
     */
    @Override
    public void onBindViewHolder(SelectableRecyclerViewAdapter.SelectableItemViewHolder holder, int position) {
        holder.set(data.get(position));
    }

    /**
     * create the view holder depending on its implementation
     */
    public abstract SelectableItemViewHolder createViewHolder(View view);

    ///// DATA MANAGEMENT /////

    @Override
    public int getItemCount() {
        return data.size();
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

        // notify mode listener
        if (modeListener != null) {
            modeListener.onSelectionModeToggle(selectionMode);
        }
    }

    public void deleteSelection() {
        for (Data d : selectedData) {
            data.remove(d);
        }
        selectedData.clear();
    }

    public void addItem(Data d) {
        this.data.add(d);
    }

    public Data getItem(int position) {return this.data.get(position);}

    public boolean isSelected(int position) {
        return selectedData.contains(data.get(position));
    }

    public void select(int position) {
        if (!isSelected(position)) {
            selectedData.add(data.get(position));
        }
    }

    public void unSelect(int position) {
        selectedData.remove(data.get(position));
    }

    ///// SELECTABLE VIEW HOLDER /////

    /**
     * Implement a basic view holder that requires a view with at least a checkbox to manage selection
     */
    public abstract class SelectableItemViewHolder extends RecyclerView.ViewHolder {

        private CheckBox selectionCheckbox;

        public SelectableItemViewHolder(View itemView, CheckBox selectionCheckbox) {
            super(itemView);

            this.selectionCheckbox = selectionCheckbox;

            // setup listeners
            selectionCheckbox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> onSelectionCheckedChange(buttonView, isChecked)
            );
            itemView.setOnLongClickListener(v -> onItemLongClick(v));
        }

        public void set(Data data) {
            // selection mode
            if (isSelectionMode()) {
                // set selection checkbox visible
                selectionCheckbox.setChecked(isSelected(this.getAdapterPosition()));
                selectionCheckbox.setVisibility(View.VISIBLE);
            } else {
                // set selection checkbox gone
                selectionCheckbox.setVisibility(View.GONE);
            }

            setData(data);
        }

        /**
         * Must be implemented to synchronise the data and the views
         * @param data the data to display
         */
        public abstract void setData(Data data);

        ///// EVENTS /////

        public boolean onItemLongClick(View v) {
            // toggle selection mode
            setSelectionMode(true);
            select(getAdapterPosition());
            notifyDataSetChanged();
            return true;
        }

        public void onSelectionCheckedChange(CompoundButton buttonView, boolean isChecked) {
            // add/remove item to the selection list
            if (isChecked) {
                select(this.getAdapterPosition());
            } else {
                unSelect(this.getAdapterPosition());
            }
        }

    }

    ///// MODE LISTENER /////

    public void setModeListener(ModeListener modeListener) {
        this.modeListener = modeListener;
    }

    /**
     * Must be implemented to be notified of mode changes
     */
    public interface ModeListener {
        void onSelectionModeToggle(boolean selectionMode);
    }
}
