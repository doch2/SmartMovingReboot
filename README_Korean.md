[![](http://cf.way2muchnoise.eu/full_449519_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/smart-moving-reboot) - Smart Moving Reboot

[![](http://cf.way2muchnoise.eu/full_449535_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/smartrender) - SmartRender

[![](http://cf.way2muchnoise.eu/full_449521_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/playerapi) - PlayerAPI

[![](http://cf.way2muchnoise.eu/full_449533_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/renderplayerapi) - RenderPlayerAPI

스마트무빙 모드
================

마인크래프트 1.12.2에서 작동하는 스마트무빙 17.0-RC4 버전

제작: 도치(doch2) | (원본 소스코드 작성자: Divisor and JonnyNova, elveskevtar)

Divisor가 만든 [기존 모드](https://www.curseforge.com/minecraft/mc-mods/smart-moving)가 1.8.9에서 업데이트가 멈춤에 따라, [JonnyNova가 1.10.2버전으로 모드](https://github.com/JonnyNova/SmartMoving)를 포팅시켰습니다. 그 1.10.2 스마트무빙에서 [eleveskevtar가 1.12.2로 2차 포팅](https://github.com/elveskevtar/SmartMoving)을 시켰으나, 포팅된 버전에서 서버가 구동되지 않는 등 몇가지 오류가 있음을 발견해 수정을 거친 버전입니다.

본 프로젝트는 오픈소스로, 아직 기존 개발자가 개발하던 스마트무빙에 비해 오류들과 구현되지 않은 기능들이 있습니다. 이를 고쳐주실 능력자분들을 적극 환영합니다!
GitHub가 익숙지 않으신 분들은 아래 링크를 참고하시면 쉽게 기여를 하실 수 있으시니 적극 기여 부탁드리겠습니다 (__)
 - 기여방법 설명 포스트: http://bit.ly/2P8t23X



설명
===========

스마트무빙 모드는 다양한 이동을 추가로 지원합니다:

* 바닐라 마인크래프트와 다른 날기, 다이빙, 수영 애니메이션
* 기어가기 (오직 애니메이션)
* 더 빠른 달리기
* 충전 점프(슈퍼 점프)
* 기어 올라가기
* 그 외 다양한 모션

정확한 모션은 설정에 따라 달라집니다 (설정 챕터 참조)



기존 모드에서 포팅되지 않음
===========

현재 스마트무빙으로 이식되지 않은 기능 및 버그는 다음과 같습니다:

* 슬라이딩(여우무빙 후 나타나는 모션)이 다른 사람에게서 한 칸 떠있는 것 처럼 애니메이션 오류
* 한 칸 블럭 통과



필수 모드
=============

이 모드를 사용하기 위해, 다음과 같은 것들이 필요합니다:

    * Minecraft Forge
    * Player API core 1.1나 그 이상
    * Render Player API core 1.0나 그 이상
    * SmartRender 3.0-RC2나 그 이상



설치 방법
============

0. 포지(Minecraft Forge)가 다운받아져 있지 않는 경우, 포지를 다운 받는다.
1. 제공된 모드들을 모두 다운받는다.
2. 다운 받은 모드들을 Appdata -> .minecraft -> mods 폴더에 집어넣는다

정상적으로 실행되지 않을 경우 확인하세요:
* 최신 버전의 Player API, Render Player API, SmartRender가 깔려져 있는지!
* 최신 버전의 마인크래프트 포지가 다운받아져 있는지!



개발 프로젝트 구성
========================

1. 개발 프로젝트를 다운받습니다.
2. 'gradlew setupDecompWorkspace' 명령어를 실행합니다.
3. IntelliJ IDEA 기준으로, 프로젝트를 한 번 실행시킨 후 다시 닫고 'gradlew genIntelliJRuns' 명령어를 실행합니다.



설정
=============

"smart_moving_options.txt" 파일을 사용하여 이 모드를 설정할 수 있습니다.
이 설정 파일은 당신의 ".minecraft" 폴더 안에 있습니다.
모드를 넣고 최초 실행 시(설정 파일이 존재하지 않을 시) 자동으로 생성됩니다.

이 설정파일을 사용하여 모드의 다양한 기능들을 설정할 수 있습니다.
