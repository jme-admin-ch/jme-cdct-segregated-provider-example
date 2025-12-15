# JME Consumer Driven Contract Testing Example Provider

## Overview
This project demonstrates consumer-driven contract testing with segregated APIs in a Java-based microservice
architecture. The provider service exposes two separate APIs, each configured with its own dedicated Pact provider for
independent contract verification.
It includes a sample provider service that fulfills contracts defined by consumer services. The contracts between the
consumers and the provider are verified using Pact JVM and a Pact Broker.

## Prerequisites

Before you begin, ensure you have the following installed:
- **Java 25** or higher
- **Docker** and **Docker Compose**

**Related Project:** This provider project works with the [JME CDCT segregated Consumer Example](https://github.com/jme-admin-ch/jme-cdct-segregated-consumer-example)
project for complete contract testing. Make sure to generate and publish the consumer pacts before verifying them with 
this provider project.

## Getting Started

### 1. Ensure the Local Pact Broker is Running

The Pact Broker is managed by the consumer project ([JME CDCT segregated Consumer Example](https://github.com/jme-admin-ch/jme-cdct-segregated-consumer-example)).
Both the consumer and provider projects use the same Pact Broker instance for contract testing.

Before running the provider verification, ensure the local Pact Broker is running and holds consumer pacts for the provider.
See the consumer project README.md for instructions on how to start the local Pact Broker and on how to publish a consumer pact.

### 2. Verify the Consumer Pact and Publish the Verification Result

Verify the provider against the consumer pacts from the Pact Broker and publish the verification results:

```bash
./mvnw verify -Plocal-pact-broker -Pcdct-enable-publishing-local
```

This command will:
- Run the provider verification tests
- Fetch pacts from the local Pact Broker
- Verify the provider implementation against each pact
- Publish verification results back to the Pact Broker

**Maven Profile Explanations:**
- `local-pact-broker`: Configures the Pact Broker URL to point to the local instance (http://localhost:9292)
- `cdct-enable-publishing-local`: Enables verification result publishing from local (non-CI) builds

Note: Usually, verification results are published in CI/CD pipelines only (to a remote Pact Broker instance).

### 3. View Verification Results

Open the Pact Broker UI in your browser ([http://localhost:9292](http://localhost:9292))
and navigate to the pact matrix of a consumer-provider pair to see the verification results.

## Provider Contract Verification

This project uses Pact JVM to verify the provider implementation against consumer contracts. Verification is performed
in the JUnit test class `TaskControllerProviderTest`, which retrieves all relevant pacts from the Pact Broker and tests
them against the running provider service. The provider verification test checks if the provider implementation meets
all expectations defined by its consumers.

## Troubleshooting

### Pact Broker Not Accessible

If you cannot access the Pact Broker consult the troubleshooting instructions in the README.md of the
([JME CDCT segregated Consumer Example](https://github.com/jme-admin-ch/jme-cdct-segregated-consumer-example)) project.

### Verification Fails

If pact verification fails:
1. Verify the Pact Broker is running and accessible
2. Check that both profiles are enabled: `-Plocal-pact-broker -Pcdct-enable-publishing-local`
3. Check that consumer pacts have been published to the broker


## Running the Provider Service Locally

Unlike other jEAP microservice example projects, the primary focus of this project is to execute Pact-related tasks
during the build process, rather than running the microservice itself. However, you can still run the provider service
locally to test its integration with consumer services.

To run the provider service locally, use the following command:

```bash
./mvnw spring-boot:run -pl jme-cdct-provider-service -Dspring-boot.run.profiles=local
```
Additionally, you need to start the mock authorization server located in the `jme-cdc-auth-scs` submodule:

```bash
./mvnw spring-boot:run -pl jme-cdct-segregated-auth-scs -Dspring-boot.run.profiles=local
```

Consumer services use this mock authorization server to obtain access tokens required for authenticating requests to
the provider service.

## Development Guidelines

This project needs to be versioned using [Semantic Versioning](http://semver.org/), and all changes need to be
documented in [CHANGELOG.md](./CHANGELOG.md) following the format defined
in [Keep a Changelog](http://keepachangelog.com/).

## Changes

Change log is available at [CHANGELOG.md](./CHANGELOG.md)

## Note

This repository is part of the open source distribution of jEAP. See [github.com/jme-admin-ch/jme](https://github.com/jme-admin-ch/jme)
for more information.

## License

This repository is Open Source Software licensed under the [Apache License 2.0](./LICENSE).
