package com.travelhana.hanabank.hanabank.Model;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper {
    String getClientSecret(String client_id);
    void insertToken(TokenDto tokenDto);
    void refreshToken(TokenDto tokenDto);
}
