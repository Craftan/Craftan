FROM gradle:8.7.0 as build

WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.gradle gradle build --parallel

FROM openjdk:21-slim as serverBuilder

WORKDIR /app

COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

ADD https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/13/downloads/paper-1.21.1-13.jar server/paper.jar
RUN echo 'eula=true' > server/eula.txt
COPY server-template /app/tmp/server-template

COPY --from=build /app/build/libs/Craftan-*-all.jar /app/tmp/app.jar

ENTRYPOINT ["/app/entrypoint.sh"]

EXPOSE 22565
EXPOSE 5005