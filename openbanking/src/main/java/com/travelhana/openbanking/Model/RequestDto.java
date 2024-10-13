package com.travelhana.openbanking.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RequestDto {
    String clientId;
    String clientSecret;
    String responseType;
    String redirectUri;
    String scope;
    String state;
    String code;
}
