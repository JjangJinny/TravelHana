package com.travelhana.openbanking.controller;

import com.travelhana.openbanking.Model.TokenDto;
import com.travelhana.openbanking.service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/oauth")
public class OauthController {
    @Autowired
    OauthService oauthService;

    @RequestMapping("/authorize")
    public RedirectView oauth(@RequestParam("response_type") String responseType,
                              @RequestParam("client_id") String clientId,
                              @RequestParam("redirect_uri") String redirectUri,
                              @RequestParam("scope") String scope,
                              @RequestParam("state") String state) {
        return oauthService.oauth(responseType, clientId, redirectUri, scope, state);
    }

    @RequestMapping("/token")
    @ResponseBody
    public Mono<TokenDto> token(@RequestParam(value = "code", required = false) String code,
                                @RequestParam("client_id") String clientId,
                                @RequestParam("client_secret") String clientSecret,
                                @RequestParam(value = "refresh_token", required = false) String refreshToken,
                                @RequestParam("redirect_uri") String redirectUri,
                                @RequestParam("state") String state) {
        if (code != null) {
            return oauthService.token(code, clientId, clientSecret, redirectUri, state);
        } else if (refreshToken != null) {
            return oauthService.refreshToken(clientId, clientSecret, refreshToken, redirectUri, state);
        }
        return Mono.empty();
    }

    @RequestMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code, @RequestParam("client_id") String clientId, @RequestParam("state") String state, @RequestParam("scope") String scope) {
        return oauthService.callback(code, clientId, state, scope);
    }
}
