package sample.cafekiosk.spring.api.service.product.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@NoArgsConstructor
public class ProductCreateServiceRequest {

    private ProductType productType;
    private ProductStatus productStatus;
    private String productName;
    private int productPrice;

    @Builder
    private ProductCreateServiceRequest(ProductType productType, ProductStatus productStatus, String productName, int productPrice) {
        this.productType = productType;
        this.productStatus = productStatus;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public Product toEntity(String nextProductNo) {
        return Product.builder()
                .productNo(nextProductNo)
                .productType(productType)
                .productStatus(productStatus)
                .productName(productName)
                .productPrice(productPrice)
                .build();
    }
}
