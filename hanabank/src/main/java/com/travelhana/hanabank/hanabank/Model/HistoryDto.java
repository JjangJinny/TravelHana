package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class HistoryDto {
    String hstry_id;
    String account_num;
    String branch_name;
    int tran_amt;
    int balance_amt;
    String tran_memo;
    String tran_date;
    String inout_type;
    int account_seq;
}
