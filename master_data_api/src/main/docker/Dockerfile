FROM frolvlad/alpine-oraclejdk8:slim
# TODO: For production we don't need JDK support - investigate develar/java

VOLUME /tmp

# Create app directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Install app dependencies
COPY master_data_api-1.0.0.jar /usr/src/app/app.jar
RUN sh -c 'touch /usr/src/app/app.jar'

# Create log dir
RUN mkdir -p /usr/src/app/log
VOLUME /usr/src/app/log

# Expose server ports
EXPOSE 9020 9021

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /usr/src/app/app.jar" ]