package sample.cafekiosk.unit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.drink.Americano;
import sample.cafekiosk.unit.drink.BeverageProduct;
import sample.cafekiosk.unit.drink.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * 단위테스트는 제한된 상황에서의 테스트를 의미한다.
 * -> 즉, 작은 코드 단위를 독립적으로 실행하여 검증하는 테스트를 의미한다.
 * -> 작은 단위란 클래스 또는 메서드를 의미한다.
 *
 * <p>장점</p>
 * - 검증 속도가 빠르고 안정적이다.
 */
class CafeKioskTest {

    @Test
    @DisplayName("키오스키에 담긴 음료의 수")
    void add() {

        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());

        System.out.println(">>> 담긴 음료 : " + cafeKiosk.getBeverageTypeName().get(0));
        System.out.println(">>> 담긴 음료 수 : " + cafeKiosk.getBeverageTypeCount());

        assertEquals("아메리카노", cafeKiosk.getBeverageTypeName().get(0));
        assertEquals(1, cafeKiosk.getBeverageTypeCount());

        assertThat(cafeKiosk.getBeverageTypeName().get(0)).isEqualTo("아메리카노");
        assertThat(cafeKiosk.getBeverageTypeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("키오스크 음료 한 잔 이상 주문")
    void addServeralBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano(2));

        assertThat(cafeKiosk.getBeverageTypeName()).hasSize(1);
        assertThat(cafeKiosk.getBeverageTypeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("키오스크 음료 0잔 주문")
    void addZeroBeverages() {
        CafeKiosk cafeKiosk = new CafeKiosk();

        assertThatThrownBy(() -> {
           cafeKiosk.put(new Americano(0));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문 가능합니다.");
    }

    @Test
    @DisplayName("Americano 음료 삭제")
    void remove() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());
        assertThat(cafeKiosk.getBeverageTypeName()).hasSize(1);

        cafeKiosk.remove(BeverageProduct.AMERICANO);
        assertThat(cafeKiosk.getBeverageTypeName()).isEmpty();
    }

    @Test
    @DisplayName("키오스크 주문 상품 전체 삭제")
    void clear() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());
        cafeKiosk.put(new Latte());

        assertThat(cafeKiosk.getBeverageTypeName()).hasSize(2);

        cafeKiosk.clear();
        assertThat(cafeKiosk.getBeverageTypeName()).isEmpty();
    }

    @Test
    @DisplayName("키오스크 주문 목록에 담긴 상품들의 총 금액을 계산할 수 있다.")
    void calculateTotalPrice() {
        // Given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());
        cafeKiosk.put(new Latte());

        // When
        int totalPrice = cafeKiosk.calculateTotalPrice();

        // Then
        assertThat(totalPrice).isEqualTo(9000);
    }

    @Test
    @Disabled
    @DisplayName("키오스크 카페 운영시간 내 주문1, 현재 시간으로 테스트하면 않되는 예시의 케이스이며 실패가 발생한다.")
    void createOrder1() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());

        Order order = cafeKiosk.createOrder();
        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().keySet()).contains(BeverageProduct.AMERICANO.getBeverageName());
    }

    @Test
    @DisplayName("키오스크 카페 운영시간 내 주문2")
    void createOrder2() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());

        Order order = cafeKiosk.createOrder(LocalDateTime.of(2023, 11, 18, 14, 0));
        assertThat(order.getBeverages()).hasSize(1);
        assertThat(order.getBeverages().keySet()).contains(BeverageProduct.AMERICANO.getBeverageName());
    }

    @Test
    @DisplayName("키오스크 카페 운영시간 외 주문")
    void createOrderOutsideOpenTime() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.put(new Americano());

        assertThatThrownBy(() -> {
            cafeKiosk.createOrder(LocalDateTime.of(2023, 11, 18, 23, 0));
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의하세요.");
    }
}