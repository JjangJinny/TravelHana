package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AccountDto {
    String account_num;
    String account_pw;
    String consumer_ci;
    String account_name;
    String holder_name;
    String account_type;
    int account_balance;
    String opening_date;
    String status;
    double interest_rate;
}
