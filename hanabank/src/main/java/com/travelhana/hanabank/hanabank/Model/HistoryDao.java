package com.travelhana.hanabank.hanabank.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistoryDao {
    @Autowired
    HistoryMapper historyMapper;
    @Autowired
    HistoryDto historyDto;

    public List<HistoryDto> getTransactionList(String accountNum, String inquiryType, String fromDate, String toDate) { return historyMapper.getTransactionList(accountNum, inquiryType, fromDate, toDate); }
    public AccountDto getAccount(String accountNum) { return historyMapper.getAccount(accountNum); }
    public AccountDto getMeetingAccount(String accountNum) { return historyMapper.getMeetingAccount(accountNum); }
    public void updateBalance(String accountNum, int tranAmt, String tranType) { historyMapper.updateBalance(accountNum, tranAmt, tranType); }
    public void insertTransferHistory(HistoryDto historyDto) { historyMapper.insertTransferHistory(historyDto); }
}
