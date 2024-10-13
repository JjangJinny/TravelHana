package com.travelhana.hanabank.hanabank.Model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMapper {
    List<HistoryDto> getTransactionList(String macntNum, String inquiryType, String fromDate, String toDate);
    AccountDto getAccount(String account_num);
    AccountDto getMeetingAccount(String account_num);
    void updateBalance(String accountNum, int tranAmt, String tranType);
    void insertTransferHistory(HistoryDto historyDto);
}
