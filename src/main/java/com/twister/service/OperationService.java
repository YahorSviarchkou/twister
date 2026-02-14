package com.twister.service;

import com.twister.entity.Operation;
import com.twister.repository.OperationRepository;
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
public class OperationService {

    OperationRepository operationRepository;

    public List<Operation> findAllOperations(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var operations = operationRepository.findAll(pageRequest);

        return operations.isEmpty()
                ? Collections.emptyList()
                : operations.toList();
    }

    public Optional<Operation> findOperationById(Long id) {
        return operationRepository.findById(id);
    }

    public boolean isOperationExists(Long id) {
        return operationRepository.existsById(id);
    }

    public Operation createOperation(Operation operation) {
        if (operationRepository.existsByTitle(operation.getTitle())) {
            throw new IllegalStateException("Operation name already exists");
        }

        if (Objects.isNull(operation.getId())) {
            var saved = operationRepository.save(operation);
            log.info("Created operation with id: {}, title: {}", saved.getId(), saved.getTitle());
            return saved;
        }
        log.error("Can't create a operation with non-null id");
        throw new IllegalStateException("Can't create a operation with non-null id");
    }

    public Operation updateOperation(Operation oldOperation, Operation newOperation) {
        if (!oldOperation.getTitle().equals(newOperation.getTitle())
                && operationRepository.existsByTitle(newOperation.getTitle())) {
            throw new IllegalStateException("Operation name already exists");
        }

        if (Objects.nonNull(oldOperation.getId())) {
            newOperation.setId(oldOperation.getId());

            var saved = operationRepository.save(newOperation);
            log.info("Updated operation with id: {}, title: {}", saved.getId(), saved.getTitle());
            return saved;
        }

        log.error("Can't update a operation with non-null id");
        throw new IllegalStateException("Can't update a operation with non-null id");

    }

    public void deleteOperation(Long id) {
        log.info("Delete operation by id: {}", id);
        operationRepository.deleteById(id);
        log.info("Operation with id: {} was deleted", id);
    }
}
