package com.twister.service;

import com.twister.entity.Operation;
import com.twister.entity.OperationSpares;
import com.twister.entity.Spare;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OperationSparesService {

    SpareService spareService;
    OperationService operationService;

    @Deprecated
    public String operationSparesToString(Set<OperationSpares> operationSparesSet) {
        return operationSparesSet
                .stream()
                .map(this::operationSparesToString)// todo optimize(loop select)
                .collect(Collectors.joining(",\n"));
    }

    public String operationSparesToString(OperationSpares operationSpares) {
        var operation = operationService.findOperationById(operationSpares.getOperationId());
        var spares = spareService.findSparesByIds(operationSpares.getSpareIds());

        var operationTitle = operation
                .map(Operation::getTitle)
                .orElse("error");
        var sparesTitle = spares
                .stream()
                .map(Spare::getTitle)
                .collect(Collectors.joining(",\n\t"));

        return String.format("%s:\n\t%s", operationTitle, sparesTitle);
    }
}
