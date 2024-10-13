package com.travelhana.hanabank.hanabank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelhana.hanabank.hanabank.Model.*;
import com.travelhana.hanabank.hanabank.service.AuthService;
import com.travelhana.hanabank.hanabank.service.CoolSmsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    AuthService authService;
    @Autowired
    CoolSmsService coolSmsService;

    @RequestMapping("/authorize")
    public String openbanking(@RequestParam("response_type") String responseType,
                              @RequestParam("client_id") String clientId,
                              @RequestParam("redirect_uri") String redirectUri,
                              @RequestParam("scope") String scope,
                              @RequestParam("state") String state) {
        authService.openbanking(responseType, clientId, redirectUri, scope, state);
        return "openbanking";
    }

    @RequestMapping("/phoneAuthentication")
    public ResponseEntity<String> phoneAuthentication(@RequestParam("to") String to) {
        String response = coolSmsService.sendSms(to);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/userAuth")
    public RedirectView userAuth(@ModelAttribute ConsumerDto consumerDto) {
        return authService.userAuth(consumerDto);
    }

    @RequestMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<TokenDto> token(@RequestParam("code") String code,
                                @RequestParam("client_id") String clientId,
                                @RequestParam("client_secret") String clientSecret,
                                @RequestParam("redirect_uri") String redirectUri) {
        return authService.generateToken(code, clientId, clientSecret, redirectUri);
    }

    @RequestMapping(value = "/refreshToken", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<TokenDto> refreshToken(@RequestParam("code") String code,
                                       @RequestParam("client_id") String clientId,
                                       @RequestParam("client_secret") String clientSecret,
                                       @RequestParam("redirect_uri") String redirectUri,
                                       @RequestParam("fintech_use_num") String fintechUseNum) {
        return authService.refreshToken(code, clientId, clientSecret, redirectUri, fintechUseNum);
    }

    @RequestMapping("/accountList")
    @ResponseBody
    public Mono<String> accountList(@RequestHeader("Authorization") String token, @RequestParam("fintech_use_num") String fintechUseNum, @RequestParam("include_cancel_yn") String includeCancelYn, @RequestParam("sort_order") String sortOrder) {
        return authService.getAccounts(token, fintechUseNum, includeCancelYn, sortOrder);
    }

    @RequestMapping("/meetingAccountList")
    @ResponseBody
    public Mono<String> meetingAccountList(@RequestHeader("Authorization") String token, @RequestParam("fintech_use_num") String fintechUseNum, @RequestParam("include_cancel_yn") String includeCancelYn, @RequestParam("sort_order") String sortOrder, @RequestParam("macnt_num_list") List<String> macntNumList) {
        Mono<String> meetingAccountMono = authService.getMeetingAccount(token, fintechUseNum, includeCancelYn, sortOrder, macntNumList);
        Mono<String> meetingMembersMono = authService.getMeetingMembers(token, fintechUseNum, macntNumList);

        // Mono.zip을 사용하여 두 개의 Mono의 결과를 결합
        return Mono.zip(meetingAccountMono, meetingMembersMono)
                .map(tuple -> {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode combinedResult = mapper.createObjectNode();

                    try {
                        String meetingAccountJson = tuple.getT1().toString(); // T1을 JSON 문자열로 변환
                        String meetingMembersJson = tuple.getT2().toString(); // T2를 JSON 문자열로 변환

                        // 두 결과를 각각 JSON으로 변환하여 병합
                        combinedResult.set("meetingAccount", mapper.readTree(meetingAccountJson));
                        combinedResult.set("meetingMembers", mapper.readTree(meetingMembersJson));

                        System.out.println(combinedResult);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return combinedResult.toString();
                });
    }

    @RequestMapping("/joinMeeting")
    @ResponseBody
    public Mono<MeetingDto> joinMeeting(@RequestHeader("Authorization") String token, @RequestParam("fintechUseNum") String fintechUseNum, @RequestParam("accountNum") String accountNum, @RequestParam("macntName") String macntName, @RequestParam("macntTargetAmount") int macntTargetAmount, @RequestParam("macntDues") int macntDues, @RequestParam("macntDuesDate") int macntDuesDate) {
        return authService.joinMeeting(token, fintechUseNum, accountNum, macntName, macntTargetAmount, macntDues, macntDuesDate);
    }

    @RequestMapping("/addMeetingMember")
    @ResponseBody
    public Mono<String> addMeetingMember(@RequestHeader("Authorization") String token, @RequestParam("fintechUseNum") String fintechUseNum, @RequestParam("macntNum") String macntNum) {
        return authService.addMeetingMember(token, fintechUseNum, macntNum);
    }

    @RequestMapping("/addAutoTransfer")
    @ResponseBody
    public Mono<String> addAutoTransfer(@RequestHeader("Authorization") String token, @RequestParam("fintechUseNum") String fintechUseNum, @RequestParam("accountNum") String accountNum, @RequestParam("macntNum") String macntNum, @RequestParam("atAmount") int atAmount, @RequestParam("atDate") int atDate, @RequestParam(value = "accountMsg", required = false) String accountMsg, @RequestParam(value = "macntMsg", required = false) String macntMsg) {
        return authService.addAutoTransfer(token, fintechUseNum, accountNum, macntNum, atAmount, atDate, accountMsg, macntMsg);
    }

    @RequestMapping("/transaction_list")
    @ResponseBody
    public Mono<ResponseEntity<List<HistoryDto>>> getTransactionList(@RequestHeader("Authorization") String token, @RequestParam("account_num") String accountNum, @RequestParam("fintech_use_num") String fintechUseNum, @RequestParam("inquiry_type") String inquiryType, @RequestParam("from_date") String fromDate, @RequestParam("to_date") String toDate) {
        return authService.getTransactionList(token, accountNum, fintechUseNum, inquiryType, fromDate, toDate);
    }

    @RequestMapping("/accountInfo")
    @ResponseBody
    public Mono<String> accountInfo(@RequestHeader("Authorization") String token, @RequestParam("fintech_use_num") String fintechUseNum, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("account_num") String accountNum, @RequestParam("inquiryType") String inquiryType) {
        return authService.getAccount(token, fintechUseNum, bankTranId, accountNum, inquiryType);
    }

    @RequestMapping("/withdraw")
    @ResponseBody
    public Mono<String> withdraw(@RequestHeader("Authorization") String token, @RequestParam("fintech_use_num") String fintechUseNum, @RequestParam("api_tran_id") String apiTranId, @RequestParam("tran_dtime") String tranDtime, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("cntr_account_num") String accountNum, @RequestParam("req_client_name") String reqClientName, @RequestParam("dps_print_content") String printContent, @RequestParam("tran_amt") int tranAmt) {
        return authService.withdraw(token, fintechUseNum, apiTranId, tranDtime, bankTranId, accountNum, reqClientName, printContent, tranAmt);
    }

    @RequestMapping("/deposit")
    @ResponseBody
    public Mono<String> deposit(@RequestHeader("Authorization") String token, @RequestParam("api_tran_id") String apiTranId, @RequestParam("tran_dtime") String tranDtime, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("cntr_account_num") String accountNum, @RequestParam("req_client_name") String reqClientName, @RequestParam("wd_print_content") String printContent, @RequestParam("tran_amt") int tranAmt, @RequestParam("account_holder_name") String accountHolderName, @RequestParam("withdraw_bank_tran_id") String withdrawBankTranId) {
        return authService.deposit(token, apiTranId, tranDtime, bankTranId, accountNum, reqClientName, printContent, tranAmt, accountHolderName, withdrawBankTranId);
    }
}
