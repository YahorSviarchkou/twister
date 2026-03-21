package com.twister.workflow.unit.reference;

import com.twister.domain.reference.SpareBrand;
import com.twister.workflow.service.reference.SpareBrandService;

public class SpareBrandServiceTest extends BaseReferenceServiceTest<SpareBrand, SpareBrandService> {

    @Override
    protected SpareBrandService createService() {
        return new SpareBrandService();
    }

    @Override
    protected SpareBrand createDomain(Long id, String name) {
        return SpareBrand.builder().id(id).name(name).build();
    }
}
