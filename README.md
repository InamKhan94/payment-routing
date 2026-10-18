# Payment Routing Service

## Overview

The Payment Routing Service calculates the cheapest route for transferring payments between bank branches in a directed network.

Each branch has a transfer cost. A charge is incurred when a payment leaves a branch, while receiving funds at a branch is free.

The application uses Java, Maven, and Spring Boot to expose a REST API that calculates the cheapest available route between two branches.

## Features

* Calculates the cheapest route using Dijkstra's shortest-path algorithm.
* Supports directed connections between branches.
* Calculates transfer costs based on the originating branch.
* Returns the route as a comma-separated string.
* Supports configurable branches, transfer costs, and connections.
* Validates request parameters.
* Handles unknown branches and unreachable destinations.
* Uses thread-safe route calculations for concurrent requests.
* Includes automated tests for routing and REST API behavior.

## Technology Stack

* Java 25
* Spring Boot 4
* Maven
* JUnit 5
* Mockito
* Spring MVC and MockMvc

## How It Works

The application models the branch network as a directed weighted graph.

Each branch represents a vertex, and each permitted connection represents a directed edge.

The weight of an outgoing edge is the transfer cost of its source branch:

`weight(u, v) = cost(u)`

For example, consider the following branches and transfer costs:

| Branch | Transfer cost |
| ------ | ------------: |
| A      |             5 |
| B      |            50 |
| C      |            10 |
| D      |            10 |
| E      |            20 |
| F      |             5 |

Available connections include:

* A → B
* A → C
* C → B
* B → D
* C → E
* D → E
* E → D
* D → F
* E → F

To calculate the cheapest route, the application uses Dijkstra's algorithm with a priority queue.

### Example

For a payment from branch A to branch D:

* Route A → B → D costs 5 + 50 = 55.
* Route A → C → E → D costs 5 + 10 + 20 = 35.

The cheapest route is:

`A,C,E,D`

The total transfer cost is 35.

## REST API

### Calculate the cheapest route

**Endpoint**

`GET /payments/route`

**Query parameters**

| Parameter     | Description        | Example |
| ------------- | ------------------ | ------- |
| `origin`      | Starting branch    | `A`     |
| `destination` | Destination branch | `D`     |

**Example request**

```http
GET /payments/route?origin=A&destination=D
```

**Successful response**

HTTP status: `200 OK`

```text
A,C,E,D
```

**Route not found**

If either branch is unknown or no permitted route exists, the service returns HTTP `404 Not Found`.

**Invalid request**

Missing or blank parameters return HTTP `400 Bad Request`.

## Service Interface

The application implements the following contract:

```java
public interface PaymentService {

    String processPayment(
            String originBranch,
            String destinationBranch);
}
```

The method returns the cheapest route as a comma-separated string or `null` when no route is available.

Blank or null inputs are rejected with an `IllegalArgumentException`.

If the origin and destination are the same valid branch, the route contains that branch alone.

## Configuration

Branch costs and directed connections are maintained in application configuration rather than hardcoded into the routing algorithm.

Example configuration structure:

```yaml
payment-routing:
  branches:
    A:
      cost: 5
      destinations: [B, C]
    B:
      cost: 50
      destinations: [D]
    C:
      cost: 10
      destinations: [B, E]
    D:
      cost: 10
      destinations: [E, F]
    E:
      cost: 20
      destinations: [D, F]
    F:
      cost: 5
      destinations: []
```

The property names must match the application's `PaymentRoutingProperties` configuration class.

New branches and connections can be introduced through configuration without changing the Dijkstra algorithm.

## Algorithm and Performance

Dijkstra's algorithm finds the minimum-cost path in a graph with non-negative edge weights.

The implementation uses:

* An adjacency-list representation of the graph.
* A priority queue to select the next lowest-cost branch.
* A distance map to track the best known cost.
* A predecessor map to reconstruct the route.

For a graph with \(V\) branches and \(E\) connections, the typical time complexity is:

`O((V + E) log V)`

The auxiliary space complexity is:

`O(V)`

for the per-request distance map, predecessor map, and priority queue, excluding the configured graph.

The adjacency list stores the graph in `O(V + E)` space.

## Thread Safety

Each route calculation maintains its own distance map, predecessor map, and priority queue.

The routing service does not store intermediate route calculations in shared mutable fields. This allows multiple requests to be processed concurrently without interfering with each other's calculations.

The configured graph should remain immutable during request processing. Configuration changes, if supported, should be published atomically rather than mutating the graph while requests are using it.

## Validation and Error Handling

The service validates:

* Null or blank origin and destination values.
* Missing or empty routing configuration.
* Invalid destination references.
* Negative transfer costs.
* Integer overflow during cost calculation.

Unknown branches and unreachable destinations return `null`, which the REST controller maps to HTTP `404 Not Found`.

Invalid request parameters return HTTP `400 Bad Request`.

Invalid graph configuration raises an exception so configuration problems can be diagnosed rather than silently producing incorrect routes.

## Testing

The project includes automated tests for the routing service and REST controller.

The tests cover:

* Finding the cheapest route.
* Returning no route when a destination is unreachable.
* Handling identical origin and destination branches.
* Rejecting blank and null inputs.
* Supporting lowercase branch names.
* Returning appropriate HTTP status codes.
* Validating missing and blank request parameters.

Run the complete test suite with:

```bash
mvn clean test
```

## Build and Run

### Prerequisites

* JDK 25
* Maven, or the Maven Wrapper included in the project

### Build

```bash
mvn clean package
```

### Run

```bash
mvn spring-boot:run
```

### Test the API

Once the application is running, send a request:

```bash
curl "http://localhost:8080/payments/route?origin=A&destination=D"
```

Expected response:

```text
A,C,E,D
```

## Design Decisions

* **Dijkstra's algorithm:** Suitable for finding cheapest paths when all transfer costs are non-negative.
* **Priority queue:** Avoids repeatedly scanning all branches to select the next lowest-cost branch.
* **Externalized configuration:** Makes the branch network extensible without changing the algorithm.
* **Local calculation state:** Supports concurrent calls without sharing mutable routing state.
* **Explicit service contract:** Keeps the implementation compatible with the required `String` return type.

## Limitations and Future Improvements

Potential enhancements include:

* Supporting larger cost ranges with `long`.
* Adding integration tests for invalid configuration.
* Adding dedicated concurrency tests.
* Benchmarking route calculations on increasingly large graphs.
* Supporting atomic refresh of routing configuration.
* Providing structured error responses and API documentation.

## License

This repository was created for a technical exercise. Add an appropriate license if redistribution or reuse is intended.
