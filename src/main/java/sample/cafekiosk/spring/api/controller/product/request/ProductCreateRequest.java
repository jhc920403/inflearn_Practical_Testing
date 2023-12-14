package sample.cafekiosk.spring.api.controller.product.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "상품 타입은 필수입니다.")
    private ProductType productType;
    @NotNull(message = "상품 판매상태는 필수입니다.")
    private ProductStatus productStatus;
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;
    @Positive(message = "상품 가격은 양수여야합니다.")
    private int productPrice;

    @Builder
    private ProductCreateRequest(ProductType productType, ProductStatus productStatus, String productName, int productPrice) {
        this.productType = productType;
        this.productStatus = productStatus;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                .productType(this.productType)
                .productStatus(this.productStatus)
                .productName(this.productName)
                .productPrice(this.productPrice)
                .build();
    }
}
