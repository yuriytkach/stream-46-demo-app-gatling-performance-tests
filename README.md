# # Demo Project for Online Stream #46 - Gatling Performance Tests

Demo project for online YouTube stream #46 about using Gatling tool for performance testing of backend microservice.

This project contains a Gatling performance test for the backend service located 
in the `backend-service-under-test` folder. The test simulates various user loads and interactions 
with the service's API endpoints.

## Access to Online Stream on YouTube

You can view the online stream on YouTube after making a donation to support my
volunteering initiative to help Ukrainian Armed Forces.

:coffee: Please, visit the [Buy Me a Coffee](https://buymeacoffee.com/ytkach/e/256122).

Thank you in advance for your support! Слава Україні! :ukraine:

View the announcement of the online stream on YouTube:

[![Online Stream Announce](https://img.youtube.com/vi/dbGkczhdPRI/0.jpg)](https://www.youtube.com/watch?v=dbGkczhdPRI)

## Simulation Details

The only simulation in this project, `GetProductsSimulation`, performs the following actions:

1. **GET /products**: Retrieves a list of products filtered by price and size.
2. **GET /products/{id}**: Retrieves details of a random product from the list obtained in the previous step.
3. **PUT /products/{id}**: Updates the stock of the retrieved product.

### Injection Steps

The simulation includes different user load patterns:
- **nothingFor(Duration.ofSeconds(2))**: No users for the first 2 seconds.
- **atOnceUsers(100)**: 100 users at once.
- **rampUsers(100).during(5)**: 100 users ramping up over 5 seconds.
- **constantUsersPerSec(300).during(80)**: 300 users per second for 80 seconds.
- **constantUsersPerSec(300).during(10).randomized()**: 300 users per second for 10 seconds with randomized intervals.
- **stressPeakUsers(1000).during(15)**: 1000 users over 15 seconds.

## Prerequisites

Ensure that the backend service is running before executing the Gatling test. 
The backend project can be found in the `backend-service-under-test` folder. 
Follow the instructions in the [backend-service-under-test README](./backend-service-under-test/README.md) 
to start the service.

## Running the Simulation

To run the simulation using the Gradle wrapper, use the following command:

```bash
./gradlew gatlingRun
```

This command will execute the `GetProductsSimulation` and generate a report of the performance test results.

## Configuration

The `gatling.conf` file contains the following settings:

```properties
gatling.charting.indicators.lowerBound=200
gatling.charting.indicators.higherBound=500
```

These settings define the lower and higher bounds for charting indicators in the generated reports.

## Building the Project

To build the project, use the following command:

```bash
./gradlew build
```

This will compile the Java sources and prepare the project for running the simulation.

## License

This project is licensed under the MIT License.
