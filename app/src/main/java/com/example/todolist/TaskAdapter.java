package com.example.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();
    private Context context;
    private boolean isRecycleBinMode = false;

    public interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onCheckChanged(Task task, boolean isChecked);
        void onRestoreClick(Task task);
        void onPermanentDeleteClick(Task task);
        void onTaskClick(Task task);
    }

    private OnTaskClickListener listener;

    public TaskAdapter(Context context, OnTaskClickListener listener) {
        this.context = context;
        this.listener = listener;
        setHasStableIds(true); // CRITICAL: Enable stable IDs
    }

    @Override
    public long getItemId(int position) {
        // CRITICAL: Use task ID as stable ID to prevent recycling issues
        if (position >= 0 && position < taskList.size()) {
            return taskList.get(position).getId();
        }
        return RecyclerView.NO_ID;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // CRITICAL: Store task ID in holder to prevent stale references
        holder.taskId = task.getId();

        holder.tvTitle.setText(task.getTitle());

        // Description
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            holder.tvDesc.setText(task.getDescription());
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        // Status Badge
        int statusColor;
        String statusText;
        switch (task.getStatus()) {
            case 0:
                statusColor = 0xFFFFA726; // Orange - To Do
                statusText = "To Do";
                break;
            case 1:
                statusColor = 0xFF42A5F5; // Blue - In Progress
                statusText = "In Progress";
                break;
            case 2:
                statusColor = 0xFF66BB6A; // Green - Complete
                statusText = "Complete";
                break;
            default:
                statusColor = 0xFF9E9E9E;
                statusText = "Unknown";
        }
        holder.tvStatus.setBackgroundColor(statusColor);
        holder.tvStatus.setText(statusText);

        // Due Date
        if (task.getDueDateMillis() > 0) {
            holder.tvDueDate.setVisibility(View.VISIBLE);
            holder.tvDueDate.setText("Due: " +
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(task.getDueDateMillis()));
        } else {
            holder.tvDueDate.setVisibility(View.GONE);
        }

        // Strike through title if completed
        if (task.isCompleted()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Recycle Bin Mode
        if (isRecycleBinMode) {
            holder.checkBox.setVisibility(View.GONE);
            holder.recycleBinActions.setVisibility(View.VISIBLE);

            holder.btnRestore.setOnClickListener(v -> {
                if (listener != null) listener.onRestoreClick(task);
            });

            holder.btnPermanentDelete.setOnClickListener(v -> {
                if (listener != null) listener.onPermanentDeleteClick(task);
            });

            holder.itemView.setOnClickListener(null);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.recycleBinActions.setVisibility(View.GONE);

            // CRITICAL FIX: Properly handle checkbox state
            setupCheckbox(holder, task);

            // Task card click
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }

        // ✅ FIXED: Hapus animasi fade-in yang menyebabkan berkedip
        // Animasi dihapus agar tidak ada flicker saat refresh
    }

    /**
     * CRITICAL: Setup checkbox with proper state management to prevent glitch
     * FIXED: Checkbox sekarang bisa turun sampai status 0 (todo)
     */
    private void setupCheckbox(TaskViewHolder holder, Task task) {

        // 1. Lepas listener lama
        holder.checkBox.setOnCheckedChangeListener(null);

        // 2. Tentukan kondisi checkbox sesuai aturan status
        //    Status 0 (todo) = unchecked
        //    Status 1 (in progress) = unchecked
        //    Status 2 (complete) = checked
        boolean shouldBeChecked = (task.getStatus() == 2);

        // 3. Set tanpa trigger listener
        holder.checkBox.setChecked(shouldBeChecked);

        // 4. Tambahkan listener baru
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Task currentTask = getItem(pos);
            if (currentTask == null || currentTask.getId() != holder.taskId) return;

            // Cegah loop: hanya panggil listener jika state benar-benar berubah
            boolean expectedState = (currentTask.getStatus() == 2);
            if (isChecked != expectedState) {
                if (listener != null) {
                    listener.onCheckChanged(currentTask, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * CRITICAL: Use DiffUtil for efficient updates and prevent glitch
     */
    public void setTasks(List<Task> newTasks) {
        if (newTasks == null) {
            newTasks = new ArrayList<>();
        }

        final List<Task> oldList = this.taskList;
        final List<Task> newList = newTasks;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // Compare by ID
                return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Task oldTask = oldList.get(oldItemPosition);
                Task newTask = newList.get(newItemPosition);

                // Compare all relevant fields
                return oldTask.getTitle().equals(newTask.getTitle()) &&
                        oldTask.getStatus() == newTask.getStatus() &&
                        oldTask.isCompleted() == newTask.isCompleted() &&
                        oldTask.getPosition() == newTask.getPosition() &&
                        oldTask.getDueDateMillis() == newTask.getDueDateMillis();
            }
        });

        this.taskList = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setRecycleBinMode(boolean mode) {
        this.isRecycleBinMode = mode;
        notifyDataSetChanged();
    }

    public List<Task> getCurrentList() {
        return new ArrayList<>(taskList); // Return copy to prevent external modification
    }

    public Task getItem(int position) {
        if (position >= 0 && position < taskList.size()) {
            return taskList.get(position);
        }
        return null;
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= taskList.size() ||
                toPosition < 0 || toPosition >= taskList.size()) {
            return;
        }

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(taskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(taskList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    // ViewHolder
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvTitle, tvDesc, tvStatus, tvDueDate;
        LinearLayout recycleBinActions;
        TextView btnRestore, btnPermanentDelete;
        int taskId = -1; // CRITICAL: Store task ID to prevent stale references

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbTask);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            recycleBinActions = itemView.findViewById(R.id.recycleBinActions);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnPermanentDelete = itemView.findViewById(R.id.btnPermanentDelete);
        }
    }
}