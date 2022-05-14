# amd-gpu
This project is intended to fix the black artifacts issue for HP OMEN 16.1 (AMD RX 6600m)
HP Support does nothing to fix that issue.

To build run 

``gradlew jpackage``

the resulting exe can be found under "amd-gpu\gpu-task-ui\build\jpackage\amd-gpu"

Use JDK 17:

Download page: https://adoptium.net/temurin/releases/
Installation guide:
- Download JDK 17
- Unpack ZIP FILE
- Create environment variable JAVA_HOME pointing to the home jdk folder
