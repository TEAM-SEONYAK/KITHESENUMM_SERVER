# ☀️💊 SEONYAK (선약)

<img width="100%" alt="main_image_seonyak" src="https://github.com/user-attachments/assets/dae4d7c4-8973-401e-9cda-7b505d9443d6">


```
" 더 큰 세상에서 걸음마를 뗄 후배들이 선배의 손을 잡고 뛸 수 있도록 "
같은 계열 '선'배와의 진로 상담 '약'속, 선약

ㄴ 같은 경험을 가진 선배와의 진로 상담을 통해 더 맞춤화된 조언을 받을 수 있도록
ㄴ 같은 계열/원하는 직무의 선배를 터치 한번으로 더 쉽게 찾을 수 있도록
```

<br/>

## Backend Developers ☁️

<div align="center">
<table>
    <tr>
        <td><img src="https://github.com/TEAM-SEONYAK/SEONYAK-SERVER/assets/81475587/6e98ef2a-efe5-4883-b283-c8bd81c8d8ac" width="500vh"></td>
        <td><img src="https://github.com/TEAM-SEONYAK/SEONYAK-SERVER/assets/81475587/bd6d3bd4-72d0-4eef-a4c0-f0f87891a6df" width="500vh"></td>
    </tr>
    <tr>
        <td align="center"> 김창균 <a href="https://github.com/ckkim817"><img src="https://img.shields.io/badge/Github-181717?style=flat-square&logo=Github&logoColor=white"/></a></td>
        <td align="center"> 홍석범 <a href="https://github.com/seokbeom00"><img src="https://img.shields.io/badge/Github-181717?style=flat-square&logo=Github&logoColor=white"/></a></td>
    </tr>
</table>
</div>

`김창균` - EC2, RDS 세팅 / 컨벤션, 엔티티 작성 / 구글 소셜 로그인 연동 / 문자 발송을 위한 외부 API 연동 / API 개발

`홍석범` - 응답, 에러 처리를 포함한 프로젝트 초기 세팅 / DOCKER CI, CD 세팅 / 네이버 OCR, 구글 미트 회의실 개설 등 외부 API 연동 / API 개발

<br/>

## Tech Stack ✨

| **Category**       | **Details**                                   |
|--------------------|-----------------------------------------------|
| **IDE**            | IntelliJ                                      |
| **Language**       | Java 17                                       |
| **Framework**      | Spring Boot 3.3.1, Gradle                     |
| **Authentication** | Spring Security, JSON Web Tokens              |
| **ORM**            | Spring Data JPA, QueryDSL                     |
| **Database**       | PostgreSQL                                    |
| **In-Memory**      | Redis (Cache)                                 |
| **Infra**          | AWS EC2, AWS RDS, AWS Route 53, Nginx, Docker |
| **CI/CD**          | GitHub Actions                                |
| **Storage**        | Amazon S3                                     |
| **Scheduling**     | Spring Scheduler                              |
| **Email**          | Spring Boot Mail                              |
| **External API**   | CoolSMS SDK, OpenFeign, Google Cloud Meet SDK |
| **API Docs**       | Notion, Swagger                               |
| **Other Tools**    | DataGrip, Discord, Figma, Postman             |

<br/>

## 1. Git Convention

> **Git Flow**

<img width="100%" alt="git_flow_seonyak" src="https://github.com/TEAM-SEONYAK/SEONYAK-SERVER/assets/81475587/7220369b-d515-4828-b496-1c907cdc7506">

- `main` 운영서버 브랜치
- `hotfix` 핫픽스용 브랜치
- `develop` 개발서버 브랜치
- `prefix/#이슈번호` 작업을 진행 중인 브랜치

<br/>

> **Branch Naming Rule**

- 이슈를 먼저 생성하고 부여받은 이슈 번호를 바탕으로 브랜치를 생성합니다.
- **여기서의 `prefix`는 전부 소문자를 사용합니다.**

```
prefix/#이슈번호

ex) feat/#8
ex) fix/#17
```

<br/>

> **Commit Convention**

- **여기서의 `Prefix`는 전부 대문자를 사용합니다.**

```
[PREFIX/#이슈번호] 작업내용

ex) [FEAT/#8] 단어 리스트 조회 API 구현
ex) [FIX/#17] 검색 결과 필터링 기능 오류 수정
```

<br/>

> **Prefix Convention**

- **`branch`에서의 `prefix`는 전부 소문자를 사용합니다.**
- **`label`은 전부 대문자를 사용합니다.**
- 한 commit에서는 해당 prefix type에 해당하는 내용만 갖도록 commit을 최대한 분리합니다.

| prefix types | 의미 |
| --- | --- |
| ✨ FEAT | 새로운 기능 추가 |
| 🛠️ FIX | 버그, 오류 등을 수정 |
| 🔀 MERGE | 다른 branch와 병합 |
| ⚙️ SETTING | 프로젝트 초기 세팅 |
| 🧩 CHORE | 기능, 성능과 직접적으로 관련이 없는 작업(초기 세팅 이후) ex) .gitignore, 의존성 추가 등 |
| 📝 DOCS | README나 WIKI 등의 문서 수정 |
| ✏️ CORRECT | 문법 상 오류나 타입, 변수명 등을 변경 |
| 📂 FILE | 폴더링 변경 |
| 🔁 RENAME | 파일 이름 변경 |
| ♻️ REFACTOR | 코드 리팩토링 |
| ⚰️ DEL | 쓸모없는 코드 삭제 |
| 💡 TEST | 테스트 코드, 리팩토링 테스트 코드 작성 |
| 🔥 !HOTFIX | 치명적인 버그가 발생하여 급하게 수정 |

<br/>

> **Issue Template**

- [PREFIX] 이슈내용
- **여기서의 `PREFIX`는 전부 대문자를 사용합니다.**

```
## 💡 Issue
<!-- 이슈에 대해 간략하게 설명해 주세요 -->
- 

## 📝 To-do
<!-- 진행할 작업에 대해 적어주세요 -->
<!--
- [ ] 도메인 모델(Entity) 정의
- [ ] DTO(Record) 정의
- [ ] Repository 인터페이스 생성
- [ ] Service 클래스 작성
- [ ] Controller 클래스 작성
- [ ] 공통 응답 처리 및 예외 처리
- [ ] Swagger 테스트
-->
- [ ] 
```

<br/>

> **Pull Request Template**

- [PREFIX] #이슈번호 - PR내용
- **여기서의 `PREFIX`는 전부 대문자를 사용합니다.**

```
# 💡 Issue
- resolved: #이슈번호

# 📸 Screenshot
<!-- 필요 시 사진, 동영상 등을 첨부해 주세요
ex) 포스트맨, 스웨거, 로그 등 -->

# 📄 Description
<!-- 작업한 내용에 대해 상세하게 설명해 주세요,
코드를 첨부할 경우 permalink를 사용해 주세요 -->
- 

# 💬 To Reviewers
<!-- 리뷰어들에게 남기고 싶은 말을 적어주세요
ex) 코드 리뷰 간 참고사항, 질문 등 -->
- 

# 🔗 Reference
<!-- 이슈를 해결하며 도움이 되었거나, 참고했던 아티클들의 링크를 첨부해 주세요 -->
- 
```

<br/>

## 2. **Package Structure**

> **Package Structure**

```
계층형 구조 vs 도메인형 구조
- 계층형 구조
  - 규모가 작고, 도메인이 적은 경우
    - 계층형 패키지 안에 클래스들이 구분이 안 될 만큼 많아질 경우가 적다.
    - 애플리케이션 흐름 및 가독성이 도메인형보다 좋다.
    - 유스케이스별로 클래스를 분리하는 경우가 적다.
    - 도메인의 변경이 일어나도, 규모가 작고 도메인이 적은 만큼 변경 범위가 그렇게 넓지 않을 것이다.
    
- 도메인형 구조
  - 규모가 크고, 도메인이 많은 경우
    - 규모가 크고 도메인이 많은 만큼 도메인별 응집도가 높은 것이 중요할 것이다.
    - 규모가 큰 만큼 유스케이스별로 클래스를 분리하는 경우가 있을 수 있다.
```

- **도메인형 구조를 사용**합니다.
- `Enum` 클래스는 `model` 패키지 안에 포함합니다.

```
├── build.gradle
├── 📂 src/main/java/org/sopt/seonyakServer
│       ├── 📂 domain
│       │       ├── 📂 entity1
│       │       │       ├── 📂 controller
│       │       │       ├── 📂 dto
│       │       │       │       ├── 📂 request
│       │       │       │       └── 📂 response
│       │       │       │
│       │       │       ├── 📂 model
│       │       │       ├── 📂 repository
│       │       │       └── 📂 service
│       │       │
│       │       └── 📂 entity2
│       │       │       ├── 📂 controller
│       │       │       ├── 📂 dto
│       │       │       │       ├── 📂 request
│       │       │       │       └── 📂 response
│       │       │       │
│       │       │       ├── 📂 model
│       │       │       ├── 📂 repository
│       │       │       └── 📂 service
│       │       │
│       │       └── 📂 utils
│       │
│       └── 📂 global
│               ├── 📂 auth
│               │       ├── 📂 filter
│               │       ├── 📂 jwt
│               │       ├── 📂 redis
│               │       └── 📂 security
│               │
│               ├── 📂 common
│               │       ├── 📂 dto
│               │       ├── 📂 external
│               │       └── 📂 model
│               │
│               ├── 📂 config
│               └── 📂 exception
│                       ├── 📂 enums
│                       ├── 📂 model(CustomException)
│                       └── GlobalExceptionHandler.java
│
└── SeonyakServerApplication.java
```

<br/>

## 3. **Architecture**

<img width="100%" alt="architecture_diagram_seonyak" src="https://github.com/user-attachments/assets/25583467-131a-48e5-8a2a-4a9631a6e0c6">

<br/>

## 4. **ERD**

<img width="100%" alt="erd_seonyak" src="https://github.com/user-attachments/assets/650039e6-f084-47f4-9ca1-6c1c9ce854f5">
