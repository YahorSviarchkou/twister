package com.twister.service.reference;

import com.twister.model.reference.SpareType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpareTypeService extends ReferenceService<SpareType> {

    @Override
    protected Class<SpareType> getReferenceClass() {
        return SpareType.class;
    }
}
