FROM ibm-semeru-runtimes:open-17.0.13_11-jre-focal

ADD target/coffee-shop-customer-0.0.1-SNAPSHOT.jar /opt
WORKDIR /opt

ENTRYPOINT ["java", "-jar", "coffee-shop-customer-0.0.1-SNAPSHOT.jar"]