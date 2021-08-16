FROM openjdk:11-jdk

ADD dist /dist
WORKDIR dist

ENTRYPOINT ["java","-jar","booking-coding-challenge-1.0.0-SNAPSHOT.jar", "server", "booking.yml"]
