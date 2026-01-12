FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests -Djavafx.platform=linux-aarch64 package \
    && mvn -DskipTests -Djavafx.platform=linux-aarch64 \
        -DincludeGroupIds=org.openjfx \
        -DoutputDirectory=/app/javafx \
        dependency:copy-dependencies

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libgtk-3-0 libxext6 libxrender1 libxtst6 libxi6 libfreetype6 libfontconfig1 libx11-6 \
        libxrandr2 libxcursor1 libxinerama1 libxss1 libglib2.0-0 \
        libgl1 libgl1-mesa-dri libgl1-mesa-glx mesa-utils \
        xvfb x11vnc novnc websockify \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/clientlourdv2-1.0.0-all.jar /app/app.jar
COPY --from=build /app/javafx /app/javafx
COPY docker/start.sh /app/start.sh
RUN chmod +x /app/start.sh
EXPOSE 6080 5900
CMD ["/app/start.sh"]
