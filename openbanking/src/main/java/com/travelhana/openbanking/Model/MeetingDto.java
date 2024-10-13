package com.travelhana.openbanking.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class MeetingDto {
    String macnt_num;
    String account_num;
    String macnt_name;
    String account_name;
    int macnt_balance;
    int macnt_target_amount;
    int macnt_dues_date;
    int macnt_dues;
}
