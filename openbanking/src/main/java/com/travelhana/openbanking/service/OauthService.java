package com.travelhana.openbanking.service;

import com.travelhana.openbanking.Model.ClientDao;
import com.travelhana.openbanking.Model.RequestDto;
import com.travelhana.openbanking.Model.TokenDao;
import com.travelhana.openbanking.Model.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.SecureRandom;

@Service
public class OauthService {
    @Autowired
    ClientDao clientDao;
    @Autowired
    TokenDao tokenDao;
    @Autowired
    RequestDto requestDto;
    @Autowired
    private TokenDto tokenDto;

    private final WebClient webClient;
    private final String OB_CLIENT_ID = "9XyZ8U7v6tRsQ5W4lMn3pP2kL1m0";
    private final String OB_CLIENT_SECRET = "B7fG2dJ9sX6zP8wR1V0tU9qH3cL4xY2k";
    private final String REQUEST_STATE = generateRandomString(16);
    private final String REDIRECT_URI = "http://localhost:8081/oauth/callback";

    private static final String BASE_URL = "http://localhost:8009";

    public OauthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8009").build();
    }

    public RedirectView oauth(String responseType, String clientId, String redirectUri, String scope, String state) {
        requestDto.setResponseType(responseType);
        requestDto.setClientId(clientId);
        requestDto.setRedirectUri(redirectUri);
        requestDto.setScope(scope);
        requestDto.setState(state);

        String bankAuthUrl = "/errorPage";
        if (isRegistered(requestDto)) {
            bankAuthUrl = BASE_URL + "/authorize?" +
                    "response_type=" + requestDto.getResponseType() + "&" +
                    "client_id=" + OB_CLIENT_ID + "&" +
                    "redirect_uri=" + REDIRECT_URI + "&" +
                    "scope=" + requestDto.getScope() + "&" +
                    "state=" + REQUEST_STATE;
        }

        return new RedirectView(bankAuthUrl);
    }

    public Mono<TokenDto> token(String code, String clientId, String clientSecret,  String redirectUri, String state) {
        requestDto.setCode(code);
        requestDto.setClientId(clientId);
        requestDto.setClientSecret(clientSecret);
        requestDto.setRedirectUri(redirectUri);
        requestDto.setState(state);

        if (isRegistered(requestDto)) {
            return webClient.post()
                    .uri("/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("code", code)
                            .with("redirect_uri", REDIRECT_URI)
                            .with("client_id", OB_CLIENT_ID)
                            .with("client_secret", OB_CLIENT_SECRET))
                    .retrieve()
                    .bodyToMono(TokenDto.class)
                    .flatMap(tokenDto -> {
                        tokenDto.setClient_id(requestDto.getClientId());
                        tokenDto.setAuthorization_code(code);

                        return Mono.fromRunnable(() -> tokenDao.insertToken(tokenDto))
                                .thenReturn(tokenDto);
                    });
        }
        return Mono.just(new TokenDto());
    }

    public Mono<TokenDto> refreshToken(String clientId, String clientSecret, String refreshToken, String redirectUri, String state) {
        // 인증코드 불러오기
        TokenDto tokenDto = tokenDao.getAuthorizationCode(refreshToken);

        requestDto.setCode(tokenDto.getAuthorization_code());
        requestDto.setClientId(clientId);
        requestDto.setClientSecret(clientSecret);
        requestDto.setRedirectUri(redirectUri);
        requestDto.setState(state);

        if (isRegistered(requestDto)) {
            return webClient.post()
                    .uri("/refreshToken")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("code", tokenDto.getAuthorization_code())
                            .with("redirect_uri", REDIRECT_URI)
                            .with("client_id", OB_CLIENT_ID)
                            .with("client_secret", OB_CLIENT_SECRET)
                            .with("fintech_use_num", tokenDto.getFintech_use_num()))
                    .retrieve()
                    .bodyToMono(TokenDto.class)
                    .flatMap(token -> {
                        token.setAuthorization_code(tokenDto.getAuthorization_code());

                        return Mono.fromRunnable(() -> tokenDao.refreshToken(token))
                                .thenReturn(token);
                    });
        }
        return Mono.just(new TokenDto());
    }

    public RedirectView callback(String code, String clientId, String state, String scope) {
        if (clientId.equals(OB_CLIENT_ID) && state.equals(REQUEST_STATE)) {
            String redirectUrl = requestDto.getRedirectUri() + "?code=" + code + "&client_id=" + requestDto.getClientId() + "&scope=" + scope + "&state=" + requestDto.getState();

            return new RedirectView(redirectUrl);
        } else {
            String errorRedirectUrl = requestDto.getRedirectUri() + "?error=invalid_client&state=" + state;
            return new RedirectView(errorRedirectUrl);
        }
    }

    public boolean isRegistered(RequestDto requestDto) {
        RequestDto confirmRequest = clientDao.getRedirectUri(requestDto.getClientId());

        // 클라이언트 인증 및 등록 여부 검증
        if (confirmRequest != null) {
            if (requestDto.getClientSecret() == null) {
                return confirmRequest.getRedirectUri().equals(requestDto.getRedirectUri());
            } else {
                return confirmRequest.getClientSecret().equals(requestDto.getClientSecret()) && confirmRequest.getRedirectUri().equals(requestDto.getRedirectUri());
            }
        }
        return false;
    }

    // 사용할 문자들 (대문자, 소문자, 숫자)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // 랜덤 문자열 생성 메서드
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
