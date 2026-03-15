package com.twister.service;

import com.twister.entity.Order;
import com.twister.viewmodel.ProcessedOrder;
import com.twister.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OperationSparesService operationSparesService;

    public List<Order> findAllOrders(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var orders = orderRepository.findAll(pageRequest);

        return orders.isEmpty()
                ? Collections.emptyList()
                : orders.toList();
    }

    @Deprecated
    public List<ProcessedOrder> findAllProcessedOrders(int pageNumber, int pageSize, Sort sort) {
        var orders = findAllOrders(pageNumber, pageSize, sort);
        return orders.stream()
                .map(order -> {
                    var operationSpares = order.getOpSps();
                    //todo optimize(loop select)
                    var opsString = operationSparesService.operationSparesToString(operationSpares);

                    var processedOrder = new ProcessedOrder();
                    processedOrder.setId(order.getId());
                    processedOrder.setNotes(order.getNotes());
                    processedOrder.setCreated(order.getCreated());
                    processedOrder.setCustomerFullName(order.getCustomer().getFullName());
                    processedOrder.setCustomerPhone(order.getCustomer().getPhone());
                    processedOrder.setOperations(opsString);

                    return processedOrder;
                })
                .toList();
    }

}
