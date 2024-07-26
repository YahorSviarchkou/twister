package com.twister.service;

import com.twister.entity.Client;
import com.twister.repository.ClientRepository;
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
public class ClientService {

    ClientRepository clientRepository;

    public List<Client> findAllClients(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var clients = clientRepository.findAll(pageRequest);

        return clients.isEmpty()
                ? Collections.emptyList()
                : clients.toList();
    }

    public Optional<Client> findClientById(Long id) {
        return clientRepository.findById(id);
    }

    public boolean isClientExists(Long id) {
        return clientRepository.existsById(id);
    }

    public Client createClient(Client client) {
        if (Objects.isNull(client.getId())) {
            var saved = clientRepository.save(client);
            log.info("Created client with id: {}, name: {}", saved.getId(), saved.getFullName());
        }
        log.error("Can't create a client with non-null id");
        throw new IllegalStateException("Can't create a client with non-null id");
    }

    public void deleteClient(Long id) {
        log.info("Delete client by id: {}", id);
        clientRepository.deleteById(id);
        log.info("Client with id: {} was deleted", id);
    }
}
