package com.twister.service.reference;

import com.twister.model.reference.ReferenceEntity;
import com.twister.repository.reference.ReferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class ReferenceService<T extends ReferenceEntity> {

    protected ReferenceRepository<T> repository;

    protected abstract Class<T> getReferenceClass();

    public void setRepository(@Autowired ReferenceRepository<T> repository) {
        this.repository = repository;
    }

    public T getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "%s not found by id: %s".formatted(getReferenceClassName(), id)
                )
            );
    }

    public T getByName(String name) {
        return repository.findByName(name.toUpperCase())
            .orElseThrow(() ->
                new NoSuchElementException(
                    "%s not found by name: %s".formatted(getReferenceClassName(), name)
                )
            );
    }

    public Page<T> getAllContainingName(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<T> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<T> getAllByIds(Set<Long> ids) {
        List<T> refs = repository.findByIdIn(ids);
        if (refs.isEmpty()) {
            throw new NoSuchElementException("No reference found for ids: " + ids);
        }

        if (refs.size() != ids.size()) {
            List<Long> actualIds = refs.stream().map(ReferenceEntity::getId).collect(Collectors.toList());
            actualIds.removeAll(ids);
            log.warn("No reference found for ids: {}", actualIds);
        }

        return refs;
    }

    public void create(T reference) {
        if (reference == null || reference.getName() == null || reference.getName().isBlank()) {
            throw new IllegalArgumentException("%s name must be exist".formatted(getReferenceClassName()));
        }

        log.info("Creating {}: {}", getReferenceClassName(), reference.getName());

        Optional<T> existingReference = repository.findByName(reference.getName());
        if (existingReference.isPresent()) {
            throw new IllegalArgumentException(
                "%s with name: %s, already exists".formatted(getReferenceClassName(), reference.getName())
            );
        }

        reference.setId(null);
        reference.setName(reference.getName().toUpperCase());
        T savedRefence = repository.save(reference);
        log.info("{}: {}, saved with id: {}",
            getReferenceClassName(), savedRefence.getName(), savedRefence.getId()
        );
    }

    protected void delete(Long id) {
        log.info("Deleting {} by id: {}", getReferenceClassName(), id);
        repository.deleteById(id);
        log.info("{} with id: {}, was successfully deleted", getReferenceClassName(), id);
    }

    private String getReferenceClassName() {
        return getReferenceClass().getSimpleName();
    }
}
