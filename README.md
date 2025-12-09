# 호흡 운동 안드로이드 앱

피그마 디자인을 기반으로 만든 호흡 운동 안드로이드 네이티브 앱입니다.

## 주요 기능

- 🫁 **호흡 설정**: 들숨, 멈춤, 날숨 시간 및 세트 수 설정
- 💾 **프리셋 저장**: 자주 사용하는 호흡 패턴을 프리셋으로 저장
- 🎯 **호흡 가이드**: 실시간 타이머와 애니메이션으로 호흡 가이드
- 🔊 **음성 안내**: 한국어 음성 가이드 제공
- 📊 **운동 기록**: 세션 기록 및 통계 확인
- ⚙️ **설정**: 음량, 음성 가이드, 효과음 설정

## 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **아키텍처**: MVVM (ViewModel)
- **데이터 저장**: DataStore (Preferences)
- **Navigation**: Navigation Compose
- **음성**: TextToSpeech API

## 개발 환경 설정

### 요구사항

- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK 24 이상 (minSdk)
- Android SDK 34 (targetSdk)

### 프로젝트 열기

1. Android Studio를 실행합니다.
2. `File > Open`을 선택하고 프로젝트 폴더를 선택합니다.
3. Android Studio가 프로젝트를 동기화할 때까지 기다립니다.

### 빌드 및 실행

1. **디버그 빌드**:
   ```
   ./gradlew assembleDebug
   ```
   또는 Android Studio에서 `Run > Run 'app'`을 클릭합니다.

2. **릴리즈 빌드** (배포용):
   ```
   ./gradlew assembleRelease
   ```
   APK 파일은 `app/build/outputs/apk/release/` 경로에 생성됩니다.

### 안드로이드 스튜디오에서 실행

1. 에뮬레이터나 실제 기기를 연결합니다.
2. `Run` 버튼 (▶️)을 클릭하거나 `Shift + F10`을 누릅니다.
3. 앱이 빌드되고 실행됩니다.

## 프로젝트 구조

```
app/src/main/java/com/breathing/app/
├── data/
│   └── DataStore.kt          # 데이터 저장 관리
├── models/
│   ├── BreathingConfig.kt    # 호흡 설정 모델
│   ├── SessionRecord.kt      # 세션 기록 모델
│   ├── Preset.kt             # 프리셋 모델
│   └── AudioSettings.kt      # 오디오 설정 모델
├── screens/
│   ├── ConfigurationScreen.kt    # 설정 화면
│   ├── BreathingScreen.kt        # 호흡 운동 화면
│   ├── CompletionScreen.kt       # 완료 화면
│   ├── HistoryScreen.kt          # 기록 화면
│   └── SettingsModal.kt          # 설정 모달
├── utils/
│   ├── AudioManager.kt       # 음성 및 사운드 관리
│   └── DateUtils.kt          # 날짜 유틸리티
├── viewmodel/
│   └── BreathingViewModel.kt # 뷰모델
└── MainActivity.kt           # 메인 Activity
```

## 주요 화면

### 1. 설정 화면 (ConfigurationScreen)
- 호흡 패턴 설정 (들숨/멈춤/날숨 시간, 세트 수)
- 프리셋 선택 및 저장
- 빠른 선택 버튼 (4-7-8, 5-5-5, 6-6-6)

### 2. 호흡 운동 화면 (BreathingScreen)
- 실시간 타이머 및 카운터
- 애니메이션 원형 인디케이터
- 음성 안내 및 사운드 효과
- 일시정지/재개/종료 기능

### 3. 완료 화면 (CompletionScreen)
- 세션 완료 정보 표시
- 오늘 완료한 세션 수
- 다시 하기 / 홈으로 / 기록 보기

### 4. 기록 화면 (HistoryScreen)
- 이번 주 통계
- 최근 기록 목록
- 날짜별 그룹화

## 배포 준비

### 1. 키스토어 생성

```bash
keytool -genkey -v -keystore breathing-app-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias breathing-app
```

### 2. 서명 설정

`app/build.gradle.kts`에 서명 설정을 추가합니다 (릴리즈 빌드 시).

### 3. APK/AAB 빌드

- **APK**: `./gradlew assembleRelease`
- **AAB** (Google Play 배포용): `./gradlew bundleRelease`

## 라이선스

이 프로젝트는 개인 사용을 위한 것입니다.

## 참고사항

- 앱은 세로 모드(Portrait)로 고정되어 있습니다.
- 음성 안내는 한국어만 지원합니다.
- 데이터는 로컬에 저장되며 클라우드 동기화는 지원하지 않습니다.

