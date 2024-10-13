package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AutoTransferDto {
    String at_id;
    int at_date;
    int at_amount;
    String macnt_num;
    String account_num;
    String macnt_msg;
    String account_msg;
}
