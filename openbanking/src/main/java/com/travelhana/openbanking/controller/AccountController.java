package com.travelhana.openbanking.controller;

import com.travelhana.openbanking.Model.MeetingDto;
import com.travelhana.openbanking.Model.TokenDto;
import com.travelhana.openbanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @RequestMapping("/list")
    @ResponseBody
    public Mono<String> list(@RequestHeader("Authorization") String token, @RequestParam("user_seq_no") String userSeqNo, @RequestParam("include_cancel_yn") String includeCancelYn, @RequestParam("sort_order") String sortOrder) {
        return accountService.getAccounts(token, userSeqNo, includeCancelYn, sortOrder);
    }

    @RequestMapping("/info")
    @ResponseBody
    public Mono<String> info(@RequestHeader("Authorization") String token, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("user_seq_no") String userSeqNo, @RequestParam("account_num") String accountNum, @RequestParam("inquiryType") String inquiryType) {
        return accountService.getAccount(token, bankTranId, userSeqNo, accountNum, inquiryType);
    }

    @RequestMapping("/meetingList")
    @ResponseBody
    public Mono<String> meetingList(@RequestHeader("Authorization") String token, @RequestParam("user_seq_no") String userSeqNo, @RequestParam("include_cancel_yn") String includeCancelYn, @RequestParam("sort_order") String sortOrder) {
        return accountService.getMeetingAccount(token, userSeqNo, includeCancelYn, sortOrder);
    }

    @RequestMapping("/joinMeeting")
    @ResponseBody
    public Mono<MeetingDto> joinMeeting(@RequestHeader("Authorization") String token, @RequestParam("userSeqNum") String userSeqNum, @RequestParam("accountNum") String accountNum, @RequestParam("macntName") String macntName, @RequestParam("macntTargetAmount") int macntTargetAmount, @RequestParam("macntDues") int macntDues, @RequestParam("macntDuesDate") int macntDuesDate) {
        return accountService.joinMeeting(token, userSeqNum, accountNum, macntName, macntTargetAmount, macntDues, macntDuesDate);
    }

    @RequestMapping("/addMeetingMember")
    @ResponseBody
    public Mono<String> addMeetingMember(@RequestHeader("Authorization") String token, @RequestParam("userSeqNum") String userSeqNum, @RequestParam("macntNum") String macntNum) {
        return accountService.addMeetingMember(token, userSeqNum, macntNum);
    }

    @RequestMapping("/addAutoTransfer")
    @ResponseBody
    public Mono<String> addAutoTransfer(@RequestHeader("Authorization") String token, @RequestParam("userSeqNum") String userSeqNum, @RequestParam("accountNum") String accountNum, @RequestParam("macntNum") String macntNum, @RequestParam("atAmount") int atAmount, @RequestParam("atDate") int atDate, @RequestParam(value = "accountMsg", required = false) String accountMsg, @RequestParam(value = "macntMsg", required = false) String macntMsg) {
        return accountService.addAutoTransfer(token, userSeqNum, accountNum, macntNum, atAmount, atDate, accountMsg, macntMsg);
    }

    @RequestMapping("/transaction_list")
    @ResponseBody
    public Mono<String> transaction_list(@RequestHeader("Authorization") String token, @RequestParam("account_num") String accountNum,  @RequestParam("user_seq_no") String userSeqNo, @RequestParam("inquiry_type") String inquiryType, @RequestParam("inquiry_base") String inquiryBase, @RequestParam("from_date") String fromDate, @RequestParam("to_date") String toDate, @RequestParam("sort_order") String sortOrder, @RequestParam("tran_dtime") String tranDtime) {
        return accountService.getTransactionList(token, accountNum, userSeqNo, inquiryType, inquiryBase, fromDate, toDate, sortOrder, tranDtime);
    }
}
