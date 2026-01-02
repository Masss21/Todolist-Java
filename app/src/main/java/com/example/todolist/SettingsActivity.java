package com.example.todolist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    // UI
    private RecyclerView rvRecycleBin;
    private TextView tvEmptyBin;
    private Button btnEmptyAll;
    private EditText etSearchRecycle;
    private Spinner spinnerSortRecycle;

    // Data
    private TaskAdapter adapter;
    private TaskDao taskDao;
    private ExecutorService executor;

    // State
    private SortOption currentSort = SortOption.DATE_MODIFIED;
    private String currentQuery = "";

    // LiveData reference (important to avoid multiple observers)
    private LiveData<?> currentSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        initViews();
        setupDatabase();
        setupRecyclerView();
        setupButtons();
        setupSortSpinner();
        setupSearch();

        loadRecycleBin();
    }

    // =========================
    // TOOLBAR
    // =========================
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recycle Bin");
        }
    }

    // =========================
    // INIT
    // =========================
    private void initViews() {
        rvRecycleBin = findViewById(R.id.rvRecycleBin);
        tvEmptyBin = findViewById(R.id.tvEmptyBin);
        btnEmptyAll = findViewById(R.id.btnEmptyAll);
        etSearchRecycle = findViewById(R.id.etSearchRecycle);
        spinnerSortRecycle = findViewById(R.id.spinnerSortRecycle);
    }

    private void setupDatabase() {
        TaskDatabase db = TaskDatabase.getInstance(this);
        taskDao = db.taskDao();
        executor = Executors.newSingleThreadExecutor();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(this, new TaskAdapter.OnTaskClickListener() {
            @Override public void onEditClick(Task task) {}
            @Override public void onDeleteClick(Task task) {}
            @Override public void onCheckChanged(Task task, boolean isChecked) {}

            @Override
            public void onRestoreClick(Task task) {
                restoreTask(task);
            }

            @Override
            public void onPermanentDeleteClick(Task task) {
                showPermanentDeleteDialog(task);
            }

            @Override public void onTaskClick(Task task) {}
        });

        adapter.setRecycleBinMode(true);
        rvRecycleBin.setLayoutManager(new LinearLayoutManager(this));
        rvRecycleBin.setAdapter(adapter);
    }

    private void setupButtons() {
        btnEmptyAll.setOnClickListener(v -> showEmptyAllDialog());
    }

    // =========================
    // SORTING
    // =========================
    private void setupSortSpinner() {
        ArrayAdapter<String> sortAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        for (SortOption option : SortOption.values()) {
            sortAdapter.add(option.getLabel());
        }

        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortRecycle.setAdapter(sortAdapter);
        spinnerSortRecycle.setSelection(currentSort.getId());

        spinnerSortRecycle.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentSort = SortOption.fromIndex(position);
                loadRecycleBin();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    // =========================
    // SEARCH
    // =========================
    private void setupSearch() {
        etSearchRecycle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                loadRecycleBin();
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // =========================
    // LOAD RECYCLE BIN (CORE)
    // =========================
    private void loadRecycleBin() {

        if (currentSource != null) {
            currentSource.removeObservers(this);
        }

        LiveData<?> source;

        boolean hasQuery = !currentQuery.isEmpty();

        if (hasQuery) {
            switch (currentSort) {
                case TITLE_AZ:
                    source = taskDao.searchDeletedTasksSortedByTitleAZ(currentQuery);
                    break;
                default:
                    source = taskDao.searchDeletedTasks(currentQuery);
                    break;
            }
        } else {
            switch (currentSort) {
                case TITLE_AZ:
                    source = taskDao.getDeletedTasksSortedByTitleAZ();
                    break;
                case TITLE_ZA:
                    source = taskDao.getDeletedTasksSortedByTitleZA();
                    break;
                case DATE_CREATED:
                    source = taskDao.getDeletedTasksSortedByCreated();
                    break;
                case DATE_MODIFIED:
                default:
                    source = taskDao.getDeletedTasks();
                    break;
            }
        }

        currentSource = source;

        ((LiveData<?>) source).observe(this, tasksObj -> {
            @SuppressWarnings("unchecked")
            java.util.List<Task> tasks = (java.util.List<Task>) tasksObj;

            boolean empty = tasks == null || tasks.isEmpty();
            rvRecycleBin.setVisibility(empty ? View.GONE : View.VISIBLE);
            tvEmptyBin.setVisibility(empty ? View.VISIBLE : View.GONE);
            btnEmptyAll.setEnabled(!empty);

            adapter.setTasks(tasks);
        });
    }

    // =========================
    // ACTIONS
    // =========================
    private void restoreTask(Task task) {
        executor.execute(() -> {
            taskDao.restoreTask(task.getId(), System.currentTimeMillis());
            runOnUiThread(() ->
                    Toast.makeText(this, "Task restored", Toast.LENGTH_SHORT).show()
            );
        });
    }

    private void showPermanentDeleteDialog(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Forever")
                .setMessage("This will permanently delete \"" + task.getTitle() + "\". This action cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    executor.execute(() -> {
                        taskDao.permanentlyDelete(task.getId());
                        runOnUiThread(() ->
                                Toast.makeText(this, "Task deleted permanently", Toast.LENGTH_SHORT).show()
                        );
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEmptyAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Empty Recycle Bin")
                .setMessage("This will permanently delete ALL tasks in recycle bin. This action cannot be undone.")
                .setPositiveButton("Empty All", (d, w) -> {
                    executor.execute(() -> {
                        taskDao.emptyRecycleBin();
                        runOnUiThread(() ->
                                Toast.makeText(this, "Recycle bin emptied", Toast.LENGTH_SHORT).show()
                        );
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // =========================
    // LIFECYCLE
    // =========================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
