package com.ran3000.notefication2.noteslist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ran3000.notefication2.AppExecutors;
import com.ran3000.notefication2.ColorManager;
import com.ran3000.notefication2.NoteficationForegroundService;
import com.ran3000.notefication2.R;
import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class NotesListActivity extends Activity {

    @BindView(R.id.notes_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.notes_list_no_notes_text)
    TextView noNotesTextView;
    @BindView(R.id.notes_list_update_button)
    Button updateButton;

    private AppExecutors executors;

    private List<Note> localNotes;
    private LinkedList<NoteDiff> noteDiffs = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executors = new AppExecutors();

        int darkColor = getIntent().getIntExtra("darkColor", ColorManager.DARK_GREEN);

        // no title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, darkColor));

        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        executors.diskIO().execute(() -> {
            NoteDatabase database = NoteDatabase.getAppDatabase(this);

            localNotes = database.noteDao().getAll();
            Collections.reverse(localNotes);

            executors.mainThread().execute(() -> {
                if (localNotes.size() > 0) {
                    NotesAdapter notesAdapter = new NotesAdapter(localNotes);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                    ItemTouchHelper.LEFT) {
                                public boolean onMove(@NonNull RecyclerView recyclerView,
                                                      @NonNull RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                    final int fromPos = viewHolder.getAdapterPosition();
                                    final int toPos = target.getAdapterPosition();
                                    // move item in `fromPos` to `toPos` in adapter.
                                    notesAdapter.onItemMove(fromPos, toPos);
                                    return true;// true if moved, false otherwise
                                }
                                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                    // remove from adapter
                                    // we aren't using this
                                }
                                @Override
                                public boolean isLongPressDragEnabled() {
                                    return true;
                                }
                                @Override
                                public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                                              int actionState) {   // We only want the active item
                                    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                                        if (viewHolder instanceof ItemTouchHelperViewHolder) {
                                            ItemTouchHelperViewHolder itemViewHolder =
                                                    (ItemTouchHelperViewHolder) viewHolder;
                                            itemViewHolder.onItemSelected();
                                        }
                                    }

                                    super.onSelectedChanged(viewHolder, actionState);
                                }

                                @Override
                                public void clearView(@NonNull RecyclerView recyclerView,
                                                      @NonNull RecyclerView.ViewHolder viewHolder) {
                                    super.clearView(recyclerView, viewHolder);

                                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                                        ItemTouchHelperViewHolder itemViewHolder =
                                                (ItemTouchHelperViewHolder) viewHolder;
                                        itemViewHolder.onItemClear();
                                    }
                                }

                            });
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                    recyclerView.setAdapter(notesAdapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noNotesTextView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @OnClick(R.id.notes_list_up_button)
    public void goToScreenUp() {
        onBackPressed();
    }

    @OnClick(R.id.notes_list_update_button)
    public void updateNotes() {

        // no diffs, just exit
        if (noteDiffs.size() == 0) {
            onBackPressed();
            return;
        }

        // update database
        updateButton.setEnabled(false); // prevent button to be clicked while updating notifications
        executors.diskIO().execute(() -> {
            NoteDatabase database = NoteDatabase.getAppDatabase(this);

            for (NoteDiff diff : noteDiffs) {
                Note currentNote = diff.getNote();
                if (currentNote.getId() == diff.getToId()) {
                    // Notes to be deleted

                    database.noteDao().delete(currentNote);
                }
            }

            executors.mainThread().execute(() -> {

                // recreate notifications
                Intent serviceIntent = new Intent(this, NoteficationForegroundService.class);
                ContextCompat.startForegroundService(this, serviceIntent);

                noteDiffs.clear();
                updateButton.setEnabled(true);

            });
        });
    }


    class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> implements ItemTouchHelperAdapter {
        private List<Note> notes;

        class NoteViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
            // each data item is just a string in this case
            TextView textView;
            RelativeLayout layout;
            ImageButton closeButton;
            int colorResource;
            NoteViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.notification_text);
                layout = v.findViewById(R.id.notification_layout);
                closeButton = v.findViewById(R.id.notification_close_button);
            }

            void setColor(int colorResource) {
                this.colorResource = colorResource;
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundResource(ColorManager.getDarkColorFor(colorResource));
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundResource(colorResource);
            }
        }

        NotesAdapter(List<Note> notes) {
            this.notes = notes;
        }

        @NonNull
        @Override
        public NotesAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_expanded_layout, parent, false);
            return new NoteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            Note note = notes.get(position);
            holder.textView.setText(note.getText());
            holder.layout.setBackgroundResource(note.getColor());
            holder.closeButton.setOnClickListener(v -> {
                notes.remove(position);
                noteDiffs.add(new NoteDiff(note, note.getId()));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notes.size());
            });
            holder.setColor(note.getColor());
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
//                    noteDiffs.add(new NoteDiff(notes.get(i), notes.get(i+1).getId()));
//                    noteDiffs.add(new NoteDiff(notes.get(i+1), notes.get(i).getId()));
                    Collections.swap(notes, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(notes, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }
    }

}


