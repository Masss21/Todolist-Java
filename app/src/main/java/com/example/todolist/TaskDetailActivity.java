package com.example.todolist;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailDescription, tvDetailStatus, tvDetailDueDate, tvDetailCreated, tvDetailModified;
    private TaskDao taskDao;
    private ExecutorService executor;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Task Detail");
        }

        // Get task ID from intent
        taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId == -1) {
            finish();
            return;
        }

        initViews();
        setupDatabase();
        loadTaskDetail();
    }

    private void initViews() {
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailDueDate = findViewById(R.id.tvDetailDueDate);
        tvDetailCreated = findViewById(R.id.tvDetailCreated);
        tvDetailModified = findViewById(R.id.tvDetailModified);
    }

    private void setupDatabase() {
        TaskDatabase db = TaskDatabase.getInstance(this);
        taskDao = db.taskDao();
        executor = Executors.newSingleThreadExecutor();
    }

    private void loadTaskDetail() {
        executor.execute(() -> {
            Task task = taskDao.getTaskById(taskId);
            if (task != null) {
                runOnUiThread(() -> displayTask(task));
            } else {
                runOnUiThread(this::finish);
            }
        });
    }

    private void displayTask(Task task) {
        tvDetailTitle.setText(task.getTitle());

        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            tvDetailDescription.setText(task.getDescription());
        } else {
            tvDetailDescription.setText("No description");
        }

        // Status
        String statusText;
        int statusColor;
        switch (task.getStatus()) {
            case 0:
                statusText = "To Do";
                statusColor = 0xFFFFA726;
                break;
            case 1:
                statusText = "In Progress";
                statusColor = 0xFF42A5F5;
                break;
            case 2:
                statusText = "Complete";
                statusColor = 0xFF66BB6A;
                break;
            default:
                statusText = "Unknown";
                statusColor = 0xFF9E9E9E;
        }
        tvDetailStatus.setText(statusText);
        tvDetailStatus.setBackgroundColor(statusColor);

        // Due Date
        if (task.getDueDateMillis() > 0) {
            tvDetailDueDate.setText(DateFormat.getDateTimeInstance().format(task.getDueDateMillis()));
        } else {
            tvDetailDueDate.setText("No due date");
        }

        // Created
        tvDetailCreated.setText(DateFormat.getDateTimeInstance().format(task.getCreatedAt()));

        // Modified
        tvDetailModified.setText(DateFormat.getDateTimeInstance().format(task.getModifiedAt()));
    }

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