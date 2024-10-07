package dev.sandeep.simple.task.management.system.controller;

import dev.sandeep.simple.task.management.system.model.Status;
import dev.sandeep.simple.task.management.system.model.Task;
import dev.sandeep.simple.task.management.system.request.TaskRequestVo;
import dev.sandeep.simple.task.management.system.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MyController {

    private final TaskService taskService;

    public MyController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> createTask(@RequestBody TaskRequestVo body) {
        Task task = new Task();
        task.setTitle(body.getTitle());
        task.setDescription(body.getDescription());
        task.setPriority(body.getPriority());
        task.setStatus(body.getTaskStatus());
        task.setDueDate(body.getDueDate());

        taskService.createTask(task);
        return ResponseEntity.ok("Task created successfully");
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false, name = "status") String status,
            @RequestParam(required = false, name = "priority") String priority,
            @RequestParam(required = false, name = "dueBefore") LocalDate dueBefore,
            @RequestParam(required = false, name = "sortField") String sortField,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {

        List<Task> tasks = taskService.getAllTasks(status, priority, dueBefore, sortField, page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/count")
    public ResponseEntity<Map<Status, Long>> getTaskCountByStatus() {
        Map<Status, Long> countByStatus = taskService.getTaskCountByStatus();
        return ResponseEntity.ok(countByStatus);
    }

    @GetMapping("/tasks/search")
    public ResponseEntity<List<Task>> searchTasksByTitle(@RequestParam String title) {
        List<Task> tasks = taskService.searchTasksByTitle(title);
        return ResponseEntity.ok(tasks);
    }
}
