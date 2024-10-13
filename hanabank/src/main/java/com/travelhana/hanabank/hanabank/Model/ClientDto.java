package com.travelhana.hanabank.hanabank.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ClientDto {
    String clientId;
    String clientSecret;
    String clientName;
}
