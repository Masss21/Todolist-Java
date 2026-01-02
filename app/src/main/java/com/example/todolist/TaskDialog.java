package com.example.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

public class TaskDialog {

    public interface AddCallback {
        void onSave(Task task);
    }

    public interface EditCallback {
        void onUpdate(Task task);
    }

    // Dialog Tambah (lengkap)
    public static void showAddDialog(Context c, AddCallback callback, int defaultPosition) {
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_task, null);

        EditText etTitle = v.findViewById(R.id.etTitle);
        EditText etDesc = v.findViewById(R.id.etDesc);
        TextView tvDate = v.findViewById(R.id.tvDate);
        RadioGroup rgStatus = v.findViewById(R.id.rgStatus);
        Button btnPickDate = v.findViewById(R.id.btnPickDate);

        final long[] pickedMillis = {0};

        btnPickDate.setOnClickListener(x -> {
            final Calendar now = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(
                    new ContextThemeWrapper(c, R.style.CustomDatePickerDialog),
                    (view, year, month, dayOfMonth) -> {

                        TimePickerDialog tp = new TimePickerDialog(
                                new ContextThemeWrapper(c, R.style.CustomTimePickerDialog),
                                (timeView, hourOfDay, minute) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                    pickedMillis[0] = cal.getTimeInMillis();
                    tvDate.setText(DateFormat.getDateTimeInstance().format(cal.getTime()));
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                tp.show();
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(c.getString(R.string.dialog_add_title))
                .setView(v)
                .setPositiveButton(c.getString(R.string.btn_save), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    int status = 0;
                    int checked = rgStatus.getCheckedRadioButtonId();
                    if (checked == R.id.rbProgress) status = 1;
                    else if (checked == R.id.rbComplete) status = 2;

                    Task newTask = new Task(title, false, desc, pickedMillis[0], status, defaultPosition);
                    callback.onSave(newTask);
                })
                .setNegativeButton(c.getString(R.string.btn_cancel), null)
                .create();

        dialog.setOnShowListener(d -> {
            int color = ContextCompat.getColor(c, R.color.dialog_button);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        });

        dialog.show();
    }

    // Dialog Edit (fill existing)
    public static void showEditDialog(Context c, Task task, EditCallback callback) {
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_task, null);

        EditText etTitle = v.findViewById(R.id.etTitle);
        EditText etDesc = v.findViewById(R.id.etDesc);
        TextView tvDate = v.findViewById(R.id.tvDate);
        RadioGroup rgStatus = v.findViewById(R.id.rgStatus);
        Button btnPickDate = v.findViewById(R.id.btnPickDate);

        etTitle.setText(task.getTitle());
        etDesc.setText(task.getDescription());
        if (task.getDueDateMillis() > 0) {
            tvDate.setText(DateFormat.getDateTimeInstance().format(task.getDueDateMillis()));
        }

        if (task.getStatus() == 0) rgStatus.check(R.id.rbTodo);
        else if (task.getStatus() == 1) rgStatus.check(R.id.rbProgress);
        else rgStatus.check(R.id.rbComplete);

        final long[] pickedMillis = {task.getDueDateMillis()};

        btnPickDate.setOnClickListener(x -> {
            final Calendar now = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(c, (view, year, month, dayOfMonth) -> {
                TimePickerDialog tp = new TimePickerDialog(c, (timeView, hourOfDay, minute) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                    pickedMillis[0] = cal.getTimeInMillis();
                    tvDate.setText(DateFormat.getDateTimeInstance().format(cal.getTime()));
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                tp.show();
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(c.getString(R.string.dialog_edit_title))
                .setView(v)
                .setPositiveButton(c.getString(R.string.btn_update), (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    int status = 0;
                    int checked = rgStatus.getCheckedRadioButtonId();
                    if (checked == R.id.rbProgress) status = 1;
                    else if (checked == R.id.rbComplete) status = 2;

                    task.setTitle(title);
                    task.setDescription(desc);
                    task.setDueDateMillis(pickedMillis[0]);
                    task.setStatus(status);

                    callback.onUpdate(task);
                })
                .setNegativeButton(c.getString(R.string.btn_cancel), null)
                .create();

        dialog.setOnShowListener(d -> {
            int color = ContextCompat.getColor(c, R.color.dialog_button);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
        });

        dialog.show();
    }
}
