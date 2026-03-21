package com.twister.workflow.unit.reference;

import com.twister.domain.reference.ServiceType;
import com.twister.workflow.service.reference.ServiceTypeService;

public class ServiceTypeServiceTest extends BaseReferenceServiceTest<ServiceType, ServiceTypeService> {

    @Override
    protected ServiceTypeService createService() {
        return new ServiceTypeService();
    }

    @Override
    protected ServiceType createDomain(Long id, String name) {
        return ServiceType.builder().id(id).name(name).build();
    }
}
