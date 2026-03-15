package com.twister.service.reference;

import com.twister.model.reference.TransportBrand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransportBrandService extends ReferenceService<TransportBrand> {

    @Override
    protected Class<TransportBrand> getReferenceClass() {
        return TransportBrand.class;
    }
}
