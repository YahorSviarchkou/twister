package com.twister.service.reference;

import com.twister.domain.reference.TransportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransportTypeService extends ReferenceService<TransportType> {

    @Override
    protected Class<TransportType> getReferenceClass() {
        return TransportType.class;
    }
}
