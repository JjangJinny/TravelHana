package com.travelhana.openbanking.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDao {
    @Autowired
    ClientMapper clientMapper;

    public RequestDto getRedirectUri(String clientId) { return clientMapper.getRedirectUri(clientId); }
}
