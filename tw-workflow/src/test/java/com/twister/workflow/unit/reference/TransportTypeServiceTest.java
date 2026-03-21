package com.twister.workflow.unit.reference;

import com.twister.domain.reference.TransportType;
import com.twister.workflow.service.reference.TransportTypeService;

public class TransportTypeServiceTest extends BaseReferenceServiceTest<TransportType, TransportTypeService> {

    @Override
    protected TransportTypeService createService() {
        return new TransportTypeService();
    }

    @Override
    protected TransportType createDomain(Long id, String name) {
        return TransportType.builder().id(id).name(name).build();
    }
}
