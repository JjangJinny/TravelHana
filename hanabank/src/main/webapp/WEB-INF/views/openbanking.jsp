<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
    <link rel="stylesheet" type="text/css" href="style/header.css" />
    <link rel="stylesheet" type="text/css" href="style/joinMeeting.css" />
    <link rel="stylesheet" type="text/css" href="style/connect.css" />
    <title>트래블하나</title>
    <%
        String userId = (String) session.getAttribute("user_id");
        String userNm = (String) session.getAttribute("user_name");
    %>
</head>
<body>
<div class="frame">
    <header>
    </header>
    <div class="container">
        <div class="container-top">
            <a href="./" class="logo"></a>
            <h2>오픈뱅킹 가입</h2>
            <div class="connect-note">
                <li>오픈뱅킹은 다른 금융기관 계좌 조회/이체, 상품 가입, 카드사 청구내역 및 선불기관(핀테크사) 이용내역 확인 등 금융 거래를 할 수 있는 서비스입니다.</li>
                <li>보이스피싱 피해 방지를 위해 오픈뱅킹 최초 계좌 등록일 포함 3일간 이체 거래가 제한됩니다.</li>
            </div>
            <form action="/userAuth" method="post" id="connect-card-form">
                <div class="sub-section">
                    <div class="sub-section-title">
                        <h4>약관동의</h4>
                    </div>
                    <div class="condition-wrapper">
                        <div class="condition">
                            <span>[ 계좌통합관리 서비스 ] 이용약관</span>
                            <div class="condition-btn-wrapper">
                                <button type="button" id="view-10028">약관보기</button>
                                <a href="./file/com10028.pdf" download></a>
                            </div>
                        </div>
                        <div class="condition">
                            <span>정보수집 · 활용 동의</span>
                            <div class="condition-btn-wrapper">
                                <button type="button" id="view-00027">약관보기</button>
                                <a href="./file/com00027.pdf" download></a>
                            </div>
                        </div>
                    </div>
                    <div>
                        <input type="checkbox" id="condition-ok" disabled required>
                        <label id="condition-ok-label" for="condition-ok">본인은 위 안내에 대해 확인하고 이해합니다.</label>
                    </div>
                </div>
                <div class="sub-section">
                    <div class="sub-section-title">
                        <h4>본인인증</h4>
                    </div>
                    <div class="input-container">
                        <input type="text" name="consumerName" id="consumerName" placeholder="이름" required>
                    </div>
                    <div class="input-container">
                        <input type="tel" id="consumerNumFront" placeholder="주민등록번호" required>
                        <span style="margin: 20px">-</span>
                        <input type="password"  id="consumerNumBack" required>
                        <input type="hidden" name="consumerNum" id="consumerNum">
                    </div>
                    <div class="authentication">
                        <select class="sel-tele" required>
                            <option selected disabled>통신사</option>
                            <option>KT</option>
                            <option>SKT</option>
                            <option>LG U+</option>
                            <option>KT 알뜰폰</option>
                            <option>SKT 알뜰폰</option>
                            <option>LG U+ 알뜰폰</option>
                        </select>
                        <div>
                            <select id="phone-first">
                                <option selected>010</option>
                            </select>
                            <input type="tel" id="phone-middle" required>
                            <input type="tel" id="phone-last" required>
                            <input type="hidden" name="consumerPnum" id="consumerPnum">
                            <button type="button" id="btn-connect-confirm" disabled>확인</button>
                        </div>
                    </div>
                    <div class="authentication-api">
                        <input type="tel" id="auth-num" required>
                        <input type="checkbox" id="auth-check" required><button type="button" id="auth-btn">인증</button>
                    </div>
                </div>
                <div class="btn-wrapper">
                    <button type="button" id="btn-cancel">취소</button>
                    <button type="submit" id="btn-next" disabled>완료</button>
                </div>
            </form>
        </div>
    </div>
</div>
<footer>
    <div id="footer-top">
        <div id="quick-links">
            <ul style="padding: 0 0 20px;">
                <li><a href="#">이용약관</a></li>
                <span>|</span>
                <li><a href="#">개인정보처리방침</a></li>
                <span>|</span>
                <li><a href="#">고객지원</a></li>
                <span>|</span>
                <li><a href="#">1:1 문의</a></li>
            </ul>
            <p>경기도 광명시 철산동 220-1</p>
            <p>2460340018@office.kopo.ac.kr</p>
        </div>

        <div id="service-logo">
            <img src="/image/hana_logo.png">
        </div>
    </div>

    <div id="footer-bottom" style="text-align: center; padding-top: 20px;">
        <p>&copy; 2024 TravelHana. All Rights Reserved.</p>
    </div>
</footer>
<script src="./scripts/joinMetting_script.js"></script>
<script src="./scripts/connect_script.js"></script>
<script src="./scripts/userAuthRequest.js"></script>
</body>
</html>
