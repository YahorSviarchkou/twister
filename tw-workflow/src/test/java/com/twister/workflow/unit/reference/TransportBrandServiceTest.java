package com.twister.workflow.unit.reference;

import com.twister.domain.reference.TransportBrand;
import com.twister.workflow.service.reference.TransportBrandService;

public class TransportBrandServiceTest extends BaseReferenceServiceTest<TransportBrand, TransportBrandService> {

    @Override
    protected TransportBrandService createService() {
        return new TransportBrandService();
    }

    @Override
    protected TransportBrand createDomain(Long id, String name) {
        return TransportBrand.builder().id(id).name(name).build();
    }
}
