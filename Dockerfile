FROM openjdk:8u151-jdk-alpine3.7

# Install Bash
RUN apk add --no-cache bash libc6-compat

# Install AppD Agent Fetch Dependencies
RUN apk --no-cache add curl unzip jq
ADD downloadJavaAgentLatest.sh .
#ADD custom-interceptors.xml .
#ADD custom-activity-correlation.xml .
#ADD log4j.xml .
#ADD log4j2.xml .
RUN ./downloadJavaAgentLatest.sh
# -javaagent:/opt/appdynamics/java/javaagent.jar

# Copy resources
ADD target/gremlindriverclient-1.0-SNAPSHOT.jar gremlindriverclient-1.0-SNAPSHOT.jar
ADD src/remote.yaml src/remote.yaml

CMD java -jar -javaagent:/opt/appdynamics/java/javaagent.jar gremlindriverclient-1.0-SNAPSHOT.jar