import java.util.ArrayList;

public class TaskList extends ArrayList<Task> {
    private int count;
    private int index;
    public TaskList(int index) {
        this.index = index;
    }
    public void updateIndex(int index) {
        this.index = index;
    }
    public int getCount() {
        return this.count + this.index;
    }
    @Override
    public void clear() {
        this.index = this.index + this.count;
        super.clear();
    }
    @Override
    public boolean add(Task task) {
        this.count++;
        return super.add(task);
    }
    @Override
    public String toString() {
        String format = "%d. %s \n";
        String res = "";
        for (int i = 0; i <this.count; i++) {
            res = res + String.format(format, i + this.index, super.get(i).toString());
        }
        return res;
    }
}
