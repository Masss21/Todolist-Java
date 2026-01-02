package com.example.todolist;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public abstract TaskDao taskDao();

    // Executor untuk operasi database
    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    // ================================
    //     MIGRATION: Version 1 → 2
    // ================================
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Tambah kolom isDeleted (boolean → integer 0/1)
            database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0"
            );

            // Tambah createdAt timestamp
            database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN createdAt INTEGER NOT NULL DEFAULT " +
                            System.currentTimeMillis()
            );

            // Tambah modifiedAt timestamp
            database.execSQL(
                    "ALTER TABLE tasks ADD COLUMN modifiedAt INTEGER NOT NULL DEFAULT " +
                            System.currentTimeMillis()
            );
        }
    };

    // ================================
    //         GET INSTANCE
    // ================================
    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TaskDatabase.class,
                            "task_db"
                    )
                    // Hapus .addMigrations(MIGRATION_1_2)
                    // Ganti dengan:
                    .fallbackToDestructiveMigration() // <--- HARUS DITAMBAHKAN INI
                    .build();
        }
        return instance;
    }
}
