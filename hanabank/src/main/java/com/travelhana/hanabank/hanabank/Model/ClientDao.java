package com.travelhana.hanabank.hanabank.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDao {
    @Autowired
    ClientMapper clientMapper;

    public String getClientSecret(String clientId) { return clientMapper.getClientSecret(clientId); }
    public void insertToken(TokenDto tokenDto) { clientMapper.insertToken(tokenDto); }
    public void refreshToken(TokenDto tokenDto) { clientMapper.refreshToken(tokenDto); }
}
