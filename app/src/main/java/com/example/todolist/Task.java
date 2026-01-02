package com.example.todolist;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;

    private long dueDateMillis; // 0 = no date

    private int status; // 0 = Todo, 1 = In Progress, 2 = Complete

    private int position; // untuk sorting manual

    private boolean isCompleted; // status selesai
    private boolean isDeleted;   // untuk Recycle Bin

    private long createdAt;  // timestamp dibuat
    private long modifiedAt; // timestamp terakhir diubah

    // === Constructor utama untuk Room ===
    public Task(String title, boolean isCompleted, String description,
                long dueDateMillis, int status, int position) {

        this.title = title;
        this.isCompleted = isCompleted;
        this.description = description;
        this.dueDateMillis = dueDateMillis;
        this.status = status;
        this.position = position;

        this.isDeleted = false;

        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.modifiedAt = now;
    }

    // === Constructor kosong untuk kebutuhan lain ===
    @Ignore
    public Task() {}

    // =======================
    //       GETTER SETTER
    // =======================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.modifiedAt = System.currentTimeMillis();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.modifiedAt = System.currentTimeMillis();
    }

    public long getDueDateMillis() { return dueDateMillis; }
    public void setDueDateMillis(long dueDateMillis) {
        this.dueDateMillis = dueDateMillis;
        this.modifiedAt = System.currentTimeMillis();
    }

    public int getStatus() { return status; }
    public void setStatus(int status) {
        this.status = status;
        this.modifiedAt = System.currentTimeMillis();
    }

    public int getPosition() { return position; }
    public void setPosition(int position) {
        this.position = position;
        // posisi tidak perlu update modifiedAt
    }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        this.modifiedAt = System.currentTimeMillis();
    }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        this.modifiedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(long modifiedAt) { this.modifiedAt = modifiedAt; }
}
