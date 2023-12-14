package sample.cafekiosk.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductStatus;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //@Query("select p from Product p where p.productStatus in :productStatus")
    List<Product> findAllByProductStatusIn(List<ProductStatus> productStatus);

    List<Product> findAllByProductNoIn(List<String> productNo);

    /**
     * JPA에서 NativeQuery는 Entity에 매핑되어 조회되는 쿼리가 아닌, 데이터베이스로 직접 조회되는 쿼리로 동작한다.
     */
    @Query(value = "select p.product_no from Product p order by p.product_id desc limit 1", nativeQuery = true)
    String findLatestProductNo();
}
