package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private TaskDao taskDao;
    private ExecutorService executor;
    private Handler mainHandler;

    // Filter, Sort, Search
    private FilterOption currentFilter = FilterOption.TODO;
    private SortOption currentSort = SortOption.POSITION;
    private String currentSearchQuery = "";

    // UI State
    private boolean isSearchVisible = false;
    private boolean isFilterDropdownVisible = false;

    // CRITICAL FIX: Simplified flag untuk prevent loop
    private boolean isProcessingUpdate = false;
    private LiveData<List<Task>> currentLiveData;
    private Observer<List<Task>> currentObserver;

    // Views
    private TextView btnFilterTodo, btnFilterProgress, btnFilterComplete;
    private TextView tvEmptyState, tvFilterLabel;
    private LinearLayout btnFilter;
    private RecyclerView rvFilterDropdown;
    private ImageView btnSearch, ivThemeToggle, ivSettings, flagImage;
    private EditText etSearch;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupDatabase();
        setupRecyclerView();
        setupNavigation();
        setupFilterDropdown();
        setupSearch();
        setupFAB();
        setupThemeToggle();
        setupSettings();
        setupLanguageFlag();
        setupSwipeGestures();

        loadTasks();
    }

    // ==================== INITIALIZATION ====================

    private void applyTheme() {
        if (ThemePreferences.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void initViews() {
        rvTasks = findViewById(R.id.rvTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnFilterTodo = findViewById(R.id.btnFilterTodo);
        btnFilterProgress = findViewById(R.id.btnFilterProgress);
        btnFilterComplete = findViewById(R.id.btnFilterComplete);
        btnFilter = findViewById(R.id.btnFilter);
        tvFilterLabel = findViewById(R.id.tvFilterLabel);
        rvFilterDropdown = findViewById(R.id.rvFilterDropdown);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAdd);
        ivThemeToggle = findViewById(R.id.ivThemeToggle);
        ivSettings = findViewById(R.id.ivSettings);
        flagImage = findViewById(R.id.flagImage);
        helloText = findViewById(R.id.helloText);

    }



    private void setupDatabase() {
        TaskDatabase db = TaskDatabase.getInstance(this);
        taskDao = db.taskDao();
        executor = Executors.newSingleThreadExecutor();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(this, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onEditClick(Task task) {
                showEditDialog(task);
            }

            @Override
            public void onDeleteClick(Task task) {
                moveToRecycleBinDirectly(task);
            }

            @Override
            public void onCheckChanged(Task task, boolean isChecked) {
                // ✅ FIXED: Handle checkbox dengan immediate refresh
                handleCheckboxChangeImmediate(task, isChecked);
            }

            @Override
            public void onRestoreClick(Task task) {
                // Not used here
            }

            @Override
            public void onPermanentDeleteClick(Task task) {
                // Not used here
            }

            @Override
            public void onTaskClick(Task task) {
                openTaskDetail(task);
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);
    }

    // ==================== NAVIGATION (3 TABS) ====================

    private void setupNavigation() {
        btnFilterTodo.setOnClickListener(v -> selectNavigation(FilterOption.TODO));
        btnFilterProgress.setOnClickListener(v -> selectNavigation(FilterOption.PROGRESS));
        btnFilterComplete.setOnClickListener(v -> selectNavigation(FilterOption.COMPLETE));

        updateNavigationUI();
    }

    private void selectNavigation(FilterOption filter) {
        currentFilter = filter;
        updateNavigationUI();

        // ✅ FIXED: Hapus animasi fade yang menyebabkan berkedip
        // Langsung load tasks tanpa animasi

        loadTasks();
    }

    private void updateNavigationUI() {
        btnFilterTodo.setBackgroundResource(R.drawable.tab_unselected);
        btnFilterProgress.setBackgroundResource(R.drawable.tab_unselected);
        btnFilterComplete.setBackgroundResource(R.drawable.tab_unselected);

        int activeColor = getThemeColor(R.attr.navigationActiveTextColor);
        int inactiveColor = getThemeColor(R.attr.navigationInactiveTextColor);

        btnFilterTodo.setTextColor(inactiveColor);
        btnFilterProgress.setTextColor(inactiveColor);
        btnFilterComplete.setTextColor(inactiveColor);

        TextView selected = null;
        if (currentFilter == FilterOption.TODO) {
            selected = btnFilterTodo;
        } else if (currentFilter == FilterOption.PROGRESS) {
            selected = btnFilterProgress;
        } else if (currentFilter == FilterOption.COMPLETE) {
            selected = btnFilterComplete;
        }

        if (selected != null) {
            selected.setBackgroundResource(R.drawable.tab_selected);
            selected.setTextColor(activeColor);
        }
    }

    private int getThemeColor(int attrId) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }

    // ==================== FILTER DROPDOWN ====================

    private void setupFilterDropdown() {
        btnFilter.setOnClickListener(v -> toggleFilterDropdown());

        rvFilterDropdown.setLayoutManager(new LinearLayoutManager(this));
        FilterDropdownAdapter dropdownAdapter = new FilterDropdownAdapter(sortOption -> {
            currentSort = sortOption;
            tvFilterLabel.setText(sortOption.getLabel());
            hideFilterDropdown();
            loadTasks();
        });
        rvFilterDropdown.setAdapter(dropdownAdapter);
    }

    private void toggleFilterDropdown() {
        if (isFilterDropdownVisible) {
            hideFilterDropdown();
        } else {
            showFilterDropdown();
            hideSearch();
        }
    }

    /**
     * ✅ FIXED: Smooth expand animation untuk dropdown
     */
    private void showFilterDropdown() {
        rvFilterDropdown.setVisibility(View.VISIBLE);
        Animation expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand_vertical);
        rvFilterDropdown.startAnimation(expandAnim);
        isFilterDropdownVisible = true;
    }

    /**
     * ✅ FIXED: Smooth collapse animation untuk dropdown
     */
    private void hideFilterDropdown() {
        Animation collapseAnim = AnimationUtils.loadAnimation(this, R.anim.collapse_vertical);
        collapseAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                rvFilterDropdown.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        rvFilterDropdown.startAnimation(collapseAnim);
        isFilterDropdownVisible = false;
    }

    // ==================== SEARCH ====================

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> toggleSearch());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                loadTasks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleSearch() {
        if (isSearchVisible) {
            hideSearch();
        } else {
            showSearch();
            hideFilterDropdown();
        }
    }

    /**
     * ✅ FIXED: Smooth expand animation untuk search
     */
    private void showSearch() {
        etSearch.setVisibility(View.VISIBLE);
        Animation expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand_vertical);
        etSearch.startAnimation(expandAnim);
        etSearch.requestFocus();
        isSearchVisible = true;
    }

    /**
     * ✅ FIXED: Smooth collapse animation untuk search
     */
    private void hideSearch() {
        Animation collapseAnim = AnimationUtils.loadAnimation(this, R.anim.collapse_vertical);
        collapseAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                etSearch.setVisibility(View.GONE);
                etSearch.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        etSearch.startAnimation(collapseAnim);
        currentSearchQuery = "";
        isSearchVisible = false;
        loadTasks();
    }

    // ==================== SWIPE GESTURES ====================

    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = vh.getAdapterPosition();
                int to = target.getAdapterPosition();
                adapter.moveItem(from, to);
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
                super.clearView(rv, vh);
                saveAllPositions();
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int pos = vh.getAdapterPosition();
                Task task = adapter.getItem(pos);

                if (task == null) return;

                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe kiri = delete
                    moveToRecycleBinDirectly(task);
                } else {
                    // Swipe kanan = edit
                    adapter.notifyItemChanged(pos);
                    showEditDialog(task);
                }
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(rvTasks);
    }

    private void saveAllPositions() {
        executor.execute(() -> {
            List<Task> list = adapter.getCurrentList();
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setPosition(i);
                }
                taskDao.updateTasks(list);
            }
        });
    }

    // ==================== FAB & DIALOGS ====================

    private void setupFAB() {
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        TaskDialog.showAddDialog(this, task -> {
            executor.execute(() -> {
                long id = taskDao.insert(task);
                task.setId((int) id);

                if (task.getDueDateMillis() > 0) {
                    AlarmHelper.setAlarm(MainActivity.this, task);
                }

                mainHandler.post(() ->
                        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                );
            });
        }, 0);
    }

    private void showEditDialog(Task task) {
        TaskDialog.showEditDialog(this, task, updatedTask -> {
            executor.execute(() -> {
                updatedTask.setModifiedAt(System.currentTimeMillis());
                taskDao.update(updatedTask);

                if (updatedTask.getDueDateMillis() > 0) {
                    AlarmHelper.cancelAlarm(this, updatedTask);
                    AlarmHelper.setAlarm(this, updatedTask);
                }

                mainHandler.post(() ->
                        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                );
            });
        });
    }

    /**
     * ✅ Delete langsung tanpa dialog konfirmasi
     */
    private void moveToRecycleBinDirectly(Task task) {
        task.setDeleted(true);

        executor.execute(() -> {
            taskDao.update(task);

            mainHandler.post(() -> {
                Toast.makeText(this, "Moved to recycle bin", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // ==================== THEME & SETTINGS ====================

    private void setupThemeToggle() {
        updateThemeIcon();
        ivThemeToggle.setOnClickListener(v -> {
            boolean isDark = ThemePreferences.isDarkMode(this);
            ThemePreferences.setDarkMode(this, !isDark);
            recreate();
        });
    }

    private void updateThemeIcon() {
        if (ThemePreferences.isDarkMode(this)) {
            ivThemeToggle.setImageResource(R.drawable.ic_sun);
        } else {
            ivThemeToggle.setImageResource(R.drawable.ic_moon);
        }
    }

    private void setupSettings() {
        ivSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsMenuActivity.class);
            startActivity(intent);
        });
    }

    private TextView helloText;
    private void setupLanguageFlag() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String country = locale.getCountry();

        int flagRes = R.drawable.flag_id;
        String helloStr = getString(R.string.hello);

        if (lang.equals("en") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloStr = "Hello";
        } else if (lang.equals("fr") && country.equals("CA")) {
            flagRes = R.drawable.flag_ca;
            helloStr = "Bonjour";
        } else if (lang.equals("en")) {
            flagRes = R.drawable.flag_us;
            helloStr = "Hello";
        } else if (lang.equals("fr")) {
            flagRes = R.drawable.flag_fr;
            helloStr = "Bonjour";
        } else if (lang.equals("it")) {
            flagRes = R.drawable.flag_it;
            helloStr = "Ciao";
        } else if (lang.equals("de")) {
            flagRes = R.drawable.flag_de;
            helloStr = "Hallo";
        }

        flagImage.setImageResource(flagRes);
        if (helloText != null) {
            helloText.setText(helloStr);
        }
    }


    // ==================== LOAD TASKS ====================

    /**
     * ✅ FIXED: Load tasks dengan LiveData auto-refresh yang lebih baik
     */
    private void loadTasks() {
        // Remove previous observer to prevent memory leak
        if (currentLiveData != null && currentObserver != null) {
            currentLiveData.removeObserver(currentObserver);
        }

        LiveData<List<Task>> liveData = getTasksLiveData();
        currentLiveData = liveData;

        // Create new observer
        currentObserver = tasks -> {
            // Skip jika sedang proses update untuk prevent flicker
            if (isProcessingUpdate) {
                return;
            }

            if (tasks == null || tasks.isEmpty()) {
                rvTasks.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);

                if (!currentSearchQuery.isEmpty()) {
                    tvEmptyState.setText("No tasks found");
                } else {
                    tvEmptyState.setText(R.string.empty_tasks);
                }
            } else {
                rvTasks.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            }

            adapter.setTasks(tasks);
        };

        liveData.observe(this, currentObserver);
    }

    private LiveData<List<Task>> getTasksLiveData() {
        int status = getStatusFromFilter(currentFilter);

        if (!currentSearchQuery.isEmpty()) {
            return taskDao.searchTasksByStatus(status, currentSearchQuery);
        }

        return getTasksByStatusWithSort(status);
    }

    private LiveData<List<Task>> getTasksByStatusWithSort(int status) {
        switch (currentSort) {
            case TITLE_AZ:
                return taskDao.getTasksByStatusSortedByTitleAZ(status);
            case TITLE_ZA:
                return taskDao.getTasksByStatusSortedByTitleZA(status);
            case DATE_CREATED:
                return taskDao.getTasksByStatusSortedByCreated(status);
            case DATE_MODIFIED:
                return taskDao.getTasksByStatusSortedByModified(status);
            default:
                return taskDao.getTasksByStatus(status);
        }
    }

    private int getStatusFromFilter(FilterOption filter) {
        if (filter == FilterOption.TODO) return 0;
        if (filter == FilterOption.PROGRESS) return 1;
        if (filter == FilterOption.COMPLETE) return 2;
        return 0;
    }

    // ==================== CHECKBOX HANDLER ====================

    /**
     * ✅ CRITICAL FIX: Handle checkbox dengan IMMEDIATE refresh
     *
     * Status progression:
     * - Forward (check): todo (0) → progress (1) → complete (2)
     * - Backward (uncheck): complete (2) → progress (1) → todo (0)
     *
     * FIXED: Task langsung pindah kategori tanpa perlu tekan navigasi
     */
    private void handleCheckboxChangeImmediate(Task task, boolean isChecked) {
        // Set flag untuk prevent observer trigger selama update
        isProcessingUpdate = true;

        int currentStatus = task.getStatus();
        int newStatus;

        if (isChecked) {
            // FORWARD: Increment status
            if (currentStatus == 0) {
                newStatus = 1; // todo → in progress
            } else if (currentStatus == 1) {
                newStatus = 2; // in progress → complete
            } else {
                newStatus = 2; // already complete
            }
        } else {
            // ✅ BACKWARD FIXED: Decrement status sampai 0
            if (currentStatus == 2) {
                newStatus = 1; // complete → in progress
            } else if (currentStatus == 1) {
                newStatus = 0; // in progress → todo ✅ FIXED!
            } else {
                newStatus = 0; // already todo
            }
        }

        // Update task object
        final int finalStatus = newStatus;
        task.setStatus(finalStatus);
        task.setCompleted(finalStatus == 2);
        task.setModifiedAt(System.currentTimeMillis());

        // Update in database
        executor.execute(() -> {
            taskDao.update(task);

            // ✅ CRITICAL FIX: Allow refresh setelah minimal delay
            mainHandler.postDelayed(() -> {
                isProcessingUpdate = false;
                // Force refresh untuk pindah kategori
                mainHandler.post(() -> loadTasks());
            }, 100); // Minimal delay 100ms
        });
    }

    // ==================== TASK DETAIL ====================

    private void openTaskDetail(Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        startActivity(intent);
    }

    /**
     * Helper untuk apply fade-in animation
     */
    private void applyFadeIn(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeIn);
    }

    // ==================== LIFECYCLE ====================

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentLiveData != null && currentObserver != null) {
            currentLiveData.removeObserver(currentObserver);
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // ==================== FILTER DROPDOWN ADAPTER ====================

    private static class FilterDropdownAdapter extends RecyclerView.Adapter<FilterDropdownAdapter.ViewHolder> {

        private final SortOption[] options = SortOption.values();
        private final OnSortSelectedListener listener;

        interface OnSortSelectedListener {
            void onSortSelected(SortOption option);
        }

        FilterDropdownAdapter(OnSortSelectedListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SortOption option = options[position];
            holder.textView.setText(option.getLabel());
            holder.textView.setOnClickListener(v -> listener.onSortSelected(option));
        }

        @Override
        public int getItemCount() {
            return options.length;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(TextView tv) {
                super(tv);
                textView = tv;
            }
        }
    }
}