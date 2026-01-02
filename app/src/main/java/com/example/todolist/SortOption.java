package com.example.todolist;

public enum SortOption {
    POSITION(0, "Default"),
    TITLE_AZ(1, "Sort by A-Z"),
    TITLE_ZA(2, "Sort by Z-A"),
    DATE_CREATED(3, "Sort by Created Date"),
    DATE_MODIFIED(4, "Sort by Modified Date");

    private final int id;
    private final String label;

    SortOption(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static SortOption fromIndex(int index) {
        switch (index) {
            case 0: return POSITION;
            case 1: return TITLE_AZ;
            case 2: return TITLE_ZA;
            case 3: return DATE_CREATED;
            case 4: return DATE_MODIFIED;
            default: return POSITION;
        }
    }
}