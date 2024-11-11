# 트래블하나 - 트래블로그에 기반한 통합 여행 쉐어 플랫폼

<img src="/image/Main.png" />

## 1. 프로젝트 개요
### 1. 기획 배경 및 필요성
<img src="/image/기획배경 및 필요성_-003.png" />

- **해외 여행객 수의 증가** : 여행에 대한 관심이 많아지는 만큼 여행에 도움이 되는 서비스에 대한 수요 증가
- **여행 관련 서비스의 분산** : 여행 계획 플래너, 여행 정보 공유, 여행 가계부 등 다양한 서비스가 등장했지만, 기능이 분산되어 있어 여행을 위해 여러 서비스를 이용해야 함
- **여행 경비 공유 사이트의 부재** : 다양한 SNS 및 커뮤니티를 통해 개인의 여행 정보 및 경비 내역 공유가 유행되고 있으나, 별도의 서비스는 존재하지 않아 직접 엑셀에 작성하는 등 경비 공유의 어려움

**&rarr; 분산되어 있는 기능을 하나로 통합하고, 여행 경비 공유를 편리하게 할 수 있는 여행 전용 플랫폼이 필요**

----

### 2. 목표
<img src="/image/목표-003.png" />

- **여행에 필요한 여러 기능을 하나로 통합한 플랫폼** : 여행에 필요한 정보 공유, 계획 플래너, 예산 관리 등 여러 기능을 하나로 통합하여 사용자들이 편리하게 여행을 즐길 수 있는 플랫폼을 제공
- **하나 금융 연계로 효율적인 여행 경비 관리** : 트래블로그 카드 내역을 기반으로 경비 내역을 공유하고, 하나은행 계좌로 모임통장을 개설하는 등 금융 연계로 효율적인 여행 경비 관리 기능을 제공

----

### 3. 기능 소개
<img src="/image/기능 소개-004.png" />

----

## 2. 진행과정
### 1. 시스템 아키텍처
> OS : Windows 11  
> Server : Tomcat 10   

<img src="/image/시스템아키텍처-008.png" />

----

### 2. 응용 기술

#### 2-1. 3-legged 인증 프로세스
<img src="/image/3-legged-009.png" />
  - 실제 오픈뱅킹 API와 동일한 3-legged 인증 방식을 구현해 사용자에 대한 Access Token을 얻음으로써 인증을 획득
  - 자동이체 및 회비납부 관리 구현 시 PL/SQL의 프로시저와 스케줄러 사용으로 복잡한 업무 처리 프로세스를 개선   


#### 2-2. PL/SQL을 통한 문제 해결
<img src="/image/plsql-010.png" />

모임서비스에서 사용자의 회비 납부 상태를 관리하기 위한 복잡한 프로세스 처리로 성능 저하 **문제 발생**  
**&rarr; PL/SQL 프로시저를 작성해 호출하는 것으로 프로세스 변경하였고 이를 통해 성능 개선**

---

### 3. 데이터 분석
- 소비패턴 분석
  - Python 및 Pandas 라이브러리를 활용한 소비데이터 분석으로 개인의 소비패턴 및 특성을 파악하고, 여행 MBTI에 분석 결과를 반영함으로써 단순 사용자 응답 기반의 MBTI 검사가 아닌 소비내역 기반의 객관적인 결과를 제공
  - Flask를 통해 트래블하나 서버와 분석 서버가 통신하며 실시간 데이터 반영

<img src="/image/소비패턴분석-011.png" />
<img src="/image/소비패턴분석-014.png" />


---

## 5. 자기소개
|        구분        | 내용                                                        | 비고                                                     |
|:----------------:|:----------------------------------------------------------|:-------------------------------------------------------|
|        성명        | 이채진                                                       | <img src="/image/3x4_ 이채진님.jpg" style="width: 100px"/> |
|       연락처        | E-mail                                                    | cowls753@naver.com                                     |
|       학력<br/>사항       | 인천대학교 정보통신공학과 졸업 (학점 : 4.0/4.5)                           | 2020.03 ~ 2024.02                                      |
|    Skill Set     | Language                                                  | C, Javascript, Java, JSP, Python                       |
|                  | Framework & Library                                       | Spring, Mybatis                                        |
|                  | Etc                                                       | Git, Oracle, AWS                                       |
|       자격증        | SQLD (SQL 개발자)                                            | 2024.06.21                                             |
|                  | ADsP (데이터분석 준전문가)                                         | 2023.11.17                                             |
|                  | 정보처리기사                                                    | 2023.11.15                                             |
|       수상<br/>경력       | 2023 관광데이터 활용 공모전 장려상                                     | 2023.11.29                                             |
|       교육<br/>이력       | 하나금융티아이 채용연계형 교육 1200시간<br/>(한국폴리텍대학교 광명융합기술교육원 - 데이터분석과) | 2024.03.04 ~ 2024.12.13 (1200시간)                       |
|              | 청년 Ai·BigData 아카데미 23기                                    | 2023.07.17 ~ 2023.09.22 (424시간)                        |
|              | 한국정보보호산업협회 융합보안 인력양성 교육<br/>(블록체인 분야)                         | 2023.06.28 ~ 2023.06.30 (21시간)                         |
