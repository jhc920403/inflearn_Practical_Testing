package sample.cafekiosk.spring.api.service.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 상품을 등록한다. 상품번호는 가장 최근 상품의 상품번호에서 1증가한 값이다.")
    void createProduct() {
        // Given
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.save(product1);

        ProductCreateRequest request = ProductCreateRequest.builder()
                .productType(HANDMADE)
                .productStatus(SELLING)
                .productName("카푸치노")
                .productPrice(5000)
                .build();

        // When
        ProductResponse productResponse = productService.createProduct(request.toServiceRequest());

        // Then
        assertThat(productResponse)
                .extracting("productNo", "productType", "productStatus", "productName", "productPrice")
                .contains("002", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting("productNo", "productType", "productStatus", "productName", "productPrice")
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "아메리카노", 4000)
                        , tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001이다.")
    void createProductWhenProductIsEmpty() {
        // Given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .productType(HANDMADE)
                .productStatus(SELLING)
                .productName("카푸치노")
                .productPrice(5000)
                .build();

        // When
        ProductResponse productResponse = productService.createProduct(request.toServiceRequest());

        // Then
        assertThat(productResponse)
                .extracting("productNo", "productType", "productStatus", "productName", "productPrice")
                .contains("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting("productNo", "productType", "productStatus", "productName", "productPrice")
                .contains(tuple("001", HANDMADE, SELLING, "카푸치노", 5000));
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