FROM docker.io/eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.source=https://github.com/extact-io/msa-rms-service-reservation

WORKDIR /msa-service-reservation

# Install packages
RUN apk update && apk add curl

# Copy the binary built in the 1st stage
COPY ./target/msa-rms-service-reservation.jar ./
COPY ./target/libs ./libs

CMD ["java", "-jar", "msa-rms-service-reservation.jar"]

EXPOSE 7003
