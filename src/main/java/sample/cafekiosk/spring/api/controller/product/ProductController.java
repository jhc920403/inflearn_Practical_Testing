package sample.cafekiosk.spring.api.controller.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.spring.api.ApiResponse;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;

import javax.validation.Valid;
import java.util.List;

/**
 * Presentation Layer
 * - 외부 세계의 요청을 가장 먼저 받는 계층이다.
 * - 파라미터에 대한 최소한의 검증(Validation)을 수행한다.
 *
 * Presentation Layer의 Test는 Business & Persistence Layer는 Mocking으로 대체한다.
 *
 * MockMvc
 * - 동작 시점에 상관 없이 일관적인 테스트 결과를 얻기위해 동일한 데이터를 제공받을 필요성이 있다.
 * - Spring에서는 MockMvc를 통해 Mock(가짜) 객체를 사용하여 MVC 동작을 재현할 수 있도록 테스트 프레임워크를 제공한다.
 *
 * 키워드 정리
 * - Layered Architecture
 *   - 단점으로 Hexagonal Architecture 이 대두되고 있다.
 *   - 단점은 JPA에서 Entity와 도메인 객체가 강한 의존성을 가지게 된다.
 * - Hexagonal Architecture
 *   - 참고: https://engineering.linecorp.com/ko/blog/port-and-adapter-architecture
 * - 동시성 제어
 *   - Optimistic Lock
 *   - Pessimistic Lock
 * - CQRS
 */

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/v1/products/new")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.ok(productService.createProduct(request.toServiceRequest()));
    }

    @GetMapping("/api/v1/products/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {
        return ApiResponse.ok(productService.getSellingProducts());
    }
}
