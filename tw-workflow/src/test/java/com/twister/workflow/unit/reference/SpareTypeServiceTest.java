package com.twister.workflow.unit.reference;

import com.twister.domain.reference.SpareType;
import com.twister.workflow.service.reference.SpareTypeService;

public class SpareTypeServiceTest extends BaseReferenceServiceTest<SpareType, SpareTypeService> {

    @Override
    protected SpareTypeService createService() {
        return new SpareTypeService();
    }

    @Override
    protected SpareType createDomain(Long id, String name) {
        return SpareType.builder().id(id).name(name).build();
    }
}
