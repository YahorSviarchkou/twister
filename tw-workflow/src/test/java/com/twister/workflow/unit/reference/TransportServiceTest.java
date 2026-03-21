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
import com.twister.domain.reference.Transport;
import com.twister.domain.reference.TransportBrand;
import com.twister.domain.reference.TransportType;
import com.twister.payload.TransportFilter;
import com.twister.workflow.dao.reference.TransportDao;
import com.twister.workflow.service.reference.CountryService;
import com.twister.workflow.service.reference.TransportBrandService;
import com.twister.workflow.service.reference.TransportService;
import com.twister.workflow.service.reference.TransportTypeService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
public class TransportServiceTest {

    private final Long ID = 1L;
    private final String NAME = "TEST";

    @Mock
    private TransportDao transportDao;

    @Mock
    private CountryService countryService;

    @Mock
    private TransportTypeService transportTypeService;

    @Mock
    private TransportBrandService transportBrandService;

    @InjectMocks
    private TransportService transportService;

    @Captor
    private ArgumentCaptor<Transport> transportCaptor;

    @Test
    void testGetTransportByIdWhenIdExists() {
        // Given
        Transport transport = Transport.builder().id(ID).build();
        // When
        when(transportDao.findById(anyLong())).thenReturn(Optional.of(transport));
        // Then
        Transport result = transportService.getTransportById(ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());

        verify(transportDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetTransportByIdWhenIdNotExists() {
        // When
        when(transportDao.findById(anyLong())).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> transportService.getTransportById(ID));

        verify(transportDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetAllTransportWithFilterWhenDataExists() {
        // Given
        Transport transport = Transport.builder().id(ID).build();
        Page<Transport> transportPage = new PageImpl<>(List.of(transport));
        // When
        when(transportDao.findAll(any(TransportFilter.class), any(Pageable.class)))
                .thenReturn(transportPage);
        // Then
        Page<Transport> resultPage =
                transportService.getAllTransport(TransportFilter.builder().build(), Pageable.unpaged());

        assertNotNull(resultPage);
        Optional<Transport> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());

        verify(transportDao, times(1)).findAll(any(TransportFilter.class), any(Pageable.class));
    }

    @Test
    void testGetAllTransportWithFilterWhenDataNotExists() {
        // When
        when(transportDao.findAll(any(TransportFilter.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        // Then
        Page<Transport> resultPage =
                transportService.getAllTransport(TransportFilter.builder().build(), Pageable.unpaged());

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(transportDao, times(1)).findAll(any(TransportFilter.class), any(Pageable.class));
    }

    @Test
    void testGetAllTransportWhenDataExists() {
        // Given
        Transport transport = Transport.builder().id(ID).build();
        Page<Transport> transportPage = new PageImpl<>(List.of(transport));
        // When
        when(transportDao.findAll(any(Pageable.class))).thenReturn(transportPage);
        // Then
        Page<Transport> resultPage = transportService.getAllTransport(Pageable.unpaged());

        assertNotNull(resultPage);
        Optional<Transport> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());

        verify(transportDao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllTransportWhenDataNotExists() {
        // When
        when(transportDao.findAll(any(Pageable.class))).thenReturn(Page.empty());
        // Then
        Page<Transport> resultPage = transportService.getAllTransport(Pageable.unpaged());

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(transportDao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testCreateTransportWhenValidData() {
        // Given
        Transport creating = Transport.builder().name(NAME).build();
        Transport saved = Transport.builder().id(ID).name(NAME).build();
        // When
        when(transportDao.save(creating)).thenReturn(saved);
        // Then
        Transport result = transportService.create(creating);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());

        verify(transportDao, times(1)).findByName(NAME);
        verify(transportDao, times(1)).save(creating);
    }

    @Test
    void testCreateTransportWhenInputDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> transportService.create(null));

        verify(transportDao, times(0)).save(any());
    }

    @Test
    void testCreateTransportWhenNameAlreadyExists() {
        // Given
        Transport creating = Transport.builder().name(NAME).build();
        Transport existed = Transport.builder().id(ID).name(NAME).build();
        // When
        when(transportDao.findByName(NAME)).thenReturn(Optional.of(existed));
        // Then
        assertThrows(IllegalArgumentException.class, () -> transportService.create(creating));

        verify(transportDao, times(1)).findByName(NAME);
        verify(transportDao, times(0)).save(any());
    }

    @Test
    void testUpdateTransportWhenValidData() {
        // Given
        int year = 2026;
        String url = "www.transport.com";
        String sku = "xxyyzz";
        String model = "STELS";
        String description = "some description";
        Country country = Country.builder().id(ID).build();
        TransportType type = TransportType.builder().id(ID).build();
        TransportBrand brand = TransportBrand.builder().id(ID).build();
        Transport updating = Transport.builder()
                .id(ID)
                .name(NAME)
                .country(country)
                .description(description)
                .issueYear(year)
                .model(model)
                .sku(sku)
                .type(type)
                .brand(brand)
                .url(url)
                .build();
        Transport existed = Transport.builder().id(ID).name("EXISTED").build();
        // When
        when(transportDao.findById(ID)).thenReturn(Optional.of(existed));
        when(transportTypeService.getById(ID)).thenReturn(type);
        when(transportBrandService.getById(ID)).thenReturn(brand);
        when(countryService.getById(ID)).thenReturn(country);
        // Then
        transportService.update(updating);

        verify(transportDao, times(1)).save(transportCaptor.capture());
        Transport result = transportCaptor.getValue();

        assertEquals(ID, result.getId());
        assertEquals(sku, result.getSku());
        assertEquals(url, result.getUrl());
        assertEquals(NAME, result.getName());
        assertEquals(type, result.getType());
        assertEquals(brand, result.getBrand());
        assertEquals(model, result.getModel());
        assertEquals(year, result.getIssueYear());
        assertEquals(country, result.getCountry());
        assertEquals(description, result.getDescription());

        verify(transportDao, times(1)).findById(ID);
        verify(transportTypeService, times(1)).getById(ID);
        verify(transportBrandService, times(1)).getById(ID);
    }

    @Test
    void testUpdateTransportWhenInputDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> transportService.update(null));

        verify(transportDao, times(0)).save(any());
    }

    @Test
    void testUpdateTransportWhenIdIsNull() {
        // Given
        Transport transport = Transport.builder().build();
        // Then
        assertThrows(IllegalArgumentException.class, () -> transportService.update(transport));

        verify(transportDao, times(0)).save(any());
    }

    @Test
    void testUpdateSpareWhenNameAlreadyExists() {
        // Given
        Transport updating = Transport.builder().id(ID).name(NAME).build();
        Transport existed = Transport.builder().id(ID).name(NAME).build();
        // When
        when(transportDao.findById(ID)).thenReturn(Optional.of(existed));
        when(transportDao.findByName(NAME)).thenReturn(Optional.of(existed));
        // Then
        assertThrows(IllegalArgumentException.class, () -> transportService.update(updating));

        verify(transportDao, times(1)).findByName(NAME);
        verify(transportDao, times(0)).save(any());
    }
}
