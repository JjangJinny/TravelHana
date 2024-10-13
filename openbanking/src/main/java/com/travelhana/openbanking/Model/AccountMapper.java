package com.travelhana.openbanking.Model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    void insertAccountInfo(String account_num, String account_name, String user_seq_no);
    List<MeetingDto> getAccountList(String user_seq_no);
    List<String> getMeetingAccountList(String user_seq_no);
    void addMeetingMember(String account_num, String user_seq_no);
    String getTranLog(String bank_tran_id);
}
