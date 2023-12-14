package sample.cafekiosk.unit.drink;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Junit의 Assertions와 Assertj의 Assertions는 예상 값과 결과 값의 일치 여부를 확인할 수 있도록 지원한다.
 * -> AssertJ의 Assertions에서 지원하는 API의 기능이 풍부하기 때문에 유용하게 사용할 수 있다.
 */
class AmericanoTest {

    @Test
    @DisplayName("Americano의 상품명 일치")
    void getName() {
        Americano americano = new Americano();

        assertEquals(americano.getName(), "아메리카노");
        assertThat(americano.getName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("Americano의 상품 가격 일치")
    void getPirce() {
        Americano americano = new Americano();

        assertThat(americano.getPrice()).isEqualTo(4000);
    }
}