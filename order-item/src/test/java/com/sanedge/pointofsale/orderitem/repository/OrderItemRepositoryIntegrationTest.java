package com.sanedge.pointofsale.orderitem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.pointofsale.BaseIntegrationTest;
import com.sanedge.pointofsale.orderitem.models.OrderItem;
import com.sanedge.pointofsale.orderitem.repository.OrderItemRepository;

public class OrderItemRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void shouldFindAllAndSaveOrderItem() {
        OrderItem item = new OrderItem();
        item.setOrderId(1);
        item.setProductId(11);
        item.setQuantity(5);
        item.setPrice(100);
        item = orderItemRepository.save(item);

        List<OrderItem> items = orderItemRepository.findOrderItemByOrder(1);
        assertThat(items).isNotEmpty();
        
        orderItemRepository.delete(item);

        assertThat(orderItemRepository.findById(item.getOrderItemId())).isEmpty();
    }
}
