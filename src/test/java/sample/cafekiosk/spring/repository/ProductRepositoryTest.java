package sample.cafekiosk.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;
import static sample.cafekiosk.spring.domain.product.ProductStatus.*;

/**
 * Junit Repository Test
 * - 단위 테스트 성격을 가지고 있는 테스트이다.
 *
 * Persistence Layer Test
 * - Data Access의 역할을 수행한다.
 * - 비즈니스 가공 로직이 포함 되면 안된다. Data에 대한 CRUD에만 집중한 레이어다.
 *
 * @SpringBootTest
 * - @SpringBootTest 내부에는 @Transactional 이 적용되어 있지 않다.
 * - Test Class는 데이터베이스에 저장한 데이터가 테스트 종료 후 롤백되지 않는다.
 * - @SpringBootTest 에서도 테스트 진행한 데이터를 롤백하고 싶은 경우 @Transactional을 별도로 붙여줘야한다.
 *
 * @DataJpaTest
 * - JPA 관련 Bean 기능만 구동하여 테스트를 진행하게 된다.
 * - Test Class 내 테스트 메서드 종료될 때마다 데이터베이스에 저장한 데이터를 롤백이 가능하다.
 *
 * @ActiveProfiles
 * - 테스트 진행시 설정파일의 Test 설정으로 동작한다.
 *
 * @Transactional
 * - `readOnly = true` 옵션을 부여하는 경우 읽기 전용으로 사용된다.
 * - CRUD에서 CUD 동작 X / Only Read
 * - JPA : CUD 스냅샷 저장, 변경감지를 하지 않기 때문에 보다 성능 향상을 얻을 수 있다.
 * - CQRS : Command와 Query를 분리할 수 있다.
 *   - Database 구성 시 Read Replica를 사용하게 되면, @Transactional 설정에 따라 분기 처리 가능하도록 할 수 있다.
 *     (참고 : https://velog.io/@juhyeon1114/JPA-Read-replica%EC%99%80-Write-replica%EB%A1%9C-%EB%B6%84%EC%82%B0-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0)
 *
 * Business Layer
 * - 비즈니스 로직을 구현하는 역할을 수행한다.
 * - Persistence Layer와 상호작용(Data를 읽고 쓰는 행위)을 통해 비즈니스 로직을 전개시킨다.
 * - 트랜잭션을 보장(원자성)해야 한다.
 */

@Transactional
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("원하는 판매 상태를 가진 상품을 조회한다.")
    void findAllBySellingStatusIn() {
        // Given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        // When
        List<Product> products = productRepository.findAllByProductStatusIn(List.of(SELLING, HOLD));


        // Then
        assertThat(products)
                .hasSize(2)
                .extracting("productNo", "productName", "productStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );
    }

    @Test
    @DisplayName("상품번호 리스트로 상품들을 조회한다.")
    void findAllByProductNoIn() {
        // Given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = createProduct("003", HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        // When
        List<Product> products = productRepository.findAllByProductNoIn(List.of("001", "002"));

        // Then
        assertThat(products)
                .hasSize(2)
                .extracting("productNo", "productName", "productStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호로 읽어온다.")
    void findLatestProductNo() {
        String targetProductNo = "003";

        // Given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4000);
        Product product3 = createProduct(targetProductNo, HANDMADE, SOLD_OUT, "팥빙수", 7000);

        productRepository.saveAll(List.of(product1, product2, product3));

        // When
        String latestProductNo = productRepository.findLatestProductNo();

        // Then
        assertThat(latestProductNo).isEqualTo(targetProductNo);
    }

    @Test
    @DisplayName("가장 마지막으로 저장한 상품의 상품번호로 읽어올 때, 상품이 하나도 없는 경우에는 Null을 반환한다.")
    void findLatestProductNoWhenProductIsEmpty() {
        // When
        String latestProductNo = productRepository.findLatestProductNo();

        // Then
        assertThat(latestProductNo).isNull();
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