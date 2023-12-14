package sample.cafekiosk.spring.api.service.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.repository.OrderProductRepository;
import sample.cafekiosk.spring.repository.OrderRepository;
import sample.cafekiosk.spring.repository.ProductRepository;
import sample.cafekiosk.spring.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

/**
 * Service Layer의 통합테스트 진행
 */
class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderService orderService;


    /**
     * Test Class 내에 Test 메서드 별로 데이터를 초기화하여 사용하는 경우 개별 테스트는 통과 할 수있지만, 테스트 메서드 간에 영향이 발생하여 테스트가 실패하는 경우도 발견된다.
     * 이를 해결하기 위해서 @AfterEach와 같이 특정 이벤트 시점에 데이터를 정리해 줄 수 있는 구문을 추가하여 테스트를 진행할 수 있다.
     * java.lang.IllegalStateException: Duplicate key 001 (attempted merging values sample.cafekiosk.spring.domain.product.Product@1d77d9c6 and sample.cafekiosk.spring.domain.product.Product@51eeae0a)
     */
    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    void createOrder() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNos(List.of("001", "002"))
                .build();

        // When
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // Then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 8000);
        assertThat(orderResponse.getProducts())
                .hasSize(2)
                .extracting("productNo", "productPrice")
                .containsExactlyInAnyOrder(
                        tuple("001", 4000)
                        , tuple("002", 4000)
                );
    }

    @Test
    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    void createOrderWithStock() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", BOTTLE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", BAKERY, HOLD, "초코 크로아상", 4000);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);

        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNos(List.of("001", "001", "002", "003"))
                .build();

        // When
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // Then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 19000);
        assertThat(orderResponse.getProducts())
                .hasSize(4)
                .extracting("productNo", "productPrice")
                .containsExactlyInAnyOrder(
                        tuple("001", 4000)
                        , tuple("001", 4000)
                        , tuple("002", 4000)
                        , tuple("003", 7000)
                );

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNo", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0)
                        , tuple("002", 1)
                );
    }

    @Test
    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    void createOrderWithDuplicateProductNumbers() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, SELLING, "카페라떼", 4500);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNos(List.of("001", "001"))
                .build();

        // When
        OrderResponse orderResponse = orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // Then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 8000);
        assertThat(orderResponse.getProducts())
                .hasSize(2)
                .extracting("productNo", "productPrice")
                .containsExactlyInAnyOrder(
                        tuple("001", 4000)
                        , tuple("001", 4000)
                );
    }

    @Test
    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    void createOrderWithNoStock() {
        // Given
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", BOTTLE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", BAKERY, HOLD, "초코 크로아상", 4000);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stock1.deductQuantity(1);  // to-do
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNos(List.of("001", "001", "002", "003"))
                .build();

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(request.toServiceRequest(), registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다.");
    }

    private Product createProduct(
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