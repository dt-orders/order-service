package com.ewolff.microservice.order.logic;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ewolff.microservice.order.clients.CatalogClient;
import com.ewolff.microservice.order.clients.CustomerClient;
import com.ewolff.microservice.order.clients.Customer;


@Service
class OrderService {

	private OrderRepository orderRepository;
	private CustomerClient customerClient;
	private CatalogClient itemClient;

     private static final Logger LOGGER = Logger.getLogger( OrderService.class.getName() );

	@Autowired
	private OrderService(OrderRepository orderRepository,
			CustomerClient customerClient, CatalogClient itemClient) {
		super();
		this.orderRepository = orderRepository;
		this.customerClient = customerClient;
		this.itemClient = itemClient;
	}

	public Order order(Order order) {
		if (order.getNumberOfLines() == 0) {
			throw new IllegalArgumentException("No order lines!");
		}
		if (!customerClient.isValidCustomerId(order.getCustomerId())) {
			throw new IllegalArgumentException("Customer does not exist!");
		}
        Customer orderCustomer = customerClient.getOne(order.getCustomerId());
        List<OrderLine> orderLines = order.getOrderLine();
        int total = 0;
        for (OrderLine ol: orderLines){
            total += ol.getCount();
        }

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Customer: ").append(orderCustomer.getName());
        logMessage.append(" ordered ").append(total);
        logMessage.append(" items for a total of: $").append(order.totalPrice(itemClient));
        LOGGER.log(Level.INFO, logMessage.toString());
		return orderRepository.save(order);
	}

	public double getPrice(long orderId) {
		return orderRepository.findById(orderId).get().totalPrice(itemClient);
	}

}
