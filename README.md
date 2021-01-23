[If you want to find the Korean version mode description, please click here!(한국어 버전 모드 설명을 찾길 원하신다면, 여기를 눌러주세요!)](https://github.com/doch2/SmartMovingReboot/blob/master/README_Korean.md)
Recommend Korean version.


Smart Moving Mod
================

Version 17.0-RC3 for Minecraft Client 1.12.2

by doch2 | (Original Creator: Divisor and JonnyNova, elveskevtar) 

This mode was ported to version 1.10.2 by [Divisor and Jonny Nova](https://github.com/JonnyNova/SmartMoving), as the update stopped at version 1.8.9. [(Old SmartMoving)](https://www.curseforge.com/minecraft/mc-mods/smart-moving) In the new smart moving mode source code, the [elevskevtar made the second port](https://github.com/elveskevtar/SmartMoving) at 1.12.2. 
But elevskevtar's version include some errors, so i  modified errors. This repository is the one I modified.


Description
===========

The Smart moving mod provides various additionaly moving possibilities:

* Climbing only via gaps in the walls
* Climbing ladders with different speeds depending on ladder coverage and/or neighbour blocks
* Alternative animations for flying and falling
* Climbing along ceilings and up vines
* Jumping up & back while climbing
* Configurable sneaking
* Alternative swimming
* Alternative diving
* Alternative flying
* Faster sprinting
* Side & Back jumps
* Charged jumps
* Wall jumping
* Head jumps
* Crawling
* Sliding

Exact behavior depends on configuration (see chapter 'Configuration' below)



Not ported in existing mode
===========

Features and bugs that are not currently implanted with Smart Moving include:

* Animated error in which sliding (motion after fox-moving) appears to be floating in one space from another.
* 1x1 block through




Required Mods
=============

To use this mode, you need the following things:

    * Minecraft Forge and
    * Player API core 1.1 or higher
    * Render Player API core 1.0 or higher
    * SmartRender 3.0-RC2 or higher



Installation
============

Move the Smart Moving files "${mod.render.file}" and "${%mod.moving.file}" into the subfolder "mods" of your Minecraft Forge installation folder. In case this folder does not exist, create it or run your Minecraft Forge client at least once.

Don't forget to:
* ensure you have the latest version of Player API core installed!
* ensure you have the latest version of Minecraft Forge installed!



Development Installation
========================

1. Download the development project.
2. Run the 'gradlew setupDecompWorkspace' command.
3. Based on IntelliJ IDEA, run the project once, close it again, and run the 'gradlew genIntelliJRuns' command.



Configuration
=============

The file "smart_moving_options.txt" can be used to configure the behavior this mod.
It is located in your ".minecraft" folder next to minecrafts "options.txt".
If does not exist at Minecraft startup time it is automatically generated.

You can use its content to manipulate this mod's various features.
