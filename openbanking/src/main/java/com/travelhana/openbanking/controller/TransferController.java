package com.travelhana.openbanking.controller;

import com.travelhana.openbanking.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    @Autowired
    TransferService transferService;

    @RequestMapping("/withdraw")
    @ResponseBody
    public Mono<String> withdraw(@RequestHeader("Authorization") String token, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("dps_print_content") String dpsPrintContent, @RequestParam("wd_account_num") String wdAccountNum, @RequestParam("tran_amt") int tranAmt, @RequestParam("user_seq_no") String userSeqNo, @RequestParam("tran_dtime") String tranDtime, @RequestParam("req_client_name") String reqClientName, @RequestParam("req_client_num") String reqClientNum) {
        return transferService.withdraw(token, bankTranId, wdAccountNum, dpsPrintContent, userSeqNo, tranAmt, tranDtime, reqClientName, reqClientNum);
    }

    @RequestMapping("/deposit")
    @ResponseBody
    public Mono<String> deposit(@RequestHeader("Authorization") String token, @RequestParam("cntr_account_num") String accountNum, @RequestParam("wd_print_content") String wdPrintContent, @RequestParam("tran_dtime") String tranDtime, @RequestParam("bank_tran_id") String bankTranId, @RequestParam("account_holder_name") String accountHolderName, @RequestParam("tran_amt") int tranAmt, @RequestParam("req_client_name") String reqClientName, @RequestParam("withdraw_bank_tran_id") String withdrawBankTranId) {
        return transferService.deposit(token, accountNum, wdPrintContent, tranDtime, bankTranId, accountHolderName, tranAmt, reqClientName, withdrawBankTranId);
    }

}
