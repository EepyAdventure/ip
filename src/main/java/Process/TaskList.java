package Process;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TaskList extends ArrayList<Task> {
    private int count;
    public TaskList(Path save) throws Exception {
        if (!Files.readAllLines(save).isEmpty()) {
            Scanner permLines = new Scanner(save);
            String type;
            String[] line;
            while (permLines.hasNext()) {
                type = permLines.next();
                line = permLines.nextLine().trim().split("\\s+");
                this.add(Task.makeTask(type, line));
            }
        }
    }

    public TaskList(Task[] tasks) throws Exception {
        super();
        Collections.addAll(this, tasks);
    }

    public TaskList() {
        super();
    }

    public int getCount() {
        return this.count;
    }
    @Override
    public void clear() {
        this.count = 0;
        super.clear();
    }
    @Override
    public boolean add(Task task) {
        this.count++;
        return super.add(task);
    }
    @Override
    public Task remove(int idx) {
        this.count--;
        return super.remove(idx);
    }

    public TaskList find(String substring) {
        return this.stream()
                .filter(task -> task.getDescription().contains(substring))
                .collect(Collectors.toCollection(TaskList::new));

    }

    @Override
    public String toString() {
        String format = "%d. %s \n";
        String res = "";
        for (int i = 0; i < this.count; i++) {
            res = res + String.format(format, i, super.get(i).toString());
        }
        return res;
    }
    public byte[] toSave() {
        String format = "%s \n";
        String res = "";
        for (int i = 0; i <this.count; i++) {
            res = res + String.format(format, super.get(i).toSave());
        }
        return res.getBytes();
    }
}
