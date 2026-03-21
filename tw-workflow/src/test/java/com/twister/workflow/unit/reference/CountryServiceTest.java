package com.twister.workflow.unit.reference;

import com.twister.domain.reference.Country;
import com.twister.workflow.service.reference.CountryService;

public class CountryServiceTest extends BaseReferenceServiceTest<Country, CountryService> {

    @Override
    protected CountryService createService() {
        return new CountryService();
    }

    @Override
    protected Country createDomain(Long id, String name) {
        return Country.builder().id(id).name(name).build();
    }
}
