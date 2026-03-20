package com.twister.workflow.service.reference;

import com.twister.domain.reference.Reference;
import com.twister.workflow.dao.reference.ReferenceDao;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
public abstract class ReferenceService<T extends Reference> {

    protected ReferenceDao<T> referenceDao;

    protected abstract Class<T> getReferenceClass();

    public void setReferenceDao(@Autowired ReferenceDao<T> referenceDao) {
        this.referenceDao = referenceDao;
    }

    public T getById(Long id) {
        return referenceDao
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("%s not found by id: %s".formatted(getReferenceClassName(), id)));
    }

    public T getByName(String name) {
        return referenceDao
                .findByName(name.toUpperCase())
                .orElseThrow(() -> new NoSuchElementException(
                        "%s not found by name: %s".formatted(getReferenceClassName(), name)));
    }

    public Page<T> getAllContainingName(String name, Pageable pageable) {
        return referenceDao.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<T> getAll(Pageable pageable) {
        return referenceDao.findAll(pageable);
    }

    public List<T> getAllByIds(Set<Long> ids) {
        List<T> refs = referenceDao.findByIdIn(ids);
        if (refs.isEmpty()) {
            throw new NoSuchElementException("No reference found for ids: " + ids);
        }

        if (refs.size() != ids.size()) {
            List<Long> actualIds = refs.stream().map(Reference::getId).collect(Collectors.toList());
            actualIds.removeAll(ids);
            log.warn("No reference found for ids: {}", actualIds);
        }

        return refs;
    }

    public void create(T reference) {
        if (reference == null
                || reference.getName() == null
                || reference.getName().isBlank()) {
            throw new IllegalArgumentException("%s name must be exist".formatted(getReferenceClassName()));
        }

        log.info("Creating {}: {}", getReferenceClassName(), reference.getName());

        Optional<T> existingReference = referenceDao.findByName(reference.getName());
        if (existingReference.isPresent()) {
            throw new IllegalArgumentException(
                    "%s with name: %s, already exists".formatted(getReferenceClassName(), reference.getName()));
        }

        reference.setId(null);
        reference.setName(reference.getName().toUpperCase());
        T savedRefence = referenceDao.save(reference);
        log.info("{}: {}, saved with id: {}", getReferenceClassName(), savedRefence.getName(), savedRefence.getId());
    }

    protected void delete(Long id) {
        log.info("Deleting {} by id: {}", getReferenceClassName(), id);
        referenceDao.deleteById(id);
        log.info("{} with id: {}, was successfully deleted", getReferenceClassName(), id);
    }

    private String getReferenceClassName() {
        return getReferenceClass().getSimpleName();
    }
}
