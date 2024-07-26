package com.twister.service;

import com.twister.entity.Transport;
import com.twister.repository.TransportRepository;
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
public class TransportService {

    TransportRepository transportRepository;

    public List<Transport> findAllTransports(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var transports = transportRepository.findAll(pageRequest);

        return transports.isEmpty()
                ? Collections.emptyList()
                : transports.toList();
    }

    public Optional<Transport> findTransportById(Long id) {
        return transportRepository.findById(id);
    }

    public boolean isTransportExists(Long id) {
        return transportRepository.existsById(id);
    }

    public Transport createTransport(Transport transport) {
        if (Objects.isNull(transport.getId())) {
            var saved = transportRepository.save(transport);
            log.info("Created transport with id: {}, title: {}", saved.getId(), saved.getTitle());
        }
        log.error("Can't create a transport with non-null id");
        throw new IllegalStateException("Can't create a transport with non-null id");
    }

    public void deleteTransport(Long id) {
        log.info("Delete transport by id: {}", id);
        transportRepository.deleteById(id);
        log.info("Transport with id: {} was deleted", id);
    }
}
