package dev.sandeep.simple.task.management.system.service;
import java.util.ArrayList;

import dev.sandeep.simple.task.management.system.model.Priority;
import dev.sandeep.simple.task.management.system.model.Status;
import dev.sandeep.simple.task.management.system.model.Task;
import dev.sandeep.simple.task.management.system.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final EntityManager entityManager;

    public TaskService(TaskRepository taskRepository, EntityManager entityManager) {
        this.taskRepository = taskRepository;
        this.entityManager = entityManager;
    }

    public List<Task> getAllTasks(String status, String priority, LocalDate dueBefore, String sortField, int page, int size) {
        List<Task> tasklist=new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        List<Predicate> predicates = new ArrayList<>();

        if(status != null) {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), Status.valueOf(status));
            predicates.add(predicate);
        }

        if(priority != null) {
            Predicate priority1 = criteriaBuilder.equal(root.get("priority"), Priority.valueOf(priority));
            predicates.add(priority1);
        }

        if(dueBefore != null) {
            Predicate dueDate = criteriaBuilder.equal(root.get("dueDate"), dueBefore);
            predicates.add(dueDate);
        }

        if(sortField != null) {
            query.orderBy(criteriaBuilder.asc(root.get("sortField")));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query)
                            .setFirstResult(page * size)
                            .setMaxResults(size)
                            .getResultList();
    }

    @Transactional
    public void createTask(Task task) {
        entityManager.persist(task);
    }

    public Map<Status, Long> getTaskCountByStatus() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Task> root = query.from(Task.class);
        query.multiselect(root.get("status"), cb.count(root))
                .groupBy(root.get("status"));
        List<Object[]> results = entityManager.createQuery(query).getResultList();
        return results.stream()
                .filter(result -> result[0] != null) // Filter out entries with null keys
                .collect(Collectors.toMap(
                        result -> (Status) result[0],
                        result -> (Long) result[1]
                ));
    }

    public List<Task> searchTasksByTitle(String title) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);
        Predicate predicate = criteriaBuilder.like(root.get("title"), "%" + title + "%");
        query.select(root).where(predicate);
        return entityManager.createQuery(query).getResultList();
    }

}
