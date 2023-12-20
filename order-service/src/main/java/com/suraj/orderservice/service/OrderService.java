package com.suraj.orderservice.service;

import com.suraj.orderservice.dto.OrderLineItemsDto;
import com.suraj.orderservice.dto.OrderRequest;
import com.suraj.orderservice.model.Order;
import com.suraj.orderservice.model.OrderLineItems;
import com.suraj.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;

	@Transactional
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
				.stream()
				.map(this::mapToDto)
				.toList();

		order.setOrderLineItemsList(orderLineItems);

		orderRepository.save(order);
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		return orderLineItems;
	}
}
