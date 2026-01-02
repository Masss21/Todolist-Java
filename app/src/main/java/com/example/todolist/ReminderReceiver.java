package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");

        if (taskId != -1 && title != null) {
            NotificationHelper.showNotification(context, taskId, title, desc);
        }
    }
}