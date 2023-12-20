package com.suraj.orderservice.service;

import com.suraj.orderservice.dto.InventoryResponse;
import com.suraj.orderservice.dto.OrderLineItemsDto;
import com.suraj.orderservice.dto.OrderRequest;
import com.suraj.orderservice.model.Order;
import com.suraj.orderservice.model.OrderLineItems;
import com.suraj.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;

	private final WebClient webClient;

	@Transactional
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
				.stream()
				.map(this::mapToEntity)
				.toList();

		order.setOrderLineItemsList(orderLineItems);

		List<String> skuCodes = order.getOrderLineItemsList().stream()
				.map(OrderLineItems::getSkuCode).toList();

		// Call Inventory Service and place order if product is in stock
		InventoryResponse[] inventoryResponseArray = webClient.get()
				.uri("http://localhost:8082/api/inventory",
						uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
				.retrieve()
				.bodyToMono(InventoryResponse[].class)
				.block();

		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Product is not in stock.!!");
		}

	}

	private OrderLineItems mapToEntity(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		return orderLineItems;
	}
}
