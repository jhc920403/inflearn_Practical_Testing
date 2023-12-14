package sample.cafekiosk.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class StockRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("상품번호 리스트로 재고를 조회한다.")
    void findAllByProductNoIn() {
        // Given
        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 3);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // When
        List<Stock> stocks = stockRepository.findAllByProductNoIn(List.of("001", "002"));

        // Then
        assertThat(stocks)
                .hasSize(2)
                .extracting("productNo", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 1),
                        tuple("002", 2)
                );
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