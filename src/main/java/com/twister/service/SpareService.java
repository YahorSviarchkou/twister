package com.twister.service;

import com.twister.entity.Spare;
import com.twister.repository.SpareRepository;
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
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpareService {

    SpareRepository spareRepository;

    public List<Spare> findAllSpares(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var spares = spareRepository.findAll(pageRequest);

        return spares.isEmpty()
                ? Collections.emptyList()
                : spares.toList();
    }

    public Optional<Spare> findSpareById(Long id) {
        return spareRepository.findById(id);
    }

    public Set<Spare> findSparesByIds(Set<Long> ids) {
        return spareRepository.findAllSparesByIdIn(ids);
    }

    public Spare createSpare(Spare spare) {
        if (spareRepository.existsByTitle(spare.getTitle())) {
            throw new IllegalStateException("Spare name already exists");
        }

        if (Objects.isNull(spare.getId())) {
            var saved = spareRepository.save(spare);
            log.info("Created spare with id: {}, title: {}", saved.getId(), saved.getTitle());
            return saved;
        }
        log.error("Can't create a spare with non-null id");
        throw new IllegalStateException("Can't create a spare with non-null id");
    }

    public Spare updateSpare(Spare oldSpare, Spare newSpare) {
        if (!oldSpare.getTitle().equals(newSpare.getTitle())
                && spareRepository.existsByTitle(newSpare.getTitle())) {
            throw new IllegalStateException("Spare name already exists");
        }

        if (Objects.nonNull(oldSpare.getId())) {
            newSpare.setId(oldSpare.getId());

            var saved = spareRepository.save(newSpare);
            log.info("Updated spare with id: {}, title: {}", saved.getId(), saved.getTitle());
            return saved;
        }
        log.error("Can't update a spare with non-null id");
        throw new IllegalStateException("Can't update a spare with non-null id");
    }


    public void deleteSpare(Long id) {
        log.info("Delete spare by id: {}", id);
        spareRepository.deleteById(id);
        log.info("Spare with id: {} was deleted", id);
    }
}
