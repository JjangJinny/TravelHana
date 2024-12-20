package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TokenDto {
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String fintech_use_num;
    private String client_id;
    private String generation_date;
    private String token_type;
    private String user_seq_no;
}
