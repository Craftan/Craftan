FROM gradle:8.10-alpine AS build

ARG PAPERMC_VERSION="1.21.1"

RUN apk add --no-cache \
  curl \
  jq

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.gradle gradle build --parallel

RUN PAPER_MC_DOWNLOAD_LINK=$(curl -s https://qing762.is-a.dev/api/papermc | jq -r ".versions.\"${PAPERMC_VERSION}\"") \
  ; curl --output /app/paper.jar ${PAPER_MC_DOWNLOAD_LINK}

FROM openjdk:21-slim

WORKDIR /app

COPY lib /tmp/lib
COPY --from=build /app/build/libs/Craftan-*-all.jar /tmp/app.jar
COPY --from=build /app/paper.jar /app/paper.jar

RUN mkdir -p /app/plugins \
  ; cp /tmp/app.jar /app/plugins/craftan.jar \
  ; cp -r /tmp/lib/plugins/* /app/plugins

RUN echo "eula=true" > /app/eula.txt

EXPOSE 22565
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/paper.jar"]
