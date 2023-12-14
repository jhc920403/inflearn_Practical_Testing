package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class OrderTest {

    @Test
    @DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
    void calculateTotalPrice() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = order("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = order("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = order("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        List<Product> products = List.of(product1, product2, product3);

        // When
        Order order = Order.create(products, registeredDateTime);

        // Then
        assertThat(order.getTotalPrice()).isEqualTo(15000);
    }

    @Test
    @DisplayName("주문 생성 시 주문 등록 시간을 기록한다.")
    void registeredDateTime() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = order("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = order("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = order("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        List<Product> products = List.of(product1, product2, product3);

        // When
        Order order = Order.create(products, registeredDateTime);

        // Then
        assertThat(order.getRegisteredDateTime()).isEqualTo(registeredDateTime);
    }

    private Product order(
            String productNo
            , ProductType productType
            , ProductStatus productStatus
            , String productName
            , int productPrice
    ) {
        return Product.builder()
                .productNo(productNo)
                .productType(productType)
                .productStatus(productStatus)
                .productName(productName)
                .productPrice(productPrice)
                .build();
    }
}