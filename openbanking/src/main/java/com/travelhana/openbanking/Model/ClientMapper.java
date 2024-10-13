package com.travelhana.openbanking.Model;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper {
    RequestDto getRedirectUri(String client_id);
}
