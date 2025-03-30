# 커뮤니티 플랫폼 백엔드

## 소개

이 프로젝트는 커뮤니티 서비스를 위한 백엔드 API 시스템입니다. 

RESTful 아키텍처를 기반으로 구축되었으며, JWT를 활용한 인증 메커니즘과 게시글, 댓글, 좋아요 기능을 갖춘 통합 커뮤니티 플랫폼을 제공합니다.

## 기술 스택

### 코어
- **Java 17** : 최신 Java 언어 기능 및 성능 향상 활용
- **Spring Boot 3.x** : 애플리케이션 프레임워크
- **Spring Security** : 인증 및 권한 관리
- **Spring Data JPA** : ORM 및 데이터 액세스 계층

### 데이터베이스
- **MySQL** : 관계형 데이터베이스

### 인증
- **JWT (JSON Web Token)** : 토큰 기반 인증 구현
- **BCrypt** : 안전한 비밀번호 암호화

### API
- **RESTful API** : 표준 HTTP 메서드 및 상태 코드 준수
- **DTO 패턴** : API 요청/응답 데이터 캡슐화

### 개발 도구 및 라이브러리
- **Lombok** : 간결한 코드 작성
- **Slf4j** : 로깅 프레임워크
- **Spring Validation** : 입력 데이터 유효성 검증


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

## 코드 품질 및 최적화

### 보안 최적화
- 비밀번호 암호화 (BCrypt) 적용
- JWT 토큰 유효기간 및 서명 검증
- 권한 기반 리소스 접근 제어
- 입력 데이터 검증 및 이스케이핑

### 성능 최적화
- 적절한 인덱싱 (userId, postId 등)
- 트랜잭션 범위 최소화
- 페이지네이션을 통한 대량 데이터 처리
- N+1 문제 회피를 위한 쿼리 최적화

### 코드 품질
- 일관된 예외 처리 및 로깅
- 명확한 계층 분리
- 재사용 가능한 컴포넌트
- 가독성 높은 코드 스타일

## 설치 및 실행 방법

### 사전 요구사항
- JDK 17 이상
- Maven 또는 Gradle
- MySQL 8.0 이상

### 설치 단계
1. 저장소 클론
   ```bash
   git clone https://github.com/yourusername/community-spring.git
   cd community-spring
   ```

2. 데이터베이스 설정
   ```sql
   CREATE DATABASE community_spring;
   USE community_spring;
   ```

3. application.properties 구성 (src/main/resources/application.properties)
   ```properties
   # 데이터베이스 연결 설정
   spring.datasource.url=jdbc:mysql://localhost:3306/community_spring?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JPA 설정
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   
   # JWT 설정
   jwt.secret=your_very_long_and_secure_secret_key_for_jwt_token_generation
   jwt.expiration=86400000
   ```

4. 애플리케이션 빌드
   ```bash
   ./mvnw clean package
   ```

5. 애플리케이션 실행
   ```bash
   java -jar target/community-spring-0.0.1-SNAPSHOT.jar
   ```

## 후기

본 프로젝트는 현대적인 Spring 기반 백엔드 시스템의 구축 과정에서 얻은 다양한 인사이트를 담고 있습니다. 특히 아래 항목들에 중점을 두었습니다:

1. **계층화된 아키텍처**: 책임과 관심사를 명확하게 분리하여 유지보수성과 테스트 용이성을 극대화했습니다.

2. **보안 최적화**: JWT 토큰을 기반으로 한 인증 시스템은 효율적이면서도 안전한 사용자 인증을 가능하게 합니다.

3. **비즈니스 로직 중심**: 각 서비스 레이어에서 비즈니스 규칙을 명확하게 구현하여 일관된 애플리케이션 동작을 보장합니다.

4. **API 설계 표준화**: 모든 API 응답은 일관된 형식(ApiResponse)을 따르며, 적절한 HTTP 상태 코드와 메시지를 제공합니다.

5. **예외 처리 전략**: 중앙화된 예외 처리 메커니즘을 통해 클라이언트에게 유용한 오류 정보를 제공합니다.

### 개선 가능성

향후 다음과 같은 개선사항을 고려할 수 있습니다:

1. **캐싱 레이어 추가**: Redis와 같은 캐시 저장소를 도입하여 자주 접근하는 데이터의 성능 향상
2. **검색 기능 강화**: Elasticsearch 통합으로 전문 검색 기능 제공
3. **파일 업로드 최적화**: AWS S3 연동을 통한 확장 가능한 이미지 저장소 구현
4. **API 문서화**: Swagger/OpenAPI 통합으로 자동화된 API 문서 제공
5. **마이크로서비스 아키텍처 검토**: 일부 기능을 독립적인 서비스로 분리하여 확장성 개선

본 프로젝트는 완전한 커뮤니티 플랫폼 구축을 위한 견고한 백엔드 기반을 제공하며, 확장성과 유지보수성을 고려한 설계로 향후 기능 추가에도 유연하게 대응할 수 있습니다.