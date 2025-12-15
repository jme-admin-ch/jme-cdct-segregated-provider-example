package ch.admin.bit.jme.cdct.task;

import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class TaskRepository {

    private final Map<String, Task> tasks = new LinkedHashMap<>();

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }

    public Task saveNewTask(TaskCreation taskToCreate) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(taskToCreate.getTitle());
        task.setContent(taskToCreate.getContent());
        task.setTag(taskToCreate.getTag());
        task.setCreatedAt(ZonedDateTime.now());
        saveTask(task);
        return task;
    }

    public void saveTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteAll() {
        tasks.clear();
    }

}
