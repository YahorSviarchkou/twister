package com.twister.service.reference;

import com.twister.domain.reference.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceTypeService extends ReferenceService<ServiceType> {

    @Override
    protected Class<ServiceType> getReferenceClass() {
        return ServiceType.class;
    }
}
