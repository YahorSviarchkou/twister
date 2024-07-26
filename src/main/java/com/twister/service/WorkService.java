package com.twister.service;

import com.twister.entity.Work;
import com.twister.repository.WorkRepository;
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
public class WorkService {

    WorkRepository workRepository;

    public List<Work> findAllWorks(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var works = workRepository.findAll(pageRequest);

        return works.isEmpty()
                ? Collections.emptyList()
                : works.toList();
    }

    public Optional<Work> findWorkById(Long id) {
        return workRepository.findById(id);
    }

    public boolean isWorkExists(Long id) {
        return workRepository.existsById(id);
    }

    public Work createWork(Work work) {
        if (Objects.isNull(work.getId())) {
            var saved = workRepository.save(work);
            log.info("Created work with id: {}, title: {}", saved.getId(), saved.getTitle());
        }
        log.error("Can't create a work with non-null id");
        throw new IllegalStateException("Can't create a work with non-null id");
    }
}
