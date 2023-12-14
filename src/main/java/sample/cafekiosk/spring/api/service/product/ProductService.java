package sample.cafekiosk.spring.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;
import sample.cafekiosk.spring.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 동시성 이슈도 생각해 볼 필요가 있다.
     * - productNo를 unique 조건을 설정하여, 동시에 요청되어 이슈가 발생한 경우 저장할 때 데이터베이스에서 오류를 반환하도록 진행 할 수 있다.
     * - 동시성에 대한 민감한 경우 Application 단에서 UUID를 설정하여 ProductNo로 구성할 수 있다.
     */
    public ProductResponse createProduct(ProductCreateServiceRequest request) {

        // productNo
        // 001 / 002 / 003 / 004
        // DB에서 마지막 저장된 Product의 상품번호를 읽어서 +1
        String nextProductNo = createNextProductNo();

        Product product = request.toEntity(nextProductNo);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    private String createNextProductNo() {
        String latestProductNo = productRepository.findLatestProductNo();
        if (latestProductNo == null) {
            return "001";
        }

        int nextProductNo = Integer.parseInt(latestProductNo) + 1;
        return String.format("%03d", nextProductNo);
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllByProductStatusIn(ProductStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }
}
