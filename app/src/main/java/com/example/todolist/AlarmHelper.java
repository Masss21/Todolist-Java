package com.example.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHelper {

    public static void setAlarm(Context c, Task task) {
        if (task.getDueDateMillis() <= 0) return;

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(c, ReminderReceiver.class);
        i.putExtra("taskId", task.getId());
        i.putExtra("title", task.getTitle());
        i.putExtra("desc", task.getDescription());

        PendingIntent pi = PendingIntent.getBroadcast(
                c,
                task.getId(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getDueDateMillis(), pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, task.getDueDateMillis(), pi);
            }
        }
    }

    public static void cancelAlarm(Context c, Task task) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(c, ReminderReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                c,
                task.getId(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {
            am.cancel(pi);
        }
    }
}
