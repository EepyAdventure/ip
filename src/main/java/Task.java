public class Task {
    private final String description;
    private boolean status;
    protected Task(String description) {
        this.description = description;
        this.status = false;
    }
    public void setStatus(boolean newStatus) {
        this.status = newStatus;
    }
    @Override
    public String toString() {
        String format = "%s status: %b";
        return String.format(format, this.description, this.status);
    }
}
