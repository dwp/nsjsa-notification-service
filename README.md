# Notification Service

## About 

Job Seeker's Allowance microservice to manage sending notifications to the Claimant

### Prerequisites

* Java 8
* Maven


## PublicKey

In application.properties, the services.publicKey needs to be populated with a good RSA key.
To create this, and set it, run ./createPublicKey.sh.  This is a one time operation.  Please take
care not to check this change in.

## Starting the jar

To run the jar, use:

```
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n \
  -jar ./target/notification-service-*.jar --spring.profiles.active=nosecure,WC \
  --logging.level.root=DEBUG
```

# Dependencies

This service requires nsjsa-commons to build.
