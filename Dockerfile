FROM folioci/alpine-jre-openjdk17:latest

ENV VERTICLE_FILE mod-ldp.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

# Copy your fat jar to the container
COPY target/mod-ldp-*.jar ${VERTICLE_HOME}/${VERTICLE_FILE}

# Expose this port locally in the container.
EXPOSE 8001
