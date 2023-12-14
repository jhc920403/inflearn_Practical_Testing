package sample.cafekiosk.unit;

import sample.cafekiosk.unit.drink.Beverage;
import sample.cafekiosk.unit.drink.BeverageProduct;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 요구사항
 * 1. 주문 목록에 음료 추가 / 제거 기능
 * 2. 주문 목록 전체 제거
 * 3. 주문 목록 총 금액 산정하기
 * 4. 주문 생성하기
 *  -> 가게 운영 시간 : 10시 ~ 22시 외에는 주문 생성 불가
 *
 * 테스트 세분화 고려 요소
 * 1. 암묵적이거나 아직 드러나지 않은 요구사항이 존재하는가?
 *   -> 해피 케이스 : 요청된 요구사항
 *   -> 예외 케이스 : 암묵적으로 이야기되지 않은 요구사항
 *
 *   >> 경계값 테스트 중요 (범위(이상, 이하, 초과, 미만), 구간, 날짜 등)
 *
 * 테스트하기 쉬운 영역이란?
 * 1. 같은 입력에는 항상 같은 결과로 응답
 * 2. 외부 세계와 단절된 형태
 *
 * 테스트하기 어려운 영역이란?
 * 1. 관측할 때마다 다른 값에 의존하는 코드
 *   - 현재 날짜 / 시간, 랜덤 값, 전역변수 / 함수, 사용자 입력 등
 * 2. 외부 세계에 영향을 주는 코드
 *   - 표준 출력, 메시지 발송, 데이터베이스에 기록하기 등
 *
 * TDD란? (Test Driven Development)
 * - 프로덕션 코드보다 테스트 코드를 먼저 작성하여 테스트 구현과정을 주도하도록 하는 방법이다.
 *
 * TDD 작성 방법
 * - RED : 실패하는 테스트를 작성한다. (구현부가 없기 때문에 실패하는 테스트이다)
 * - GREEN : 테스트를 통과하기 위한 최소한의 코딩을 한다.
 * - REFACTOR : 테스트 통과를 유지하면서 코드 리팩토링을 수행한다.
 * 
 * 선 기능 구현, 후 테스트 작성의 문제
 * - 테스트 자체의 누락 가능성
 * - 특정 테스트 케이스만 검증할 가능성
 * - 잘못된 구현을 다소 늦게 발견할 가능성
 *
 *
 * !!테스트는 [문서]이다
 * - 프로덕션 기능을 설명하는 테스트 코드 문서이다.
 * - 다양한 테스트 케이스를 통해 프로덕션 코드를 이해하는 시각과 관점을 보완한다.
 * - 어느 한 사람이 과거에 경험한 고민과 결과물을 팀 차원으로 승격시켜서, 모두의 자산으로 공유할 수 있다.
 *
 * > @DisplayName
 *  - 테스트에 대한 문장형태로 세부적인 설명을 할 수 있으며, 테스트에 대한 행위 결과까지 기술하면 좋다.
 *  - 테스트 진행시 테스트 목록에 @DisplayName 에 기입된 내용으로 출력된다.
 *  - 설명시 추상적인 단어는 최대한 자제하고, 도메인 용어를 사용하여 작성하면 좋다.
 *  - 테스트의 현상을 중점으로 기술하지 말아야 한다. ex) 테스트가 성공한다 / 실패한다
 *
 * BDD (Behavior Driven Development)
 * - TDD에서 파생된 개발방법이다.
 * - 메서드(함수) 단위의 테스트에 집중하기 보다, 시나리오 기반한 테스트케이스(TC) 자체에 집중하여 테스트한다.
 * - 개발자가 아닌 사람이 봐도 이해할 수 있을 정도의 추상화 수준(Level)을 권장한다.
 *
 * >> Given / When / Then
 *  - Given : 시나리오 진행에 필요한 모든 과정을 준비한다. (객체, 값, 상태 등) 즉, 어떤 환경인지를 작성하게 된다.
 *  - When : 시나리오 행동 진행한다. 즉 어떤 행동을 진행하는 경우인지를 작성한다.
 *  - Then : 시나리오 진행에 대한 결과 명시, 검증한다. 즉 어떤 상태 변화가 일어나는지를 작성한다.
 *
 *
 * 통합 테스트
 * : 단위 테스트 단위인 메서드 또는 클래스가 아닌 다수의 메소드 또는 클래스가 함께 동작하여 생성하는 기능인 경우 통합 테스트가 필요하게 된다.
 * - 여러 모듈이 협력하는 기능을 통합적으로 검증하는 단계이다.
 * - 일반적으로 작은 범위의 단위 테스트만으로는 기능 전체의 신뢰성을 보장할 수 없는 경우가 존재한다.
 * - 풍부한 단위 테스트 & 큰 기능 단위를 검증하는데 통합테스트가 사용된다.
 */
public class CafeKiosk {

    private static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22, 0);

    private final UUID uuid;
    private final Map<String, Beverage> beverages = new HashMap<>();

    public CafeKiosk() {
        this.uuid = UUID.randomUUID();
    }

    public void put(Beverage beverage) {
        if(beverage.getCount() == 0) {
            throw new IllegalArgumentException("음료는 1잔 이상 주문 가능합니다.");
        }

        this.beverages.put(beverage.getName(), beverage);
    }

    public void remove(BeverageProduct beverageProduct) {
        this.beverages.remove(beverageProduct.getBeverageName());
    }

    public void clear() {
        this.beverages.clear();
    }

    public List<String> getBeverageTypeName() {
        return beverages.keySet().stream().collect(Collectors.toList());
    }

    public int getBeverageTypeCount() {
        return this.beverages.size();
    }

    public int calculateTotalPrice() {

        int count = 0;
        for (String beverage : beverages.keySet()) {
            count += (beverages.get(beverage).getPrice() * beverages.get(beverage).getCount());
        }

        return count;
    }

    /**
     * 테스트하기 어려운 영역을 구분하여 분리하기
     * -> createOrder()에서 현재시간(currentDateTime)을 서버 값으로 하드코딩시 시간을 맞춰야되는 어려움이 발생한다.
     *    이를 해결하기 위해서 메서드 내 하드코딩이 아닌 매개변수로 받게 작성하면 된다.
     */
    public Order createOrder() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = currentDateTime.toLocalTime();

        if (currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }

        return new Order(uuid, LocalDateTime.now(), beverages);
    }

    public Order createOrder(LocalDateTime currentDateTime) {
        LocalTime currentTime = currentDateTime.toLocalTime();

        if (currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }

        return new Order(uuid, LocalDateTime.now(), beverages);
    }
}
