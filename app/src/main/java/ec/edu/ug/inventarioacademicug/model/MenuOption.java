package ec.edu.ug.inventarioacademicug.model;

public class MenuOption {
    private int actionId;
    private String title;
    private String description;
    private String shortLabel;

    public MenuOption(int actionId, String title, String description, String shortLabel) {
        this.actionId = actionId;
        this.title = title;
        this.description = description;
        this.shortLabel = shortLabel;
    }

    public int getActionId() { return actionId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getShortLabel() { return shortLabel; }
}