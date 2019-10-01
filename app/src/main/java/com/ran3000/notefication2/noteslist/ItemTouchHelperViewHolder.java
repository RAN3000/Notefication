package com.ran3000.notefication2.noteslist;

import androidx.recyclerview.widget.ItemTouchHelper;

/**
 * Notifies a View Holder of relevant callbacks from
 * {@link ItemTouchHelper.Callback}.
 *
 *
 * from: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelper} first registers an
     * item as being moved or swiped.
     * Implementations should update the item view to indicate
     * it's active state.
     */
    void onItemSelected();


    /**
     * Called when the {@link ItemTouchHelper} has completed the
     * move or swipe, and the active item state should be cleared.
     */
    void onItemClear();
}
