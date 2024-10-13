package com.travelhana.hanabank.hanabank.Model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MeetingMapper {
    void insertMeeting(MeetingDto meetingDto);
    void insertMeetingUser(String macntNum, String fintechUseNum);
    MeetingDto getMeetingAccount(String macntNum);
    List<MeetingDto> getMeetingAccountList(List<String> macntNumList);
    void insertAutoTransfer(@Param("accountNum") String accountNum,@Param("macntNum") String macntNum, @Param("atAmount") int atAmount, @Param("atDate") int atDate, @Param("accountMsg") String accountMsg, @Param("macntMsg") String macntMsg);
    AutoTransferDto getAutoTransfer(String macnt_num);
    List<MeetingDto> getMeetingMembers(List<String> macntNumList);
    AccountDto getAccountInfo(String account_num);
    AccountDto getMeetingAccountInfo(String macnt_num);
}
