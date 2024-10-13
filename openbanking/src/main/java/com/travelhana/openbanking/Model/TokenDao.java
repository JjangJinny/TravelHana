package com.travelhana.openbanking.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenDao {
    @Autowired
    AuthInfoMapper authInfoMapper;

    public TokenDto getFintechUseNum(String userSeqNo) { return authInfoMapper.getFintechUseNum(userSeqNo); }

    public void insertToken(TokenDto tokenDto) { authInfoMapper.insertToken(tokenDto); }

    public TokenDto getAuthorizationCode(String refreshToken) { return authInfoMapper.getAuthorizationCode(refreshToken); }

    public void refreshToken(TokenDto tokenDto) { authInfoMapper.refreshToken(tokenDto); }
}
