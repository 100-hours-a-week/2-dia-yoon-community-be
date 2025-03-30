# 커뮤니티 플랫폼 백엔드

## 소개

이 프로젝트는 커뮤니티 서비스를 위한 백엔드 API 시스템입니다. 

RESTful 아키텍처를 기반으로 구축되었으며, JWT를 활용한 인증 메커니즘과 게시글, 댓글, 좋아요 기능을 갖춘 통합 커뮤니티 플랫폼을 제공합니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| **언어 및 프레임워크** | Java 17, Spring Boot 3.x |
| **보안** | Spring Security, JWT, BCrypt |
| **데이터베이스** | MySQL, Spring Data JPA, Hibernate |
| **API 설계** | RESTful API, DTO 패턴 |
| **유틸리티** | Lombok, Slf4j, Spring Validation |
| **인프라/기타** | CORS 구성, Transaction 관리, Global Exception Handling |

## 주요 기능

### 사용자 인증 및 관리
- **회원가입** : 이메일, 비밀번호, 닉네임 기반 계정 생성
- **로그인** : JWT 토큰 발급 및 인증
- **토큰 검증** : 유효한 세션 확인
- **이메일/닉네임 중복 확인** : 회원가입 전 유효성 검증
- **프로필 관리** : 개인 정보 조회 및 수정
- **비밀번호 변경** : 보안 강화를 위한 정기 비밀번호 변경 지원
- **회원 탈퇴** : 데이터 정리와 함께 안전한 탈퇴 처리

### 게시글 관리
- **게시글 목록 조회** : 페이지네이션 및 정렬 기능 제공
- **게시글 상세 조회** : 조회수 추적 기능 포함
- **게시글 작성** : 제목, 내용, 이미지 업로드 지원
- **게시글 수정** : 작성자 권한 검증 및 내용 갱신
- **게시글 삭제** : 연관 데이터(댓글, 좋아요) 정리 로직 포함
- **사용자별 게시글 조회** : 특정 사용자의 활동 조회

### 댓글 시스템
- **댓글 목록 조회** : 게시글별 댓글 타임라인 제공
- **댓글 작성** : 게시글에 의견 추가
- **댓글 수정/삭제** : 작성자 권한 관리
- **댓글 작성자 정보 연동** : 사용자 프로필 정보 노출

### 좋아요 시스템
- **좋아요 토글** : 추가/취소 기능 통합 처리
- **좋아요 상태 조회** : 사용자별 좋아요 상태 확인
- **좋아요 수 집계** : 게시글별 인기도 측정

### 보안 및 오류 처리
- **글로벌 예외 처리** : 일관된 에러 응답 형식
- **토큰 기반 인증** : API 엔드포인트 보호
- **입력 데이터 검증** : 클라이언트 요청 유효성 검사
- **적절한 HTTP 상태 코드** : RESTful 원칙 준수

## 프로젝트 구조

```
com.example.community_spring/
│
├── auth/                              # 인증 관련 컴포넌트
│   ├── AuthController.java           # 인증 API 엔드포인트
│   ├── AuthService.java              # 인증 서비스 인터페이스
│   └── AuthServiceImpl.java          # 인증 서비스 구현체
│
├── config/                            # 애플리케이션 구성
│   ├── JwtProperties.java            # JWT 설정값
│   ├── SecurityConfig.java           # Spring Security 구성
│   └── WebConfig.java                # 웹 애플리케이션 구성(CORS 등)
│
├── exception/                         # 예외 처리
│   ├── GlobalExceptionHandler.java   # 전역 예외 핸들러
│   └── UnauthorizedException.java    # 인증 예외 클래스
│
├── Post/                              # 게시글 관련 컴포넌트
│   ├── Controller/                   
│   │   ├── PostController.java       # 게시글 API
│   │   ├── CommentController.java    # 댓글 API
│   │   └── LikesController.java      # 좋아요 API
│   │
│   ├── DTO/                          # 데이터 전송 객체
│   │   ├── request/                  # 요청 DTO
│   │   └── response/                 # 응답 DTO
│   │
│   ├── Entity/                       # 엔티티 클래스
│   │   ├── Post.java                 # 게시글 엔티티
│   │   ├── Comment.java              # 댓글 엔티티
│   │   └── Likes.java                # 좋아요 엔티티
│   │
│   ├── Repository/                   # 데이터 액세스
│   │   ├── PostRepository.java       
│   │   ├── CommentRepository.java    
│   │   └── LikesRepository.java      
│   │
│   └── Service/                      # 비즈니스 로직
│       ├── PostService.java         
│       ├── CommentService.java       
│       └── LikesService.java         
│
├── User/                              # 사용자 관련 컴포넌트
│   ├── Controller/                    
│   │   └── UserController.java       # 사용자 API
│   │
│   ├── DTO/                          # 데이터 전송 객체
│   │   ├── request/                  # 요청 DTO
│   │   └── response/                 # 응답 DTO
│   │
│   ├── Entity/                       # 엔티티 클래스
│   │   └── User.java                 # 사용자 엔티티
│   │
│   ├── Repository/                   # 데이터 액세스
│   │   └── UserRepository.java       
│   │
│   └── Service/                      # 비즈니스 로직
│       └── UserService.java          
│
└── util/                              # 유틸리티 클래스
    └── JwtTokenProvider.java         # JWT 생성 및 검증
```

## ERD (Entity Relationship Diagram)
<img width="1037" alt="Image" src="https://github.com/user-attachments/assets/b7f0175a-7471-453d-82da-707b3f7a736a" />

### 테이블 설명

#### User 테이블
- 사용자 계정 정보 관리
- 이메일을 유니크 식별자로 활용
- 비밀번호는 BCrypt로 암호화하여 저장
- 프로필 이미지 URL 저장
- 생성 및 수정 타임스탬프 자동 관리

#### Post 테이블
- 커뮤니티 게시글 정보
- 작성자(userId)와 1:N 관계
- 제목, 내용, 이미지 정보 저장
- 조회수 및 좋아요 수 카운터 유지
- 생성 타임스탬프 관리

#### Comment 테이블
- 게시글에 대한 댓글 정보
- 게시글(postId) 및 작성자(userId)와 관계
- 댓글 내용 및 작성 시간 저장
- 게시글 삭제 시 연계 삭제(cascade)

#### Likes 테이블
- 게시글 좋아요 정보 저장
- postId와 userId의 복합 유니크 제약조건
- 게시글 좋아요 수 집계에 활용
- 게시글 삭제 시 연계 삭제(cascade)

## API 명세

### 인증 API

| 메서드 | 엔드포인트 | 설명 | 권한 | 상태 코드 |
|-------|------------|------|------|----------|
| POST | /api/auth/register | 회원가입 | 없음 | 201 Created |
| POST | /api/auth/login | 로그인 | 없음 | 200 OK |
| GET | /api/auth/validate | 토큰 검증 | 토큰 | 200 OK |
| GET | /api/auth/check-email | 이메일 중복 확인 | 없음 | 200 OK |
| GET | /api/auth/check-nickname | 닉네임 중복 확인 | 없음 | 200 OK |

### 사용자 API

| 메서드 | 엔드포인트 | 설명 | 권한 | 상태 코드 |
|-------|------------|------|------|----------|
| GET | /api/users/profile | 프로필 조회 | 토큰 | 200 OK |
| GET | /api/users/me | 현재 사용자 정보 | 토큰 | 200 OK |
| PUT | /api/users/profile | 프로필 수정 | 토큰 | 200 OK |
| PUT | /api/users/password | 비밀번호 변경 | 토큰 | 200 OK |
| DELETE | /api/users | 회원 탈퇴 | 토큰 | 200 OK |
| POST | /api/users/logout | 로그아웃 | 토큰 | 200 OK |

### 게시글 API

| 메서드 | 엔드포인트 | 설명 | 권한 | 상태 코드 |
|-------|------------|------|------|----------|
| GET | /api/posts | 게시글 목록 조회 | 없음 | 200 OK |
| GET | /api/posts/{postId} | 게시글 상세 조회 | 없음 | 200 OK |
| POST | /api/posts | 게시글 작성 | 토큰 | 201 Created |
| PUT | /api/posts/{postId} | 게시글 수정 | 토큰(작성자) | 200 OK |
| DELETE | /api/posts/{postId} | 게시글 삭제 | 토큰(작성자) | 200 OK |
| GET | /api/posts/user/{userId} | 유저 게시글 목록 | 없음 | 200 OK |

### 댓글 API

| 메서드 | 엔드포인트 | 설명 | 권한 | 상태 코드 |
|-------|------------|------|------|----------|
| GET | /api/posts/{postId}/comments | 댓글 목록 조회 | 없음 | 200 OK |
| POST | /api/posts/{postId}/comments | 댓글 작성 | 토큰 | 201 Created |
| PUT | /api/comments/{commentId} | 댓글 수정 | 토큰(작성자) | 200 OK |
| DELETE | /api/comments/{commentId} | 댓글 삭제 | 토큰(작성자) | 200 OK |

### 좋아요 API

| 메서드 | 엔드포인트 | 설명 | 권한 | 상태 코드 |
|-------|------------|------|------|----------|
| POST | /api/posts/{postId}/likes | 좋아요 토글 | 토큰 | 200 OK |
| GET | /api/posts/{postId}/likes/status | 좋아요 상태 조회 | 토큰 | 200 OK |


## 후기

2주 간 프로젝트를 진행하면서 많이 성장할 수 있었습니다. 기회가 된다면 고도화를 진행하고 싶었지만, 진행하지 못함에 많은 아쉬움이 남습니다. 
이전에 백엔드 개발 경험은 있었지만, 프론트도 오랜만에 하고 연동은 처음 하는 것이라서 많은 어려움이 존재했습니다. API 명세서를 자세하게 작성했다고 생각했는데 개발 과정에서 더욱 자세한 API를 요구함을 알게되었습니다. 이후에 개발할 때는 설계를 탄탄하게 해야겠다고 느꼈습니다. 

프론트엔드를 개발할 때, 공통되는 헤더를 페이지별로 중복되게 짰었습니다. 그러나 백엔드 연동 과정에서 동일한 코드들을 계속 바꿔줘야 하는 것을 인식하고 Header 라는 공통 코드를 작성하게 되었습니다. 처음 프론트 개발을 할 때 미리 처리했더라면, 연동에서 시간 단축이 되었을텐데 아쉬웠습니다. 

그리고 코드를 짤 때는 개발을 완성하는 것에 집중해서 가독성이나 코드의 타당성에 대해 생각하지 못했습니다. 이후 케빈의 피드백을 받고 효율적인 코드로 수정하면 좋을 것 같습니다. 

### 개선 가능성

향후 다음과 같은 고도화를 진행하고 싶습니다. 

1. **효율적인 코드**
2. **AWS를 이용한 이미지 처리**
3. **비동기 적용**
4. **유저 간 채팅 적용**
