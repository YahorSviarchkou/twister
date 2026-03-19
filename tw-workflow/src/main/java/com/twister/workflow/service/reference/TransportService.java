package com.twister.workflow.service.reference;

import com.twister.domain.reference.Transport;
import com.twister.payload.TransportFilter;
import com.twister.workflow.dao.reference.TransportDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportDao transportDao;
    private final TransportTypeService transportTypeService;
    private final TransportBrandService transportBrandService;
    private final CountryService countryService;

    public Transport getTransportById(Long id) {
        return transportDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Transport not found by id: " + id));
    }

    public Page<Transport> getAllTransport(TransportFilter filter, Pageable pageable) {
        return transportDao.findAll(filter, pageable);
    }

    public Page<Transport> getAllTransport(Pageable pageable) {
        return transportDao.findAll(pageable);
    }

    public void create(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("Transport not exist for creating");
        }

        log.info("Creating transport");
        updateNotNull(transport, transport);

        transport.setId(null);
        Transport dbTransport = transportDao.save(transport);
        log.info("Transport successfully created with id: {}", dbTransport.getId());
    }

    public void update(Transport transport) {
        if (transport == null || transport.getId() == null) {
            throw new IllegalArgumentException("Transport not exist for updating");
        }

        log.info("Updating transport with id: {}", transport.getId());
        Transport dbTransport = getTransportById(transport.getId());
        updateNotNull(transport, dbTransport);

        transportDao.save(dbTransport);
        log.info("Transport with id: {}, successfully updated", transport.getId());
    }

    private void updateNotNull(Transport source, Transport target) {
        Optional.ofNullable(source.getName()).ifPresent(target::setName);
        Optional.ofNullable(source.getModel()).ifPresent(target::setModel);
        Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
        Optional.ofNullable(source.getSku()).ifPresent(target::setSku);
        Optional.ofNullable(source.getUrl()).ifPresent(target::setUrl);
        Optional.ofNullable(source.getIssueYear()).ifPresent(target::setIssueYear);

        Optional.ofNullable(source.getType())
            .map(transportType -> transportTypeService.getById(transportType.getId()))
            .ifPresent(target::setType);
        Optional.ofNullable(source.getBrand())
            .map(transportBrand -> transportBrandService.getById(transportBrand.getId()))
            .ifPresent(target::setBrand);
        Optional.ofNullable(source.getCountry())
            .map(country -> countryService.getById(country.getId()))
            .ifPresent(target::setCountry);
    }
}
