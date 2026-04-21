# 🏫 SmartCampusAPI

A fully functional **RESTful API** for managing Rooms and Sensors across a Smart Campus, built using **JAX-RS (Jersey)** with an embedded **Grizzly HTTP server**. 

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


## 🚀 How to Build and Run

### Prerequisites
- Apache NetBeans IDE
- Java JDK 11+
- Maven (included with NetBeans)

### Steps

1. **Clone the repository**
   
2. https://github.com/Ibrahimm51/SmartCampusAPI.git
   
3. **Open the project in NetBeans**
   - File → Open Project → Select the `SmartCampusAPI` folder

4. **Build the project**
   - Right-click the project → **Clean and Build**
   - Maven will automatically download all dependencies

5. **Run the server**
   - Expand `com.smartcampus.smartcampusapi`
   - Right-click `SmartCampusApi.java` → **Run File**

6. **Access the API**
   - http://localhost:8080/api/v1/
  
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

💻 Sample curl Commands

```bash
# 1. Discovery — see what the API offers
curl http://localhost:8080/api/v1/

# 2. Create a room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'

# 3. Get all rooms
curl http://localhost:8080/api/v1/rooms

# 4. Create a sensor linked to a room
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","roomId":"LIB-301"}'

# 5. Filter sensors by type
curl "http://localhost:8080/api/v1/sensors?type=Temperature"

# 6. Add a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.5}'

# 7. Get all readings for a sensor
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings

# 8. Try to delete a room that has sensors (expect 409 Conflict)
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


# ⚠️ Error Handling

| Status Code | Meaning | When It Happens |
|-------------|---------|-----------------|
| 409 Conflict | Room has active sensors | Trying to delete a room that still contains sensors |
| 422 Unprocessable Entity | Invalid room reference | Creating a sensor with a roomId that doesn't exist |
| 403 Forbidden | Sensor unavailable | Posting a reading to a sensor in MAINTENANCE status |
| 500 Internal Server Error | Unexpected error | Any unhandled runtime exception |



------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


## 🛠️ Technology Stack

- **Java 11**
- **JAX-RS** (Jakarta RESTful Web Services)
- **Jersey 2.41** (JAX-RS implementation)
- **Grizzly HTTP Server** (embedded — no Tomcat needed)
- **Jackson** (JSON serialisation)
- **Maven** (dependency management)

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


## 📖 API Design Overview

This API follows RESTful architectural principles and is organised around two main resources: **Rooms** and **Sensors**.

- **Rooms** represent physical spaces on campus (e.g. libraries, labs). Each room has an ID, name, and capacity.
- **Sensors** are devices deployed inside rooms (e.g. temperature, CO2, occupancy). Each sensor belongs to a room via a `roomId`.
- **Sensor Readings** are historical data points recorded by a sensor over time.

The API uses a logical resource hierarchy:
- `/api/v1/rooms` — manage campus rooms
- `/api/v1/sensors` — manage sensors across all rooms
- `/api/v1/sensors/{id}/readings` — manage historical readings per sensor

All data is stored in-memory using `ConcurrentHashMap` for thread safety. All responses are in **JSON format**. The API includes full error handling with meaningful HTTP status codes and never exposes raw Java errors to the client.


----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Report: Answers to Questions

### Part 1.1 — JAX-RS Resource Lifecycle
By default, JAX-RS creates a **new instance of a resource class for every incoming HTTP request**. This is called "per-request" scope. This means you cannot store state (like a list of rooms) inside a resource class itself, because it would be lost after each request. To solve this, I used a separate `DataStore` class with `static ConcurrentHashMap` fields. Since these are static, they live for the entire lifetime of the application and are shared across all requests. I used `ConcurrentHashMap` specifically because it is thread-safe, meaning multiple requests hitting the API at the same time cannot corrupt the data.

### Part 1.2 — HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means that API responses include links to related resources, so clients can navigate the API without needing to memorise URLs. For example, our discovery endpoint returns links to `/api/v1/rooms` and `/api/v1/sensors`. This benefits client developers because they do not need to rely solely on static documentation — the API itself tells them where to go next, making it more self-documenting and easier to use.

### Part 2.1 — Returning IDs vs Full Objects
Returning only IDs uses less network bandwidth and is faster, but forces the client to make additional requests to fetch details for each room, which increases the number of round trips. Returning full room objects gives the client everything it needs in one response, reducing complexity on the client side. For small collections, returning full objects is better. For very large collections, returning IDs or paginated summaries is more efficient.

### Part 2.2 — DELETE Idempotency
The DELETE operation is idempotent in my implementation. The first DELETE on a room that exists removes it and returns 204 No Content. If the same DELETE request is sent again, the room no longer exists, so the server returns 404 Not Found. Although the status code differs, the server state is the same after both calls — the room is gone. This is the standard RESTful definition of idempotency: the same operation applied multiple times produces the same server state.

### Part 3.1 — @Consumes Annotation
If a client sends data in a format other than `application/json` (for example `text/plain` or `application/xml`) to an endpoint annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS will automatically reject the request and return a **415 Unsupported Media Type** response. The resource method is never even called — Jersey handles the rejection at the framework level before the request reaches our code.

### Part 3.2 — @QueryParam vs Path Parameter for Filtering
Using `@QueryParam` (e.g. `/api/v1/sensors?type=CO2`) is considered better for filtering because query parameters are optional by nature — if omitted, the full list is returned. Path parameters (e.g. `/api/v1/sensors/type/CO2`) imply a specific resource identity, which is semantically incorrect for filtering. Query parameters are also easier to combine (e.g. `?type=CO2&status=ACTIVE`), more cacheable, and better understood by API clients and browsers as search/filter operations.

### Part 4.1 — Sub-Resource Locator Pattern
The Sub-Resource Locator pattern delegates handling of a nested path to a separate dedicated class. Instead of putting all logic in one giant resource class, each class has a single clear responsibility. In my implementation, `SensorResource` handles `/sensors` and delegates `/sensors/{id}/readings` to `SensorReadingResource`. This makes the code easier to maintain, test, and extend. In large APIs with many nested resources, this prevents any single class from becoming too complex.

### Part 5.2 — HTTP 422 vs 404
A 404 Not Found means the requested URL/resource does not exist. A 422 Unprocessable Entity means the request URL was valid and the JSON was syntactically correct, but the server cannot process it because of a semantic issue — in this case, the `roomId` referenced inside the JSON payload points to a room that does not exist. Using 422 is more semantically accurate because it tells the client exactly what went wrong: the data was understood but logically invalid.

### Part 5.4 — Security Risk of Exposing Stack Traces
Exposing Java stack traces to external API consumers is a serious security risk. A stack trace reveals the internal structure of the application — package names, class names, method names, line numbers, and the technology stack being used. An attacker can use this information to identify known vulnerabilities in specific library versions, understand the application's logic, craft targeted attacks, and identify which third-party frameworks are in use. My `GlobalExceptionMapper` catches all unexpected errors and returns only a generic 500 message to the client, while logging the full trace internally for developers.

### Part 5.5 — JAX-RS Filters for Cross-Cutting Concerns
Using JAX-RS filters for logging is better than adding `Logger.info()` calls inside every resource method because filters implement the **DRY principle** (Don't Repeat Yourself). If logging logic needs to change, you only change it in one place — the filter class. It also keeps resource methods clean and focused on business logic only. Filters run automatically for every request and response without any extra code in individual methods, making the approach more scalable and maintainable.


