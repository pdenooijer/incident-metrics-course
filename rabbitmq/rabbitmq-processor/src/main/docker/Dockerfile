FROM eclipse-temurin:21-jre-alpine

ENV TZ='Europe/Amsterdam'
ENV LANGUAGE='nl_NL'

# We make distinct layers so if there are application changes the library layers can be re-used
COPY elastic-apm-agent.jar /opt/lib/
COPY target/quarkus-app/lib/ /opt/lib/
COPY target/quarkus-app/*.jar /opt/
COPY target/quarkus-app/app/ /opt/app/
COPY target/quarkus-app/quarkus/ /opt/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Delastic.apm.rabbitmq_naming_mode=QUEUE \
  -Delastic.apm.verify_server_cert=false\
  -Delastic.apm.log_level=TRACE\
  -Delastic.apm.service_name=processor\
  -Delastic.apm.server_urls=http://apm-server-apm-server.monitoring:8200\
  -Delastic.apm.secret_token=\
  -Delastic.apm.environment=workshop\
  -Delastic.apm.application_packages=nl.ssg.incidentmetricscourse\
  -Ddisabled.elastic.apm.capture_body=all\
  -Ddisabled.elastic.apm.enable_log_correlation=true\
  -Ddisabled.elastic.apm.log_ecs_reformatting=OVERRIDE\
  -Delastic.apm.log_sending=true"
ENV JAVA_TOOL_OPTIONS="-javaagent:/opt/lib/elastic-apm-agent.jar"

CMD [ "/bin/sh", "-c", "java ${JAVA_OPTS_APPEND} -jar /opt/quarkus-run.jar" ]
