package com.travelhana.hanabank.hanabank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelhana.hanabank.hanabank.Model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    ClientDao clientDao;
    @Autowired
    private ConsumerDao consumerDao;
    @Autowired
    MeetingDao meetingDao;
    @Autowired
    RequestDto requestDto;
    @Autowired
    MeetingDto meetingDto;

    private String consumerCI;
    private final WebClient webClient;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private HistoryDao historyDao;
    @Autowired
    private AccountDto accountDto;

    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void openbanking(String responseType, String clientId, String redirectUri, String scope, String state) {
        requestDto.setResponseType(responseType);
        requestDto.setClientId(clientId);
        requestDto.setRedirectUri(redirectUri);
        requestDto.setScope(scope);
        requestDto.setState(state);
    }

    public RedirectView userAuth(ConsumerDto consumerDto) {
        insertConsumer(consumerDto);
        this.consumerCI = consumerDto.getConsumerCI();
        String code = getRandomString();

        String redirectUrl = requestDto.getRedirectUri() + "?code=" + code + "&client_id=" + requestDto.getClientId() + "&scope=" + requestDto.getScope() + "&state=" + requestDto.getState();

        return new RedirectView(redirectUrl);
    }

    public Mono<TokenDto> generateToken(String code, String clientId, String clientSecret, String redirectUri) {
        requestDto.setCode(code);
        requestDto.setClientId(clientId);
        requestDto.setClientSecret(clientSecret);
        requestDto.setRedirectUri(redirectUri);

        TokenDto tokenResponse = new TokenDto();
        if (isRegistered(requestDto)) {
            String token = getRandomString();
            String refreshToken = getRandomString();

            // 사용자 인증 정보 생성
            tokenResponse.setAccess_token(token);
            tokenResponse.setToken_type("Bearer");
            tokenResponse.setExpires_in(3600);
            tokenResponse.setRefresh_token(refreshToken);
            tokenResponse.setScope(requestDto.getScope());
            String seq_no = getRandomNum(9);
            tokenResponse.setUser_seq_no("U" + seq_no);
            tokenResponse.setClient_id(requestDto.getClientId());
            String fintechUseNum = seq_no + getRandomNum(15);
            tokenResponse.setFintech_use_num(fintechUseNum);

            // 사용자 인증 정보 저장
            clientDao.insertToken(tokenResponse);

            // 사용자 핀테크 이용번호 업데이트
            consumerDao.updateFintechUseNum(fintechUseNum, this.consumerCI);
        }
        return Mono.just(tokenResponse);
    }

    public Mono<TokenDto> refreshToken(String code, String clientId, String clientSecret, String redirectUri, String fintechUseNum) {
        requestDto.setCode(code);
        requestDto.setClientId(clientId);
        requestDto.setClientSecret(clientSecret);
        requestDto.setRedirectUri(redirectUri);

        TokenDto tokenResponse = new TokenDto();
        if (isRegistered(requestDto)) {
            String token = getRandomString();
            String refreshToken = getRandomString();

            // 사용자 인증 정보 생성
            tokenResponse.setAccess_token(token);
            tokenResponse.setRefresh_token(refreshToken);

            // 사용자 인증 정보 저장
            tokenResponse.setFintech_use_num(fintechUseNum);
            clientDao.refreshToken(tokenResponse);
        }
        return Mono.just(tokenResponse);
    }

    public Mono<String> getAccounts(String token, String fintechUseNum, String includeCancelYn, String sortOrder) {
        System.out.println("token: " + token);
        // 토큰 유효성 검증
        if(isValidToken(token, fintechUseNum)) {
            List<AccountDto> accountList = consumerDao.getAccountList(fintechUseNum);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode responseJson = mapper.createObjectNode();

            if (accountList != null && !accountList.isEmpty()) {
                responseJson.put("res_cnt", accountList.size());

                ArrayNode resList = mapper.createArrayNode();
                for (AccountDto account : accountList) {
                    ObjectNode accountJson = mapper.createObjectNode();
                    accountJson.put("fintech_use_num", fintechUseNum);
                    accountJson.put("account_name", account.getAccount_name());
                    accountJson.put("account_num", account.getAccount_num());

                    resList.add(accountJson);
                }

                responseJson.set("res_list", resList);
                System.out.println(responseJson);
            }
            return Mono.just(responseJson.toString());
        }

        return Mono.just("error : no result");
    }

    public Mono<String> getMeetingAccount(String token, String fintechUseNum, String includeCancelYn, String sortOrder, List<String> macntNumList) {
        // 토큰 유효성 검증
        if(isValidToken(token, fintechUseNum)) {
            if (macntNumList != null && !macntNumList.isEmpty()) {
                List<MeetingDto> accountList = meetingDao.getMeetingAccountList(macntNumList);

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode responseJson = mapper.createObjectNode();

                if (accountList != null && !accountList.isEmpty()) {
                    responseJson.put("res_cnt", accountList.size());

                    ArrayNode resList = mapper.createArrayNode();
                    for (MeetingDto account : accountList) {
                        ObjectNode accountJson = mapper.createObjectNode();
                        accountJson.put("fintech_use_num", fintechUseNum);
                        accountJson.put("macnt_name", account.getMacnt_name());
                        accountJson.put("macnt_num", account.getMacnt_num());
                        accountJson.put("macnt_balance", account.getMacnt_balance());
                        accountJson.put("macnt_target_amount", account.getMacnt_target_amount());
                        accountJson.put("macnt_dues_date", account.getMacnt_dues_date());
                        accountJson.put("macnt_dues", account.getMacnt_dues());

                        AutoTransferDto atDto = meetingDao.getAutoTransfer(account.getMacnt_num());
                        List<AccountDto> accountDtos = consumerDao.getAccountList(fintechUseNum);
                        if (atDto != null && accountDtos.stream().anyMatch(accountDto -> accountDto.getAccount_num().equals(atDto.getAccount_num()))) {
                            accountJson.put("at_date", atDto.getAt_date());
                            accountJson.put("at_amount", atDto.getAt_amount());
                            accountJson.put("account_num", atDto.getAccount_num());
                        }

                        resList.add(accountJson);
                    }

                    responseJson.set("res_list", resList);
                }
                return Mono.just(responseJson.toString());
            }
        }

        return Mono.just("error : no result");
    }

    public Mono<String> getMeetingMembers(String token, String fintechUseNum, List<String> macntNumList) {
        // 토큰 유효성 검증
        if(isValidToken(token, fintechUseNum)) {
            if (macntNumList != null && !macntNumList.isEmpty()) {
                List<MeetingDto> memberList = meetingDao.getMeetingMembers(macntNumList);

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode responseJson = mapper.createObjectNode();
                ArrayNode resultsArray = mapper.createArrayNode();  // Array to store each group of macntNum and res_list

                if (memberList != null && !memberList.isEmpty()) {
                    Map<String, List<MeetingDto>> groupedMembers = memberList.stream()
                            .collect(Collectors.groupingBy(MeetingDto::getMacnt_num));

                    // Iterate through groupedMembers and create JSON structure for each entry
                    for (Map.Entry<String, List<MeetingDto>> entry : groupedMembers.entrySet()) {
                        String macntNum = entry.getKey();
                        List<MeetingDto> members = entry.getValue();

                        ObjectNode macntJson = mapper.createObjectNode(); // New object for each macntNum group
                        macntJson.put("macntNum", macntNum);

                        ArrayNode resList = mapper.createArrayNode();
                        for (MeetingDto member : members) {
                            ObjectNode accountJson = mapper.createObjectNode();
                            accountJson.put("consumer_name", member.getConsumer_name());
                            accountJson.put("consumer_pnum_back", member.getConsumer_pnum_back());
                            accountJson.put("macnt_pay_status", member.getMacnt_pay_status());

                            resList.add(accountJson);
                        }
                        macntJson.set("res_list", resList);
                        resultsArray.add(macntJson);
                    }
                    responseJson.set("results", resultsArray);
                }
                return Mono.just(responseJson.toString());
            }
        }

        return Mono.just("error : no result");
    }

    public Mono<MeetingDto> joinMeeting(String token, String fintechUseNum, String accountNum, String macntName, int macntTargetAmount, int macntDues, int macntDuesDate) {
        meetingDto.setAccount_num(accountNum);
        meetingDto.setMacnt_name(macntName);
        meetingDto.setMacnt_target_amount(macntTargetAmount);
        meetingDto.setMacnt_dues(macntDues);
        meetingDto.setMacnt_dues_date(macntDuesDate);

        // 토큰 유효성 검증
        if(isValidToken(token, fintechUseNum)) {
            // 모임통장 계좌번호 생성
            String macntNum = "102" + getRandomNum(11);
            meetingDto.setMacnt_num(macntNum);

            try {
                // 모임통장 정보 저장
                meetingDao.insertMeeting(meetingDto);
                meetingDao.insertMeetingUser(macntNum, fintechUseNum);

                return Mono.just(meetingDto);
            } catch (DuplicateKeyException e) {
                return Mono.error(new IllegalArgumentException("해당 계좌로부터 개설한 모임통장이 존재합니다."));
            } catch (Exception e) {
                // 기타 예외 처리
                System.err.println("An error occurred: " + e.getMessage());
                return Mono.error(new RuntimeException("모임통장 생성 중 오류가 발생했습니다."));
            }
        }

        return Mono.just(new MeetingDto());
    }

    public Mono<String> addMeetingMember(String token, String fintechUseNum, String macntNum) {
        // 토큰 유효성 검증
        if(isValidToken(token, fintechUseNum)) {
            try {
                // 모임통장 멤버 추가
                meetingDao.insertMeetingUser(macntNum, fintechUseNum);

                return Mono.just("success");
            } catch (DuplicateKeyException e) {
                return Mono.error(new IllegalArgumentException("이미 가입된 멤버입니다."));
            } catch (Exception e) {
                // 기타 예외 처리
                System.err.println("An error occurred: " + e.getMessage());
                return Mono.error(new RuntimeException("모임통장 생성 중 오류가 발생했습니다."));
            }
        }
        return Mono.just("error");
    }

    public Mono<String> addAutoTransfer(String token, String fintechUseNum, String accountNum, String macntNum, int atAmount, int atDate, String accountMsg, String macntMsg) {
        if (isValidToken(token, fintechUseNum)) {
            MeetingDto meetingDto = meetingDao.getMeetingAccount(macntNum);
            if (accountNum.equals(meetingDto.getAccount_num())) {
                return Mono.just("입출금 정보는 동일할 수 없습니다.");
            }

            meetingDao.insertAutoTransfer(accountNum, macntNum, atAmount, atDate, accountMsg, macntMsg);
            return Mono.just("자동이체가 등록되었습니다. 매월 " + atDate + "일에 " + atAmount + "원이 이체됩니다.");
        }
        return Mono.just("Auto-Transfer Info Registration failed");
    }

    public Mono<ResponseEntity<List<HistoryDto>>> getTransactionList(String token, String accountNum, String fintechUseNum, String inquiryType, String fromDate, String toDate) {
        if (isValidToken(token, fintechUseNum)) {
            List<HistoryDto> transactionList = historyDao.getTransactionList(accountNum, inquiryType, fromDate, toDate);

            return Mono.just(new ResponseEntity<>(transactionList, HttpStatus.OK));
        }
        return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    public Mono<String> getAccount(String token, String fintechUseNum, String bankTranId, String accountNum, String inquiryType) {
        if (isValidToken(token, fintechUseNum)) {
            String result = "";

            AccountDto accountDto = new AccountDto();
            if (accountNum.startsWith("102")) {
                accountDto = meetingDao.getMeetingAccountInfo(accountNum);
            } else if (accountNum.startsWith("412")) {
                accountDto = meetingDao.getAccountInfo(accountNum);
            }

            if (inquiryType.equals("holderName")) {
                result = accountDto.getHolder_name();
            } else if (inquiryType.equals("accountPw")) {
                result = accountDto.getAccount_pw();
            }

            return Mono.just(result);
        }
        return Mono.just(accountNum);
    }

    public Mono<String> withdraw(String token, String fintechUseNum, String apiTranId, String tranDtime, String bankTranId, String accountNum, String reqClientName, String printContent, int tranAmt) {
        if (isValidToken(token, fintechUseNum)) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode responseJson = mapper.createObjectNode();

            responseJson.put("api_tran_id", apiTranId);
            responseJson.put("api_tran_dtm", tranDtime);

            ConsumerDto consumerDto = consumerDao.getWdLimit(fintechUseNum);
            int wdLimitRemainAmt = consumerDto.getWd_limit_remain_amt() - tranAmt;

            if (wdLimitRemainAmt >= 0) {
                AccountDto accountDto = historyDao.getAccount(accountNum);
                // 잔액 검증
                if (accountDto.getAccount_balance() - tranAmt >= 0) {
                    // 출금 계좌 잔액 업데이트
                    historyDao.updateBalance(accountNum, tranAmt, "O");

                    // 출금 내역 추가
                    HistoryDto historyDto = new HistoryDto();
                    historyDto.setAccount_num(accountNum);
                    historyDto.setBranch_name((printContent == null || printContent.isEmpty()) ? reqClientName : printContent);
                    historyDto.setTran_amt(tranAmt);
                    historyDto.setBalance_amt(accountDto.getAccount_balance());
                    historyDto.setTran_date(tranDtime);
                    historyDto.setInout_type("O");

                    historyDao.insertTransferHistory(historyDto);

                    // 응답 설정
                    responseJson.put("rsp_code", "A0000");
                    responseJson.put("rsp_message", "");
                    responseJson.put("bank_tran_id", bankTranId);
                    responseJson.put("account_num", accountNum);
                    responseJson.put("account_alias", accountDto.getAccount_name());
                    responseJson.put("bank_code_std", "081");
                    responseJson.put("bank_name", "하나은행");
                    responseJson.put("print_content", printContent);
                    responseJson.put("account_holder_name", accountDto.getHolder_name());
                    responseJson.put("tran_amt", tranAmt);
                    responseJson.put("wd_limit_remain_amt", wdLimitRemainAmt);
                } else {
                    responseJson.put("rsp_code", "A0112");
                    responseJson.put("rsp_message", "잔액부족");
                }
            } else {
                responseJson.put("rsp_code", "A0112");
                responseJson.put("rsp_message", "사용자 출금이체 한도 초과 (일 한도) [(출금이체 요청 금액:[" + tranAmt + "] + 출금이체 당일 누적 금액:[" + (consumerDto.getWd_limit() - consumerDto.getWd_limit_remain_amt()) + "]) > 사용자 출금이체 한도(일별):[" + consumerDto.getWd_limit() + "]]");
                responseJson.put("wd_limit_remain_amt", consumerDto.getWd_limit_remain_amt());
            }

            return Mono.just(responseJson.toString());
        }

        return Mono.just(HttpStatus.UNAUTHORIZED.toString());
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public Mono<String> deposit(String token, String apiTranId, String tranDtime, String bankTranId, String accountNum, String reqClientName, String printContent, int tranAmt, String accountHolderName, String withdrawBankTranId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        responseJson.put("api_tran_id", apiTranId);
        responseJson.put("api_tran_dtm", tranDtime);

        // 입금 계좌 잔액 업데이트
        historyDao.updateBalance(accountNum, tranAmt, "I");
        AccountDto accountDto = historyDao.getMeetingAccount(accountNum);

        // 입금 내역 추가
        HistoryDto historyDto = new HistoryDto();
        historyDto.setAccount_num(accountDto.getAccount_num());
        historyDto.setBranch_name((printContent == null || printContent.isEmpty()) ? reqClientName : printContent);
        historyDto.setTran_amt(tranAmt);
        historyDto.setBalance_amt(accountDto.getAccount_balance());
        historyDto.setTran_date(tranDtime);
        historyDto.setInout_type("I");

        historyDao.insertTransferHistory(historyDto);

        // 응답 설정
        responseJson.put("rsp_code", "A0000");
        responseJson.put("rsp_message", "");
        responseJson.put("wd_account_holder_name", reqClientName);
        responseJson.put("bank_tran_id", bankTranId);
        responseJson.put("bank_tran_date", tranDtime);
        responseJson.put("bank_code_std", "081");
        responseJson.put("bank_name", "하나은행");
        responseJson.put("account_num", accountNum);
        responseJson.put("print_content", printContent);
        responseJson.put("account_holder_name", accountHolderName);
        responseJson.put("tran_amt", tranAmt);
        responseJson.put("withdraw_bank_tran_id", withdrawBankTranId);

        String sql = "{call process_payment_status(?, ?)}"; // 프로시저 호출 SQL
        // 프로시저 호출
        jdbcTemplate.update(sql, historyDto.getBranch_name(), Long.parseLong(historyDto.getAccount_num()));

        return Mono.just(responseJson.toString());
    }

    public boolean isRegistered(RequestDto requestDto) {
        String secret = clientDao.getClientSecret(requestDto.getClientId());

        // 클라이언트 인증 및 등록 여부 검증
        return secret != null && secret.equals(requestDto.getClientSecret());
    }

    public boolean isValidToken(String token, String fintechUseNum) {
        String accessToken = consumerDao.getAccessToken(fintechUseNum);
        return accessToken.equals(token);
    }

    public void insertConsumer(ConsumerDto consumerDto) {
        String ci = consumerDto.getConsumerNum() + consumerDto.getConsumerPnum();
        consumerDto.setConsumerCI(generateSHA256Hash(ci));

        consumerDao.insertConsumer(consumerDto);
    }

    public static String generateSHA256Hash(String plaintext) {
        try {
            // SHA-256 알고리즘을 사용하기 위한 MessageDigest 인스턴스 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 문자열을 바이트 배열로 변환하고 해시 계산
            byte[] encodedHash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 16진수 문자열로 변환
            return bytesToHex(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘이 지원되지 않습니다.", e);
        }
    }

    // 바이트 배열을 16진수 문자열로 변환하는 메서드
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getRandomString() {
        // UUID를 사용한 고유 인증코드 생성
        return UUID.randomUUID().toString();
    }

    private static final String NUMBERS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getRandomNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
