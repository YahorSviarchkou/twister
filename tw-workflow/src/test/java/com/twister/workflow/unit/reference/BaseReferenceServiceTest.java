package com.twister.workflow.unit.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.reference.Reference;
import com.twister.workflow.dao.reference.ReferenceDao;
import com.twister.workflow.service.reference.ReferenceService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public abstract class BaseReferenceServiceTest<T extends Reference, S extends ReferenceService<T>> {

    protected final Long ID = 1L;
    protected final String NAME = "TEST";

    @Mock
    protected ReferenceDao<T> referenceDao;

    protected S service;

    protected abstract S createService();

    protected abstract T createDomain(Long id, String name);

    @BeforeEach
    void setUp() {
        service = createService();
        ReflectionTestUtils.setField(service, "referenceDao", referenceDao);
    }

    @Test
    void testGetByIdWhenIdExists() {
        // Given
        T domain = createDomain(ID, NAME);
        // When
        when(referenceDao.findById(ID)).thenReturn(Optional.of(domain));
        // Then
        T result = service.getById(ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());

        verify(referenceDao, times(1)).findById(ID);
    }

    @Test
    void testGetByIdWhenIdNotExists() {
        // When
        when(referenceDao.findById(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> service.getById(any()));

        verify(referenceDao, times(1)).findById(any());
    }

    @Test
    void testGetByNameWhenNameExists() {
        // Given
        T domain = createDomain(ID, NAME);
        // When
        when(referenceDao.findByName(NAME)).thenReturn(Optional.of(domain));
        // Then
        T result = service.getByName(NAME);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());

        verify(referenceDao, times(1)).findByName(NAME);
    }

    @Test
    void testGetByNameWhenNameNotExists() {
        // When
        when(referenceDao.findByName(anyString())).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> service.getByName(anyString()));

        verify(referenceDao, times(1)).findByName(anyString());
    }

    @Test
    void testGetAllContainingNameWhenNameExists() {
        // Given
        String searchName = "est";
        Pageable pageable = Pageable.unpaged();
        Page<T> domainPage = new PageImpl<>(List.of(createDomain(ID, NAME)));
        // When
        when(referenceDao.findByNameContainingIgnoreCase(eq(searchName), any(Pageable.class)))
                .thenReturn(domainPage);
        // Then
        Page<T> resultPage = service.getAllContainingName(searchName, pageable);

        assertNotNull(resultPage);
        Optional<T> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());
        assertEquals(NAME, result.get().getName());

        verify(referenceDao, times(1)).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void testGetAllContainingNameWhenNameNotExists() {
        // When
        when(referenceDao.findByNameContainingIgnoreCase(any(), any())).thenReturn(Page.empty());
        // Then
        Page<T> resultPage = service.getAllContainingName(any(), any());

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(referenceDao, times(1)).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void testGetAllWhenDomainExists() {
        // Given
        Page<T> domainPage = new PageImpl<>(List.of(createDomain(ID, NAME)));
        // When
        when(referenceDao.findAll(any())).thenReturn(domainPage);
        // Then
        Page<T> resultPage = service.getAll(any());

        assertNotNull(resultPage);
        Optional<T> result = resultPage.stream().findFirst();

        assertTrue(result.isPresent());
        assertEquals(ID, result.get().getId());
        assertEquals(NAME, result.get().getName());

        verify(referenceDao, times(1)).findAll(any());
    }

    @Test
    void testGetAllWhenDomainNotExists() {
        // Given
        Pageable pageable = Pageable.unpaged();
        // When
        when(referenceDao.findAll(any(Pageable.class))).thenReturn(Page.empty());
        // Then
        Page<T> resultPage = service.getAll(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());

        verify(referenceDao, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllByIdsWhenAllDomainExists() {
        // Given
        List<T> domainList = List.of(createDomain(1L, null), createDomain(2L, null));
        // When
        when(referenceDao.findByIdIn(Set.of(1L, 2L))).thenReturn(domainList);
        // Then
        List<T> resultList = service.getAllByIds(Set.of(1L, 2L));

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(1L, resultList.getFirst().getId());
        assertEquals(2L, resultList.getLast().getId());

        verify(referenceDao, times(1)).findByIdIn(Set.of(1L, 2L));
    }

    @Test
    void testGetAllByIdsWhenNotAllDomainExists() {
        // Given
        List<T> domainList = List.of(createDomain(1L, null));
        // When
        when(referenceDao.findByIdIn(Set.of(1L, 2L))).thenReturn(domainList);
        // Then
        List<T> resultList = service.getAllByIds(Set.of(1L, 2L));

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().getId());

        verify(referenceDao, times(1)).findByIdIn(Set.of(1L, 2L));
    }

    @Test
    void testGetAllByIdsWhenDomainNotExists() {
        // When
        when(referenceDao.findByIdIn(any())).thenReturn(List.of());
        // Then
        assertThrows(NoSuchElementException.class, () -> service.getAllByIds(any()));

        verify(referenceDao, times(1)).findByIdIn(any());
    }

    @Test
    void testCreateReferenceWhenValidData() {
        // Given
        T creating = createDomain(null, NAME);
        T saved = createDomain(ID, NAME);
        // When
        when(referenceDao.findByName(NAME)).thenReturn(Optional.empty());
        when(referenceDao.save(creating)).thenReturn(saved);
        // Then
        T result = service.create(creating);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());

        verify(referenceDao, times(1)).findByName(NAME);
        verify(referenceDao, times(1)).save(creating);
    }

    @Test
    void testCreateReferenceWhenInputDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> service.create(null));

        verify(referenceDao, times(0)).findByName(any());
        verify(referenceDao, times(0)).save(any());
    }

    @Test
    void testCreateReferenceWhenNameIsNull() {
        // Given
        T creating = createDomain(null, null);
        // Then
        assertThrows(IllegalArgumentException.class, () -> service.create(creating));

        verify(referenceDao, times(0)).findByName(any());
        verify(referenceDao, times(0)).save(any());
    }

    @Test
    void testCreateReferenceWhenNameIsBlank() {
        // Given
        T creating = createDomain(null, "");
        // Then
        assertThrows(IllegalArgumentException.class, () -> service.create(creating));

        verify(referenceDao, times(0)).findByName(any());
        verify(referenceDao, times(0)).save(any());
    }

    @Test
    void testCreateReferenceWhenNameAlreadyExist() {
        // Given
        T creating = createDomain(null, NAME);
        // When
        when(referenceDao.findByName(NAME)).thenReturn(Optional.of(createDomain(ID, NAME)));
        // Then
        assertThrows(IllegalArgumentException.class, () -> service.create(creating));

        verify(referenceDao, times(1)).findByName(NAME);
        verify(referenceDao, times(0)).save(any());
    }
}
