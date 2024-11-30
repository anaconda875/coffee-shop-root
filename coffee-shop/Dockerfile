FROM ibm-semeru-runtimes:open-17.0.13_11-jre-focal

ADD target/coffee-shop-0.0.1-SNAPSHOT.jar /opt
WORKDIR /opt
EXPOSE 8080 50000
ENV JDK_JAVA_OPTIONS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50000'

ENTRYPOINT ["java", "-jar", "coffee-shop-0.0.1-SNAPSHOT.jar"]