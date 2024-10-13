package com.travelhana.openbanking.Model;

import com.travelhana.openbanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountDao {
    @Autowired
    AccountMapper accountMapper;

    public void insertAccountInfo(String accountNum, String accountName, String userSeqNo) { accountMapper.insertAccountInfo(accountNum, accountName, userSeqNo); }

    public List<MeetingDto> getAccountList(String userSeqNum) { return accountMapper.getAccountList(userSeqNum); }

    public List<String> getMeetingAccountList(String userSeqNum) { return accountMapper.getMeetingAccountList(userSeqNum); }

    public void addMeetingMember(String accountNum, String userSeqNo) { accountMapper.addMeetingMember(accountNum, userSeqNo); }

    public String getTranLog(String bankTranId) { return accountMapper.getTranLog(bankTranId); }
}
