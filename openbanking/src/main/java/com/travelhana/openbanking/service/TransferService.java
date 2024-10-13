package com.travelhana.openbanking.service;

import com.travelhana.openbanking.Model.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TransferService {
    @Autowired
    AccountService accountService;
    @Autowired
    AccountDao accountDao;

    private final WebClient webClient;
    private static final String BASE_URL = "http://localhost:8009";

    public TransferService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8009").build();
    }

    public Mono<String> withdraw(String token, String bankTranId, String cntrAccountNum, String dpsPrintContent, String userSeqNo, int tranAmt, String tranDtime, String reqClientName, String reqClientNum) {
        String fintechUseNum = accountService.isValidToken(token, userSeqNo);
        if (fintechUseNum != null) {
            if (accountDao.getTranLog(bankTranId) == null) {
                String apiTranId = OauthService.generateRandomString(8) + "-" + OauthService.generateRandomString(4) + "-" + OauthService.generateRandomString(4) + "-" + OauthService.generateRandomString(4);
                return webClient.post()
                        .uri("/withdraw")
                        .contentType(MediaType.APPLICATION_JSON) // application/json; charset=UTF-8
                        .header("Authorization", token)
                        .body(BodyInserters.fromFormData("api_tran_id", apiTranId)
                                .with("cntr_account_num", cntrAccountNum)
                                .with("dps_print_content", dpsPrintContent)
                                .with("fintech_use_num", fintechUseNum)
                                .with("tran_amt", String.valueOf(tranAmt))
                                .with("tran_dtime", tranDtime)
                                .with("req_client_name", reqClientName)
                                .with("req_client_num", reqClientNum)
                                .with("bank_tran_id", bankTranId)
                        ).retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> {
                            // 오류 발생 시 로그 출력
                            System.err.println("Error(A0003) : 내부 처리 에러 " + e.getMessage());
                        });
            }
            return Mono.just("Error(A0326) : 거래고유번호(참가기관) 중복");
        }

        return Mono.just("error : invalid token");
    }

    public Mono<String> deposit(String token, String accountNum, String wdPrintContent, String tranDtime, String bankTranId, String accountHolderName, int tranAmt, String reqClientName, String withdrawBankTranId) {
        if (accountDao.getTranLog(bankTranId) == null) {
            String apiTranId = OauthService.generateRandomString(8) + "-" + OauthService.generateRandomString(4) + "-" + OauthService.generateRandomString(4) + "-" + OauthService.generateRandomString(4);
            return webClient.post()
                    .uri("/deposit")
                    .contentType(MediaType.APPLICATION_JSON) // application/json; charset=UTF-8
                    .header("Authorization", token)
                    .body(BodyInserters.fromFormData("api_tran_id", apiTranId)
                            .with("cntr_account_num", accountNum)
                            .with("wd_print_content", wdPrintContent)
                            .with("tran_amt", String.valueOf(tranAmt))
                            .with("tran_dtime", tranDtime)
                            .with("req_client_name", reqClientName)
                            .with("bank_tran_id", bankTranId)
                            .with("account_holder_name", accountHolderName)
                            .with("withdraw_bank_tran_id", withdrawBankTranId)
                    ).retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("Error(A0003) : 내부 처리 에러 " + e.getMessage());
                    });
        }
        return Mono.just("Error(A0326) : 거래고유번호(참가기관) 중복");
    }
}