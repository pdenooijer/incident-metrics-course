FROM registry.access.redhat.com/ubi9/openjdk-21:1.21

ENV TZ='Europe/Amsterdam'
ENV LANGUAGE='nl_NL'

# We make distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 elastic-apm-agent.jar /deployments/lib/
COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -Delastic.apm.rabbitmq_naming_mode=QUEUE \
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
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
ENV JAVA_TOOL_OPTIONS="-javaagent:/deployments/lib/elastic-apm-agent.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
