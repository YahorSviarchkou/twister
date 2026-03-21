package com.twister.workflow.unit.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.reference.Country;
import com.twister.domain.reference.Spare;
import com.twister.domain.reference.SpareBrand;
import com.twister.domain.reference.SpareType;
import com.twister.domain.reference.TransportType;
import com.twister.payload.SpareFilter;
import com.twister.workflow.dao.reference.SpareDao;
import com.twister.workflow.service.reference.CountryService;
import com.twister.workflow.service.reference.SpareBrandService;
import com.twister.workflow.service.reference.SpareService;
import com.twister.workflow.service.reference.SpareTypeService;
import com.twister.workflow.service.reference.TransportTypeService;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class SpareServiceTest {

    private final Long ID = 1L;
    private final String NAME = "TEST";

    @Mock
    private SpareDao spareDao;

    @Mock
    private SpareBrandService spareBrandService;

    @Mock
    private SpareTypeService spareTypeService;

    @Mock
    private CountryService countryService;

    @Mock
    private TransportTypeService transportTypeService;

    @InjectMocks
    private SpareService spareService;

    @Captor
    private ArgumentCaptor<Spare> spareCaptor;

    @Test
    void testGetSpareByIdWhenIdExists() {
        // Given
        Spare spare = Spare.builder().id(ID).build();
        // When
        when(spareDao.findById(anyLong())).thenReturn(Optional.of(spare));
        // Then
        Spare result = spareService.getSpareById(ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());

        verify(spareDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetSpareByIdWhenIdNotExists() {
        // When
        when(spareDao.findById(anyLong())).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> spareService.getSpareById(ID));

        verify(spareDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllSparesWithFilterWhenDataExists() {
        // Given
        Spare spare = Spare.builder().id(ID).build();
        Page<Spare> sparePage = new PageImpl<>(List.of(spare));
        // When
        when(spareDao.findAll(any(SpareFilter.class), any(Pageable.class))).thenReturn(sparePage);
        // Then
        Page<Spare> resultPage = spareService.getAllSpares(SpareFilter.builder().build(), Pageable.unpaged());

        assertNotNull(resultPage);
        Optional<Spare> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());

        verify(spareDao, times(1)).findAll(any(SpareFilter.class), any(Pageable.class));
    }

    @Test
    void testGetAllSparesWithFilterWhenDataNotExists() {
        // When
        when(spareDao.findAll(any(SpareFilter.class), any(Pageable.class))).thenReturn(Page.empty());
        // Then
        Page<Spare> resultPage = spareService.getAllSpares(SpareFilter.builder().build(), Pageable.unpaged());

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(spareDao, times(1)).findAll(any(SpareFilter.class), any(Pageable.class));
    }

    @Test
    void testGetAllSparesWhenDataExists() {
        // Given
        Spare spare = Spare.builder().id(ID).build();
        Page<Spare> sparePage = new PageImpl<>(List.of(spare));
        // When
        when(spareDao.findAll(any(Pageable.class))).thenReturn(sparePage);
        // Then
        Page<Spare> resultPage = spareService.getAllSpares(Pageable.unpaged());

        assertNotNull(resultPage);
        Optional<Spare> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());

        verify(spareDao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllSparesWhenDataNotExists() {
        // When
        when(spareDao.findAll(any(Pageable.class))).thenReturn(Page.empty());
        // Then
        Page<Spare> resultPage = spareService.getAllSpares(Pageable.unpaged());

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(spareDao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllByIdsWhenNotAllSparesExists() {
        // Given
        Spare spare = Spare.builder().id(ID).build();
        List<Spare> spareList = List.of(spare);
        // When
        when(spareDao.findByIdIn(Set.of(1L, 2L))).thenReturn(spareList);
        // Then
        List<Spare> resultList = spareService.getAllByIds(Set.of(1L, 2L));

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().getId());

        verify(spareDao, times(1)).findByIdIn(Set.of(1L, 2L));
    }

    @Test
    void testGetAllByIdsWhenSparesNotExists() {
        // When
        when(spareDao.findByIdIn(any())).thenReturn(List.of());
        // Then
        assertThrows(NoSuchElementException.class, () -> spareService.getAllByIds(any()));

        verify(spareDao, times(1)).findByIdIn(any());
    }

    @Test
    void testCreateSpareWhenValidData() {
        // Given
        Spare creating = Spare.builder().name(NAME).build();
        Spare saved = Spare.builder().id(ID).name(NAME).build();
        // When
        when(spareDao.save(creating)).thenReturn(saved);
        // Then
        Spare result = spareService.create(creating);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());

        verify(spareDao, times(1)).findByName(NAME);
        verify(spareDao, times(1)).save(creating);
    }

    @Test
    void testCreateSpareWhenInputDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.create(null));

        verify(spareDao, times(0)).save(any());
    }

    @Test
    void testCreateSpareWhenNameAlreadyExists() {
        // Given
        Spare creating = Spare.builder().name(NAME).build();
        Spare existed = Spare.builder().id(ID).name(NAME).build();
        // When
        when(spareDao.findByName(NAME)).thenReturn(Optional.of(existed));
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.create(creating));

        verify(spareDao, times(1)).findByName(NAME);
        verify(spareDao, times(0)).save(any());
    }

    @Test
    void testUpdateSpareWhenValidData() {
        // Given
        int year = 2026;
        String url = "www.spare.com";
        String sku = "xxyyzz";
        String model = "PH100";
        String material = "plastic";
        String description = "some description";
        BigDecimal price = BigDecimal.TEN;
        Country country = Country.builder().id(ID).build();
        SpareType type = SpareType.builder().id(ID).build();
        SpareBrand brand = SpareBrand.builder().id(ID).build();
        TransportType transportType = TransportType.builder().id(ID).build();
        Spare updating = Spare.builder()
                .id(ID)
                .name(NAME)
                .brand(brand)
                .type(type)
                .country(country)
                .description(description)
                .issueYear(year)
                .material(material)
                .model(model)
                .price(price)
                .sku(sku)
                .transportType(transportType)
                .url(url)
                .build();
        Spare existed = Spare.builder().id(ID).name("EXISTED").build();
        // When
        when(spareDao.findById(ID)).thenReturn(Optional.of(existed));
        when(spareTypeService.getById(ID)).thenReturn(type);
        when(spareBrandService.getById(ID)).thenReturn(brand);
        when(transportTypeService.getById(ID)).thenReturn(transportType);
        when(countryService.getById(ID)).thenReturn(country);
        // Then
        spareService.update(updating);

        verify(spareDao, times(1)).save(spareCaptor.capture());
        Spare result = spareCaptor.getValue();

        assertEquals(ID, result.getId());
        assertEquals(sku, result.getSku());
        assertEquals(url, result.getUrl());
        assertEquals(NAME, result.getName());
        assertEquals(type, result.getType());
        assertEquals(brand, result.getBrand());
        assertEquals(model, result.getModel());
        assertEquals(price, result.getPrice());
        assertEquals(year, result.getIssueYear());
        assertEquals(country, result.getCountry());
        assertEquals(material, result.getMaterial());
        assertEquals(description, result.getDescription());
        assertEquals(transportType, result.getTransportType());

        verify(spareDao, times(1)).findById(ID);
        verify(spareTypeService, times(1)).getById(ID);
        verify(spareBrandService, times(1)).getById(ID);
        verify(transportTypeService, times(1)).getById(ID);
    }

    @Test
    void testUpdateSpareWhenInputDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.update(null));

        verify(spareDao, times(0)).save(any());
    }

    @Test
    void testUpdateSpareWhenIdIsNull() {
        // Given
        Spare spare = Spare.builder().build();
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.update(spare));

        verify(spareDao, times(0)).save(any());
    }

    @Test
    void testUpdateSpareWhenNameAlreadyExists() {
        // Given
        Spare updating = Spare.builder().id(ID).name(NAME).build();
        Spare existed = Spare.builder().id(ID).name(NAME).build();
        // When
        when(spareDao.findById(ID)).thenReturn(Optional.of(existed));
        when(spareDao.findByName(NAME)).thenReturn(Optional.of(existed));
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.update(updating));

        verify(spareDao, times(1)).findByName(NAME);
        verify(spareDao, times(0)).save(any());
    }

    @Test
    void testAddToWarehouseWhenValidData() {
        // Given
        Spare spare = Spare.builder().id(ID).warehouseQuantity(1).build();
        // When
        when(spareDao.findById(ID)).thenReturn(Optional.of(spare));
        when(spareDao.save(any(Spare.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        spareService.addToWarehouse(1, ID);

        verify(spareDao, times(1)).save(spareCaptor.capture());
        Spare result = spareCaptor.getValue();

        assertEquals(ID, result.getId());
        assertEquals(2, result.getWarehouseQuantity());

        verify(spareDao, times(1)).findById(ID);
    }

    @Test
    void testAddToWarehouseWhenQuantityEqualZero() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.addToWarehouse(0, ID));

        verify(spareDao, times(0)).findById(anyLong());
        verify(spareDao, times(0)).save(any(Spare.class));
    }

    @Test
    void testAddToWarehouseWhenQuantityLessThenZero() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.addToWarehouse(-1, ID));

        verify(spareDao, times(0)).findById(anyLong());
        verify(spareDao, times(0)).save(any(Spare.class));
    }

    @Test
    void testTakeFromWarehouseWhenValidData() {
        // Given
        Spare spare = Spare.builder().id(ID).warehouseQuantity(1).build();
        // When
        when(spareDao.findById(ID)).thenReturn(Optional.of(spare));
        when(spareDao.save(any(Spare.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        spareService.takeFromWarehouse(1, ID);

        verify(spareDao, times(1)).save(spareCaptor.capture());
        Spare result = spareCaptor.getValue();

        assertEquals(ID, result.getId());
        assertEquals(0, result.getWarehouseQuantity());

        verify(spareDao, times(1)).findById(ID);
    }

    @Test
    void testTakeFromWarehouseWhenQuantityEqualZero() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.takeFromWarehouse(0, ID));

        verify(spareDao, times(0)).findById(anyLong());
        verify(spareDao, times(0)).save(any(Spare.class));
    }

    @Test
    void testTakeFromWarehouseWhenQuantityLessThenZero() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.takeFromWarehouse(-1, ID));

        verify(spareDao, times(0)).findById(anyLong());
        verify(spareDao, times(0)).save(any(Spare.class));
    }

    @Test
    void testTakeFromWarehouseWhenInputQuantityGreaterThenWarehouseQuantity() {
        // Given
        Spare spare = Spare.builder().id(ID).warehouseQuantity(0).build();
        // When
        when(spareDao.findById(ID)).thenReturn(Optional.of(spare));
        // Then
        assertThrows(IllegalArgumentException.class, () -> spareService.takeFromWarehouse(1, ID));

        verify(spareDao, times(1)).findById(ID);
        verify(spareDao, times(0)).save(any(Spare.class));
    }
}
