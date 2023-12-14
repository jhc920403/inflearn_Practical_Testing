package sample.cafekiosk.spring.api.service.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.repository.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.*;

/**
 * 순수한 Mockito로만 테스트 구현
 */
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailSendClient mailSendClient;

    /*
     * @Mock 대신 Spy를 사용할 수 있다.
     * @Spy
     * private MailSendClient mailSendClient;
     */

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("메일 전송 테스트")
    void sendMail() {
        // Given
        /*
         * @Mock & @ExtendWith(MockitoExtension.class)을 사용하여 의존성 주입을 해주지 않은 경우에는 아래 주석과 같이 작성할 수 있다.         *
         * MailSendClient mailSendClient = Mockito.mock(MailSendClient.class);
         * MailSendHistoryRepository mailSendHistoryRepository = Mockito.mock(MailSendHistoryRepository.class);
         *
         * @InjectMocks 를 사용하여 의존성 주입을 해주지 않는 경우에는 아래 주석과 같이 작성할 수 있다.
         * @InjectMocks를 참조하여 @Mock 설정이 되어있는 필드에 의존성을 주입해준다.
         * MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository);
         *
         * @Spy를 사용한 경우 실제 객체를 사용하여 테스트 객체를 만들기 때문에 Mockito.when()을 사용할 수 없어 아래 주석과 같이 작성할 수 있다.
         * @Mock에서는 실질적으로 로깅처리가 되지 않지만, @Spy에서는 실 객체를 이용하여 테스트하기 때문에 로그도 출력이 가능하다.
         * doReturn(true)
         *         .when(mailSendClient)
         *         .sendEmail(anyString(), anyString(), anyString(), anyString());
         *
         * BDDMockito(Behaviour-Driven Development) : 비즈니스 중심의 행위 주도 개발 방법론이다.
         * - 테스트 대상의 상태 변화를 태스트하는 것이고, 시나리오 기반 테스트하는 패턴을 권장한다.
         * - 아래 코드를 보면 Given절에 Mockito의 when 메서드를 사용하는 것이 보일거다.
         *   테스트의 When이 아닌 곳에서 when이 등장하여 혼동을 주는 문제를 방지하기 위해 탄생했다.
         * - 모든 동작은 Mockito와 동일하다.
         * BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
         *         .willReturn(true);
         *
         * 키워드 정리
         * - Test Double, Stubbing
         *   - dummy, fake, stub, spy, mock
         * - @Mock, @MockBean, @Spy, @SpyBean, @InjectMocks
         * - BDDMockito
         * - Classicist vs Mockist
         */
        Mockito.when(mailSendClient.sendEmail(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
                ).thenReturn(true);



        // When
        boolean result = mailService.sendMail("", "", "", "");

        // Then
        assertThat(result).isTrue();

        // Save가 몇번 호출되어있는지 확인하는 구문이다.
        Mockito.verify(mailSendHistoryRepository, Mockito.times(1)).save(Mockito.any(MailSendHistory.class));
    }
}