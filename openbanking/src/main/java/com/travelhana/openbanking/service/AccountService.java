package com.travelhana.openbanking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelhana.openbanking.Model.AccountDao;
import com.travelhana.openbanking.Model.MeetingDto;
import com.travelhana.openbanking.Model.TokenDao;
import com.travelhana.openbanking.Model.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    @Autowired
    TokenDao tokenDao;
    @Autowired
    AccountDao accountDao;

    private final WebClient webClient;
    private static final String BASE_URL = "http://localhost:8009";
    public AccountService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8009").build();
    }

    public Mono<String> getAccounts(String token, String userSeqNo, String includeCancelYn, String sortOrder) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNo);

        if (fintechUseNum != null) {
            List<MeetingDto> myAccounts = accountDao.getAccountList(userSeqNo);
            if (!myAccounts.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode responseJson = mapper.createObjectNode();
                responseJson.put("res_cnt", myAccounts.size());

                ArrayNode resList = mapper.createArrayNode();
                for (MeetingDto account : myAccounts) {
                    ObjectNode accountJson = mapper.createObjectNode();
                    accountJson.put("fintech_use_num", fintechUseNum);
                    accountJson.put("account_name", account.getAccount_name());
                    accountJson.put("account_num", account.getAccount_num());

                    resList.add(accountJson);
                }
                responseJson.set("res_list", resList);

                return Mono.just(responseJson.toString());
            } else {
                return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/accountList")
                                .queryParam("fintech_use_num", fintechUseNum)
                                .queryParam("include_cancel_yn", "N")
                                .queryParam("sort_order", "D")
                                .build())
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class) // 응답을 JSON 문자열로 받기
                        .doOnNext(json -> {
                            ObjectMapper mapper = new ObjectMapper();

                            JsonNode rootNode = null;
                            try {
                                rootNode = mapper.readTree(json);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            JsonNode resListNode = rootNode.path("res_list");
                            if (resListNode.isArray()) {
                                for (JsonNode accountNode : resListNode) {
                                    String accountNum = accountNode.path("account_num").asText();
                                    String accountName = accountNode.path("account_name").asText();
                                    accountDao.insertAccountInfo(accountNum, accountName, userSeqNo);
                                }
                            }
                        })
                        .doOnError(e -> {
                            // 오류 발생 시 로그 출력
                            System.err.println("계좌 목록 조회 중 오류 발생: " + e.getMessage());
                        });
            }
        }
        return Mono.just("error : invalid token");
    }

    public Mono<String> getAccount(String token, String bankTranId, String userSeqNo, String accountNum, String inquiryType) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNo);

        if (fintechUseNum != null) {
            return webClient.post()
                    .uri("accountInfo")
                    .contentType(MediaType.APPLICATION_JSON) // application/json; charset=UTF-8
                    .header("Authorization", token)
                    .body(BodyInserters.fromFormData("bank_tran_id", bankTranId)
                            .with("fintech_use_num", fintechUseNum)
                            .with("account_num", accountNum)
                            .with("scope", "inquiry")
                            .with("inquiryType", inquiryType)
                    ).retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> {
                        System.out.println("계좌정보조회 : " + response);
                    })
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("계좌정보조회 중 오류 발생: " + e.getMessage());
                    });
        }
        return Mono.just("error : invalid token");
    }

    public Mono<String> getMeetingAccount(String token, String userSeqNo, String includeCancelYn, String sortOrder) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNo);

        if (fintechUseNum != null) {
            List<String> myMeetingAccounts = accountDao.getMeetingAccountList(userSeqNo);
            if (!myMeetingAccounts.isEmpty()) {
                return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/meetingAccountList")
                                .queryParam("fintech_use_num", fintechUseNum)
                                .queryParam("include_cancel_yn", "N")
                                .queryParam("sort_order", "D")
                                .queryParam("macnt_num_list", myMeetingAccounts)
                                .build())
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class) // 응답을 JSON 문자열로 받기
                        .doOnNext(json -> {
                            // JSON 응답 출력
                            System.out.println("JSON Response: " + json);
                        })
                        .doOnError(e -> {
                            // 오류 발생 시 로그 출력
                            System.err.println("계좌 목록 조회 중 오류 발생: " + e.getMessage());
                        });
            } else {
                return Mono.just(myMeetingAccounts.toString());
            }
        }
        return Mono.just("error : invalid token");
    }

    public Mono<MeetingDto> joinMeeting(String token, String userSeqNo, String accountNum, String macntName, int macntTargetAmount, int macntDues, int macntDuesDate) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNo);

        if (fintechUseNum != null) {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/joinMeeting")
                            .queryParam("fintechUseNum", fintechUseNum)
                            .queryParam("accountNum", accountNum)
                            .queryParam("macntName", macntName)
                            .queryParam("macntTargetAmount", macntTargetAmount)
                            .queryParam("macntDues", macntDues)
                            .queryParam("macntDuesDate", macntDuesDate)
                            .build())
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(MeetingDto.class)
                    .doOnNext(response -> {
                        accountDao.insertAccountInfo(response.getMacnt_num(), response.getMacnt_name(), userSeqNo);
                    })
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("모임서비스 가입 중 오류 발생: " + e.getMessage());
                    });
        }
        return Mono.just(new MeetingDto());
    }

    public Mono<String> addMeetingMember(String token, String userSeqNum, String macntNum) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNum);

        if (fintechUseNum != null) {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/addMeetingMember")
                            .queryParam("fintechUseNum", fintechUseNum)
                            .queryParam("macntNum", macntNum)
                            .build())
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> {
                        accountDao.addMeetingMember(macntNum, userSeqNum);
                    })
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("모임서비스 가입 중 오류 발생: " + e.getMessage());
                    });
        }
        return Mono.just("error : invalid token");
    }

    public Mono<String> addAutoTransfer(String token, String userSeqNum, String accountNum, String macntNum, int atAmount, int atDate, String accountMsg, String macntMsg) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNum);

        if (fintechUseNum != null) {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/addAutoTransfer")
                        .queryParam("fintechUseNum", fintechUseNum)
                        .queryParam("accountNum", accountNum)
                        .queryParam("macntNum", macntNum)
                        .queryParam("atAmount", atAmount)
                        .queryParam("atDate", atDate)
                        .queryParam("accountMsg", accountMsg)
                        .queryParam("macntMsg", macntMsg)
                        .build())
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("자동이체 등록 중 오류 발생: " + e.getMessage());
                    });
        }
        return Mono.just("error : invalid token");
    }

    public Mono<String> getTransactionList(String token, String accountNum, String userSeqNo, String inquiryType, String inquiryBase, String fromDate, String toDate, String sortOrder, String tranDtime) {
        // 토큰 유효성 검증
        String fintechUseNum = isValidToken(token, userSeqNo);
        if (fintechUseNum != null) {
            return webClient.post()
                    .uri("/transaction_list")
                    .contentType(MediaType.APPLICATION_JSON) // application/json; charset=UTF-8
                    .header("Authorization", token)
                    .body(BodyInserters.fromFormData( "account_num", accountNum)
                            .with("fintech_use_num", fintechUseNum)
                            .with("inquiry_type", inquiryType)
                            .with("from_date", fromDate)
                            .with("to_date", toDate)
                    ).retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        // 오류 발생 시 로그 출력
                        System.err.println("거래내역 조회 중 오류 발생: " + e.getMessage());
                    });
        }
        return Mono.just("error : invalid token");
    }

    public String isValidToken(String token, String userSeqNo) {
        TokenDto accessToken = tokenDao.getFintechUseNum(userSeqNo);
        if (accessToken != null && accessToken.getAccess_token().equals(token)) {
            return accessToken.getFintech_use_num();
        }
        return null;
    }
}
