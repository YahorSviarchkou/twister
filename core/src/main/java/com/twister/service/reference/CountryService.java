package com.twister.service.reference;

import com.twister.model.reference.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CountryService extends ReferenceService<Country> {

    @Override
    protected Class<Country> getReferenceClass() {
        return Country.class;
    }
}
