FROM openjdk:17-jdk-slim
WORKDIR ExchangeRates
COPY target/ExchangeRates-0.0.1-SNAPSHOT.jar ExchangeRates.jar
ENTRYPOINT ["java", "-jar", "ExchangeRates.jar"]