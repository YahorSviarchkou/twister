package com.twister.service;

import com.twister.entity.Task;
import com.twister.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;

    public List<Task> findAllTasks(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var tasks = taskRepository.findAll(pageRequest);

        return tasks.isEmpty()
                ? Collections.emptyList()
                : tasks.toList();
    }

    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public boolean isTaskExists(Long id) {
        return taskRepository.existsById(id);
    }

    public Task createTask(Task task) {
        if (Objects.isNull(task.getId())) {
            var saved = taskRepository.save(task);
//            log.info("Created task with id: {}, title: {}", saved.getId(), saved.get());
        }
        log.error("Can't create a task with non-null id");
        throw new IllegalStateException("Can't create a task with non-null id");
    }
}
