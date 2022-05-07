FROM gradle:jdk11 AS builder
WORKDIR /src
COPY . ./
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM openjdk:slim-buster
RUN apt update
RUN apt install -y wget
RUN apt install -y net-tools
RUN wget https://download-gcdn.ej-technologies.com/jprofiler/jprofiler_linux_13_0_1.tar.gz -P /tmp/ && \
  tar -xzf /tmp/jprofiler_linux_13_0_1.tar.gz -C /usr/local &&\
  rm /tmp/jprofiler_linux_13_0_1.tar.gz
RUN adduser spring
USER spring:spring
ARG DEPENDENCY=/src/build/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# ENTRYPOINT ["java","-cp","app:app/lib/*","com.chubock.chatservice.ChatServiceApplication", "-agentpath:/usr/local/jprofiler13.0.1/bin/linux-x64/libjprofilerti.so=port=8849"]
ENTRYPOINT ["java","-cp","app:app/lib/*","com.chubock.chatservice.ChatServiceApplication"]