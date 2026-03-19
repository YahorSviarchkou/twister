package com.twister.workflow.service.reference;

import com.twister.domain.reference.SpareBrand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpareBrandService extends ReferenceService<SpareBrand> {

    @Override
    protected Class<SpareBrand> getReferenceClass() {
        return SpareBrand.class;
    }
}
