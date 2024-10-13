package com.travelhana.hanabank.hanabank.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsumerDao {
    @Autowired
    ConsumerMapper consumerMapper;
    @Autowired
    ConsumerDto consumerDto;

    public void insertConsumer(ConsumerDto consumerDto) { consumerMapper.insertConsumer(consumerDto); }
    public void updateFintechUseNum(String fintechUseNum, String consumerCI) { consumerMapper.updateFintechUseNum(fintechUseNum, consumerCI); }
    public String getAccessToken(String fintechUseNum) { return consumerMapper.getAccessToken(fintechUseNum); }
    public List<AccountDto> getAccountList(String fintechUseNum) { return consumerMapper.getAccountList(fintechUseNum); }
    public ConsumerDto getWdLimit(String fintechUseNum) { return consumerMapper.getWdLimit(fintechUseNum); }
}

