package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.repository.OrderRepository;
import sample.cafekiosk.spring.repository.ProductRepository;
import sample.cafekiosk.spring.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /**
     * 재고 감소는 동시성 고민이 필요한 서비스이다.
     * -> 동시에 요청이 들어온 경우 우선순위 충돌 발생 가능성이 존재한다.
     * -> Optimistic lock / pessimistic lock / ... 락에 대한 고민이 필요하다.
     */
    public OrderResponse createOrder(OrderCreateServiceRequest request , LocalDateTime registeredDateTime) {
        List<String> productNos = request.getProductNos();

        // Product
        List<Product> products = findProductsBy(productNos);

        deductStockQuantities(products);

        Order order = Order.create(products, registeredDateTime);
        Order saveOrder = orderRepository.save(order);

        return OrderResponse.of(saveOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        List<String> stockProductNos = extractStockProductNos(products);
        Map<String, Stock> stockMap = createStockMapBy(stockProductNos);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNos);

        // 재고 차감 시도
        for (String stockProductNo : new HashSet<>(stockProductNos)) {
            Stock stock = stockMap.get(stockProductNo);
            int quantity = productCountingMap.get(stockProductNo).intValue();

            if (stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(quantity);
        }
    }

    /**
     * 동일한 상품이 중복으로 주문되었을 경우 처리를 위한 구문이다.
     */
    private List<Product> findProductsBy(List<String> productNos) {
        List<Product> products = productRepository.findAllByProductNoIn(productNos);

        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNo, p -> p));

        return productNos.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    private static List<String> extractStockProductNos(List<Product> products) {
        // 재고 차감 체크가 필요한 상품들 filter
        List<String> stockProductNos = products.stream()
                .filter(product -> ProductType.containsStockType(product.getProductType()))
                .map(Product::getProductNo)
                .collect(Collectors.toList());
        return stockProductNos;
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNos) {
        // 재고 엔티티 조회
        List<Stock> stocks = stockRepository.findAllByProductNoIn(stockProductNos);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNo, s -> s));
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNos) {
        // 상품별 Counting
        Map<String, Long> productCountingMap = stockProductNos.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        return productCountingMap;
    }
}
