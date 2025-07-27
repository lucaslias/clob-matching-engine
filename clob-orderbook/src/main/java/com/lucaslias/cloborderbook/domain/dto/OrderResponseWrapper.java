package com.lucaslias.cloborderbook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseWrapper {
    private List<OrderDTO> buyOrders;
    private List<OrderDTO> sellOrders;
}
