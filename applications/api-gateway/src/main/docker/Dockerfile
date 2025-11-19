FROM eclipse-temurin:21-jre-alpine

ENV TZ='Europe/Amsterdam'
ENV LANGUAGE='nl_NL'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY target/quarkus-app/lib/ /opt/lib/
COPY target/quarkus-app/*.jar /opt/
COPY target/quarkus-app/app/ /opt/app/
COPY target/quarkus-app/quarkus/ /opt/quarkus/

EXPOSE 8080
USER 185

CMD [ "java", "-jar", "/opt/quarkus-run.jar" ]
