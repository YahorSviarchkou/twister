package com.twister.workflow.service.reference;

import com.twister.domain.reference.Reference;
import com.twister.domain.reference.Spare;
import com.twister.payload.SpareFilter;
import com.twister.workflow.dao.reference.SpareDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpareService {

    private final SpareDao spareDao;
    private final SpareBrandService spareBrandService;
    private final SpareTypeService spareTypeService;
    private final CountryService countryService;
    private final TransportTypeService transportTypeService;

    public Spare getSpareById(Long id) {
        return spareDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Spare not found by id: " + id));
    }

    public Page<Spare> getAllSpares(SpareFilter filter, Pageable pageable) {
        return spareDao.findAll(filter, pageable);
    }

    public Page<Spare> getAllSpares(Pageable pageable) {
        return spareDao.findAll(pageable);
    }

    public List<Spare> getAllByIds(Set<Long> ids) {
        List<Spare> spares = spareDao.findByIdIn(ids);
        if (spares.isEmpty()) {
            throw new NoSuchElementException("No spares found for ids: " + ids);
        }

        if (spares.size() != ids.size()) {
            List<Long> actualIds = spares.stream().map(Reference::getId).collect(Collectors.toList());
            actualIds.removeAll(ids);
            log.warn("No spares found for ids: {}", actualIds);
        }

        return spares;
    }

    public void create(Spare spare) {
        if (spare == null) {
            throw new IllegalArgumentException("Spare not exist for creating");
        }

        log.info("Creating spare");
        updateNotNull(spare, spare);

        spare.setId(null);
        Spare dbSpare = spareDao.save(spare);
        log.info("Spare successfully created with id: {}", dbSpare.getId());
    }

    public void update(Spare spare) {
        if (spare == null || spare.getId() == null) {
            throw new IllegalArgumentException("Spare not exist for updating");
        }

        log.info("Updating spare with id: {}", spare.getId());
        Spare dbSpare = getSpareById(spare.getId());
        updateNotNull(spare, dbSpare);

        spareDao.save(dbSpare);
        log.info("Spare with id: {}, successfully updated", spare.getId());
    }

    public void addToWarehouse(int quantity, Long spareId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity of spares must be greater then 0");
        }

        log.info("Adding {} spares[id: {}] to warehouse", quantity, spareId);
        updateWarehouseQuantity(quantity, spareId);
    }

    public void takeFromWarehouse(int quantity, Long spareId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity of spares must be greater then 0");
        }

        log.info("Taking {} spares[id: {}] from warehouse", quantity, spareId);
        updateWarehouseQuantity(quantity * -1, spareId);
    }

    private void updateWarehouseQuantity(int quantity, Long spareId) {
        Spare spare = getSpareById(spareId);
        log.info("Current spares[id: {}] quantity in warehouse: {}", spareId, spare.getWarehouseQuantity());

        if (0 > spare.getWarehouseQuantity() + quantity) {
            throw new IllegalArgumentException("Not enough spares in warehouse");
        }

        spare.setWarehouseQuantity(spare.getWarehouseQuantity() - quantity);
        Spare savedSpare = spareDao.save(spare);
        log.info("Spares[id: {}] warehouse quantity updated to {}",
            savedSpare.getId(), savedSpare.getWarehouseQuantity()
        );
    }

    private void updateNotNull(Spare source, Spare target) {
        Optional.ofNullable(source.getName()).ifPresent(target::setName);
        Optional.ofNullable(source.getModel()).ifPresent(target::setModel);
        Optional.ofNullable(source.getMaterial()).ifPresent(target::setMaterial);
        Optional.ofNullable(source.getDescription()).ifPresent(target::setDescription);
        Optional.ofNullable(source.getPrice()).ifPresent(target::setPrice);
        Optional.ofNullable(source.getSku()).ifPresent(target::setSku);
        Optional.ofNullable(source.getUrl()).ifPresent(target::setUrl);
        Optional.ofNullable(source.getWarehouseQuantity()).ifPresent(target::setWarehouseQuantity);
        Optional.ofNullable(source.getIssueYear()).ifPresent(target::setIssueYear);

        Optional.ofNullable(source.getType())
            .map(spareType -> spareTypeService.getById(spareType.getId()))
            .ifPresent(target::setType);
        Optional.ofNullable(source.getBrand())
            .map(spareBrand -> spareBrandService.getById(spareBrand.getId()))
            .ifPresent(target::setBrand);
        Optional.ofNullable(source.getTransportType())
            .map(transportType -> transportTypeService.getById(transportType.getId()))
            .ifPresent(target::setTransportType);
        Optional.ofNullable(source.getCountry())
            .map(country -> countryService.getById(country.getId()))
            .ifPresent(target::setCountry);
    }
}
