package com.travelhana.openbanking.Model;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthInfoMapper {
    TokenDto getFintechUseNum(String user_seq_no);
    void insertToken(TokenDto tokenDto);
    void refreshToken(TokenDto tokenDto);
    TokenDto getAuthorizationCode(String refresh_token);
}
