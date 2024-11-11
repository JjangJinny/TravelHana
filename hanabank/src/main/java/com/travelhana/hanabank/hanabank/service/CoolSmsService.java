package com.travelhana.hanabank.hanabank.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CoolSmsService {
    private String apiKey = API_KEY
    private String apiSecret = API_SECRET
    private String fromPhoneNumber = PHONE_NUM
    final DefaultMessageService defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

    public String sendSms(String to)  {
        try {
            // 랜덤한 6자리 인증번호 생성
            String numStr = generateRandomNumber();
            Message coolsms = new Message();

            coolsms.setFrom(fromPhoneNumber);
            coolsms.setTo(to);
            coolsms.setText("[하나은행] 오픈뱅킹 가입을 위한 본인확인 인증번호 [" + numStr + "]을 정확하게 입력해주세요.");
            defaultMessageService.sendOne(new SingleMessageSendingRequest(coolsms));

            return numStr;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 랜덤한 6자리 숫자 생성 메서드
    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }
}
