FROM openjdk:21

VOLUME /logs

COPY out/production/Store_Management_System_Playground /tmp
COPY mysql-connector-j-9.1.0.jar /tmp

WORKDIR /tmp

CMD java -cp /tmp/mysql-connector-j-9.1.0.jar:. Main
