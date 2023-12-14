package sample.cafekiosk.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CafekioskApplication {

    /**
     * 요구사항
     * 1. 키오스크 주문을 위해 상품 리스트 노출 화면 필요 (Persistence Layer Test)
     *   -> 상품 판매 / 보류 / 중지 등 현황 식별 가능하도록 구성
     *   -> 상품 데이터: PRODUCT_ID / PRODUCT_NO / PRODUCT_TYPE / PRODUCT_STATUS / PRODUCT_NAME / PRODUCT_PRICE
     * 2. 상품 주문 생성하기 (Business Layer Test)
     *   -> 상품 번호 리스트를 받아 주문 생성한다.
     *   -> 주문은 주문 상태 / 주문 등록 시간을 가진다.
     *   -> 주문의 총 금액을 계산할 수 있어야 한다.
     * 3. 주문 생성 시 재고 처리
     *   -> 주문 생성 시 재고 확인 및 개수 차감 후 생성하기
     *   -> 재고는 상품번호를 가진다.
     *   -> 재고와 관련 있는 상품 타입은 병 음료, 베이커리이다.
     * 4. 관리자 페이지에서 제공하는 기능 (Presentation Layer Test)
     *   -> 관리자 페이지에서 신규 상품을 등록할 수 있다.
     *   -> 상품명 / 상품 타입 / 판매 상태 / 가격 등을 입력받는다.
     * 5. 주문 통계에 대한 메일 알림 시스템 (Mock Test)
     * 
     * 테스트를 위한 조언
     * - 한 문단에 한 주제
     *   - 분기문과 반복문이 하나의 테스트에 존재하는 경우 다수의 주제가 존재할 가능성이 높아지며, 테스트로 검증하자 하는 목적을 알기 어렵게 한다.
     *   - 케이스 확장이 필요하면 @ParameterizedTest 를 사용하는 것이 좋다.
     * - 테스트 환경을 완벽하게 제어하기
     *   - 시간과 같은 변동성이 존재하는 테스트는 시점의 영향이 크기 때문에, 이를 제어해야한다.
     *     - 또한, 운영 환경이 Local / Dev / QA / prd 별로 다를 수 있기 때문에 완벽한 고정시간으로 통제가 필요할 수 있다.
     *   - 외부시스템은 제어가 불가능한 경우가 많기 때문에 Mock과 같은 설정이 필요하다.
     * - 테스트 환경의 독립성 보장
     *   - `OrderServiceTest` 내 주석으로 to-do 존재하는 위치를 보면 주문 생성 테스트에서 재고 차감 테스트가 존재한다.
     *     - 문제 1) 테스트 코드 가독성이 낮아진다.
     *     - 문제 2) to-do의 stock1.deductQuantity()의 변동이 발생하여 테스트가 실패할 수 있으며,
     *              복잡한 테스트에 포함되어있을 경우 원인 확인 시간이 오래 걸릴 수 있다.
     * - 테스트간 독립성을 보장
     *   - 다수의 테스트 간의 독립성이 보장되어야 한다.
     *   - 공유되는 필드(글로벌 변수)를 다수의 테스트 메서드가 함께 공유하며, 조작되는 형태는 피해야된다.
     * - 한 눈에 들어오는 Test Fixture 구성하기
     *   - Fixture : 고정물, 고정되어있는 물체
     *   - 테스트를 위해 원하는 상태로로 고정시킨 일련의 객체를 의미
     *   - @BeforeAll && @BeforeEach && @AfterEach && AfterAll
     *     - Given 구성을 위해 중복을 제거로 사용할 수도 있지만, 수정에 모든 테스트에 영향을 미치기 때문에 피하는 것이 좋다.
     *     - 사용 환경
     *       1)각 테스트 입장에서 봤을 때, 동작을 몰라도 테스트 내용을 이해하는데 문제가 없는가?
     *       2) 수정해도 모든 테스트에 영향을 주지 않는가?
     *   - 테스트를 위한 Data.sql로 데이터를 사용하여 테스트하는 방향으로 작성하게 된다면 규모가 커질 경우 수정사항이 발생하면 관리포인트가 된다.
     * - Test Fixture 클렌징
     *   - OrderServiceTest 클래스처럼 Test 시 데이터 저장에 대한 동작방식(@DataJpaTest && @SpringBootTest)의 차이점으로 인한 처리방식 고려 필요
     *   - Jpa 사용시 deleteAll()과 deleteAllInBatch()의 동작 방식을 알고 사용하라 (쿼리 최적화 부분)
     * - @ParameterizedTest
     *   - 테스트 시 파라미터 변동이 필요한 경우 제공되는 기능이다.
     *   - ProductTypeTest 테스트의 containsStockType3() 메소드 또는 containsStockType4() 메소드와 같이 작성하여 테스트 할 수 있다.
     * - @DynamicTest
     *   - 특수적인 환경을 설정하여 시나리오 테스트하기 위한 기능을 지원한다.
     *   - StockTest 클래스 파일의 stockDeductionDynamicTest() 메소드와 같이 작성할 수 있다.
     * - 테스트 수행도 비용이다. 환경 통합하기
     *   - 전체 테스트를 수행하게된다면, 테스트가 복잡하고 통합테스트(@SpringBootTest && @ActiveProfiles)로 작성하는 경우 테스트 속도가 느려진다.
     *   - 통합테스트(@SpringBootTest)를 실행하는 경우를 위해 Spring Boot 를 한번만 실행시킬 수 있도록 IntegrationTestSupport 클래스를 작성하여 테스트 클래스에 상속해준다.
     *     (다만, @MockBean과 같은 경우는 Mock 객체를 생성하기 위해 별도로 Spring Boot가 실행된다.)
     *   - @MockBean의 경우 Spring Boot 재실행을 방지하기 위해 IntegrationTestSupport에 작성하여 제공할 수 있다.
     *     (다른 테스트에 영향을 줄 수 있기 때문에 고려해야된다.)
     *   - @DataJpaTest를 사용해야되는 특별한 이유가 없다면, @SpringBootTest로 통일하여 IntegrationTestSupport 상속받아 사용하는 것도 고려해볼 수 있는 방법이다.
     *
     * QnA
     * Q1. private Method의 테스트는 어떻게 하나요?
     * A1. private Method는 테스트를 할 필요가 없다.
     *     공개 API만 테스트 하면 되며, 테스트가 꼭 필요하다 느낀다면 객체를 분리할 시점인가를 고민해야된다.
     * Q2. 테스트에서만 필요한 메서드가 생겼는데 프로덕션 코드에 필요 없다면?
     * A2. 프로덕션 코드에 만들어도 된다. 하지만 보수적으로 접근해야한다.
     */
    public static void main(String[] args) {
        SpringApplication.run(CafekioskApplication.class, args);
    }


    /**
     * 번외 용어
     * - Test Double (https://martinfowler.com/articles/mocksArentStubs.html)
     *   - Dummy : 아무 것도 하지 않는 객체
     *   - Fake : 단순한 형태로 동일한 기능은 수행하지만, 프로덕션에서 사용하기는 부족한 객체 (ex. FakeRepository)
     *   - Stub : 테스트에서 요청한 것에 대해 미리 준비한 결과를 제공하는 객체, 그 외에는 응답하지 않음 (상태 검증의 목적을 가짐)
     *   - Spy : Stub이면서, 호출된 내용을 기록하여 보여줄 수 있는 객체이며, 일부는 실제 객체처럼 동작시키고 일부만 Stubbing 할 수 있다.
     *   - Mock : 행위에 대한 기대를 명세하고, 그에 따라 동작하도록 만들어진 객체 (행위 검증의 목적을 가짐)
     *   
     * Classicist & Mockist
     * - Mockist : 모든 테스트를 Mock 위주로 진행하라
     * - Classicist : 최대한 진짜 객체 위주로 테스트를 진행하라
     *   - Mock으로 진행한 단위 테스트의 결과가 통합되어 동작하는 기능에 정상 동작할 것인가라는 문제가 있다.
     *   - 외부시스템에 대해서 Mock을 사용해야된다.
     *
     * Spring Rest Docs
     * - 테스트 코드를 통한 API 문서 자동화 도구
     * - API 명세를 문서로 만들고 외부에 제공함으로서 협업을 원활하게 한다.
     * - 기본적으로 AsciiDoc을 사용하여 문서를 작성한다.
     * - REST Docs VS Swagger
     *   - REST Docs
     *     - 장점
     *       - 테스트를 통과해야 문서가 만들어진다. (신뢰도가 높다)
     *       - 프로덕션 코드에 비침투적이다.
     *     - 단점
     *       - 코드 양이 많다.
     *       - 설정이 어렵다.
     *   - Swagger
     *     - 장점
     *       - 적용이 쉽다.
     *       - 문서에서 바로 API 호출을 수행해 볼 수 있다.
     *     - 단점
     *       - 프로덕션 코드에 침투적이다.
     *       - 테스트와 무관하기 때문에 실제 동작과 문서가 다를 수 있다.
     *
     *
     * 학습 테스트 (Google guava : https://github.com/google/guava)
     * - 잘 모르는 기능, 라이브러리, 프레임워크를 학습하기 위해 작성하는 테스트
     * - 여러 테스트 케이스를 스스로 정의하고 검증하는 과정을 통해 보다 구체적인 동작과 기능을 학습할 수 있다.
     * - 관련 문서만 읽는 것보다 재미있게 학습할 수 있다.
     */
}
