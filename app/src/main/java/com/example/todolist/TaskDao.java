package com.example.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Update
    void updateTasks(List<Task> tasks);

    @Delete
    void delete(Task task);

    // Get task by ID (for detail view)
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    Task getTaskById(int taskId);

    // Get all tasks (tidak termasuk yang di recycle bin)
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY position ASC")
    LiveData<List<Task>> getAllTasks();

    // Get tasks by status
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status ORDER BY position ASC")
    LiveData<List<Task>> getTasksByStatus(int status);

    // Get deleted tasks (recycle bin)
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY modifiedAt DESC")
    LiveData<List<Task>> getDeletedTasks();

    // Search tasks
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND title LIKE '%' || :query || '%' ORDER BY position ASC")
    LiveData<List<Task>> searchTasks(String query);

    // Search tasks by status
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status AND title LIKE '%' || :query || '%' ORDER BY position ASC")
    LiveData<List<Task>> searchTasksByStatus(int status, String query);

    // Get all tasks sorted by title A-Z
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY LOWER(title) ASC")
    LiveData<List<Task>> getTasksSortedByTitleAZ();

    // Get all tasks sorted by title Z-A
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY LOWER(title) DESC")
    LiveData<List<Task>> getTasksSortedByTitleZA();

    // Get all tasks sorted by created date
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY createdAt DESC")
    LiveData<List<Task>> getTasksSortedByCreated();

    // Get all tasks sorted by modified date
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY modifiedAt DESC")
    LiveData<List<Task>> getTasksSortedByModified();

    // Get tasks by status sorted by title A-Z
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status ORDER BY LOWER(title) ASC")
    LiveData<List<Task>> getTasksByStatusSortedByTitleAZ(int status);

    // Get tasks by status sorted by title Z-A
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status ORDER BY LOWER(title) DESC")
    LiveData<List<Task>> getTasksByStatusSortedByTitleZA(int status);

    // Get tasks by status sorted by created date
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status ORDER BY createdAt DESC")
    LiveData<List<Task>> getTasksByStatusSortedByCreated(int status);

    // Get tasks by status sorted by modified date
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND status = :status ORDER BY modifiedAt DESC")
    LiveData<List<Task>> getTasksByStatusSortedByModified(int status);

    // Permanently delete task
    @Query("DELETE FROM tasks WHERE id = :taskId")
    void permanentlyDelete(int taskId);

    // Restore task from recycle bin
    @Query("UPDATE tasks SET isDeleted = 0, modifiedAt = :timestamp WHERE id = :taskId")
    void restoreTask(int taskId, long timestamp);

    // Empty recycle bin
    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    void emptyRecycleBin();

    // =======================
// RECYCLE BIN - SEARCH
// =======================
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 AND title LIKE '%' || :query || '%' ORDER BY modifiedAt DESC")
    LiveData<List<Task>> searchDeletedTasks(String query);


    // =======================
// RECYCLE BIN - SORTING
// =======================
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY LOWER(title) ASC")
    LiveData<List<Task>> getDeletedTasksSortedByTitleAZ();

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY LOWER(title) DESC")
    LiveData<List<Task>> getDeletedTasksSortedByTitleZA();

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY createdAt DESC")
    LiveData<List<Task>> getDeletedTasksSortedByCreated();

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY modifiedAt DESC")
    LiveData<List<Task>> getDeletedTasksSortedByModified();

    // =======================
// RECYCLE BIN - SORT + SEARCH
// =======================
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 AND title LIKE '%' || :query || '%' ORDER BY LOWER(title) ASC")
    LiveData<List<Task>> searchDeletedTasksSortedByTitleAZ(String query);


}

