package dev.sandeep.simple.task.management.system.request;

import dev.sandeep.simple.task.management.system.model.Priority;
import dev.sandeep.simple.task.management.system.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequestVo {
    private long id;
    private String title;
    private String description;
    private Status taskStatus;
    private Priority priority;
    private LocalDate dueDate;
}
