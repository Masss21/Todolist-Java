package com.example.todolist;

public enum FilterOption {
    ALL(0, "All"),
    TODO(1, "To Do"),
    PROGRESS(2, "In Progress"),
    COMPLETE(3, "Complete"),
    DELETED(4, "Recycle Bin");

    private final int id;
    private final String label;

    FilterOption(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}