package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ConsumerDto {
    String consumerName;
    String consumerNum;
    String consumerPnum;
    String consumerAddr;
    String consumerCI;
    String fintechUseNum;
    int wd_limit;
    int wd_limit_remain_amt;
}
