package com.travelhana.hanabank.hanabank.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeetingDao {
    @Autowired
    MeetingMapper meetingMapper;

    public void insertMeeting(MeetingDto meetingDto) { meetingMapper.insertMeeting(meetingDto); }

    public void insertMeetingUser(String macntNum, String fintechUseNum) { meetingMapper.insertMeetingUser(macntNum, fintechUseNum); }

    public MeetingDto getMeetingAccount(String macntNum) { return meetingMapper.getMeetingAccount(macntNum); }

    public List<MeetingDto> getMeetingAccountList(List<String> macntNumList) { return meetingMapper.getMeetingAccountList(macntNumList); }

    public void insertAutoTransfer(String accountNum, String macntNum, int atAmount, int atDate, String accountMsg, String macntMsg)  { meetingMapper.insertAutoTransfer(accountNum, macntNum, atAmount, atDate, accountMsg, macntMsg); }

    public AutoTransferDto getAutoTransfer(String macntNum) { return meetingMapper.getAutoTransfer(macntNum); }

    public List<MeetingDto> getMeetingMembers(List<String> macntNumList) { return meetingMapper.getMeetingMembers(macntNumList); }

    public AccountDto getAccountInfo(String accountNum) { return meetingMapper.getAccountInfo(accountNum); }

    public AccountDto getMeetingAccountInfo(String macntNum) { return meetingMapper.getMeetingAccountInfo(macntNum); }
}
