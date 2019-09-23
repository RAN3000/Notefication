package com.ran3000.notefication2;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ran3000.notefication2.data.Note;
import com.ran3000.notefication2.data.NoteDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesListActivity extends Activity {

    @BindView(R.id.notes_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.notes_list_no_notes_text)
    TextView noNotesTextView;

    private AppExecutors executors;

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

            List<Note> notes = database.noteDao().getAll();

            executors.mainThread().execute(() -> {
                if (notes.size() > 0) {
                    recyclerView.setAdapter(new NotesAdapter(notes));
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


    class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
        private List<Note> notes;

        class NoteViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView textView;
            RelativeLayout layout;
            NoteViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.notification_text);
                layout = v.findViewById(R.id.notification_layout);
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
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }
    }

}


