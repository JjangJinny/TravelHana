package com.travelhana.hanabank.hanabank.Model;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ConsumerMapper {
    void insertConsumer(ConsumerDto consumerDto);
    void updateFintechUseNum(String fintechUseNum, String consumerCI);
    String getAccessToken(String fintechUseNum);
    List<AccountDto> getAccountList(String fintechUseNum);
    ConsumerDto getWdLimit(String fintechUseNum);
}
