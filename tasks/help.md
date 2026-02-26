# API_REACTIVE_TASKS — Calling the API and Sending a Message to the Queue

## Purpose

This document explains how the project's reactive task API and the RabbitMQ queue interact, and shows simple ways to:
- **(a)** Call the API that creates a task
- **(b)** Send a message to the RabbitMQ queue at the same time (client-side or server-side)

It also covers verification and troubleshooting.

## Quick Summary

- **Server-side behavior:** The codebase already enqueues a message when a task is created. See `com.fran.task.tasks.adapter.TaskAdapter#createTask` which persists the Task and then calls `queueTask(...)` to send an `OutboundMessage` to the `spring-reactive-queue` queue.
- **Client-side options:** You can:
  1. Call the REST API and rely on the server to enqueue the message for you, or
  2. Call the REST API and also publish a message directly to RabbitMQ from the client in parallel.

## Assumptions

| Item | Value | Notes |
|------|-------|-------|
| Service host | `http://localhost:8080` | Change as needed |
| REST endpoint to create a task | `POST /tasks` (body: JSON task) | Adjust if your project maps this differently |
| RabbitMQ queue name | `spring-reactive-queue` | Constant used in `TaskAdapter` |
| RabbitMQ management UI | `http://localhost:15672` | Default user/password: `guest`/`guest` |

## 1) How It Works (Server-Side)

When you call the API that creates a task (for example `POST /tasks`), `TaskAdapter.createTask`:

1. Persists the Task through `persistenceAdapter.createTask(...)`;
2. Calls `queueTask(persistedTask)` which converts the Task to JSON and sends a Reactor RabbitMQ `OutboundMessage` to the `spring-reactive-queue` queue using the injected `Sender`.

Because of this, simply calling the API endpoint that creates a task is usually enough — the server will push the message to RabbitMQ automatically.

## 2) Client-Side: Call the API and Also Send a Message to the Queue in Parallel

### Use Cases

- You want the server to store the Task but also need to publish an explicit, separate message from the client (for example for cross-service notification).
- You want to ensure the client-side workflow sends an extra message immediately in addition to the server-side message.

### Options

#### A) curl + rabbitmqadmin (Two Independent Commands)

**Step 1:** Create the task (server will also enqueue):

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"id":"1","name":"my-task","payload":"hello"}'
```

**Step 2:** Publish a message directly to RabbitMQ using `rabbitmqadmin` (or the HTTP API):

```bash
rabbitmqadmin -u guest -p guest publish routing_key=spring-reactive-queue payload='{ "event":"external", "taskId":"1" }'
```

> **Note:** `rabbitmqadmin` is a CLI helper available from the RabbitMQ Management plugin; alternatively use the HTTP POST to the management API.

#### B) Single Client Program — Java Reactor Example

This example uses Reactor's WebClient and Reactor RabbitMQ's `Sender`. It runs both the HTTP POST and the RabbitMQ publish in parallel and waits for both to complete.

**Important:** Adapt imports and wiring to your build. This snippet is a minimal example to illustrate the pattern.

```java
// imports omitted for brevity
WebClient webClient = WebClient.create("http://localhost:8080");
Sender sender = /* obtain reactor.rabbitmq.Sender from your client or context */;

String queue = "spring-reactive-queue";
String taskJson = "{ \"id\": \"1\", \"name\": \"my-task\" }";

Mono<ClientResponse> apiCall = webClient
    .post()
    .uri("/tasks")
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(taskJson)
    .exchangeToMono(resp -> Mono.just(resp));

byte[] payload = taskJson.getBytes(StandardCharsets.UTF_8);
OutboundMessage outbound = new OutboundMessage("", queue, payload);

Mono<Void> rmqPublish = sender
    .declareQueue(QueueSpecification.queue(queue).durable(true))
    .thenMany(sender.sendWithPublishConfirms(Flux.just(outbound)))
    .then();

// Run both in parallel and wait for completion
Mono.zip(apiCall, rmqPublish).block();

// Or if you prefer not to block in a reactive app, use Mono.when(apiCall.then(), rmqPublish).subscribe(...)
```

**Notes on the snippet:**
- The server's POST /tasks call will still enqueue its own message via `TaskAdapter` (so you'll get two messages unless you change that behavior).
- If you want the client to only publish (and not trigger server-side enqueue), you'd need a server endpoint that does not call `queueTask` or a separate lightweight endpoint that just persists the Task without queueing.

## 3) Verifying Delivery

- **Check persistence:** Inspect the application's storage or call `GET /tasks` or `GET /tasks/{id}` (if available) to confirm the Task was persisted by the server.
- **Check RabbitMQ via Management UI:** `http://localhost:15672` (login: `guest`/`guest` by default). Look at the `spring-reactive-queue` queue and its message rates.
- **CLI check:**

```bash
# list queues
rabbitmqctl list_queues

# or use HTTP API (example) to get queue details
curl -u guest:guest http://localhost:15672/api/queues/%2F/spring-reactive-queue
```

- **Read messages from the queue** using the project's reactive receiver:
  - The code exposes a `startReceivingMessages()` method on `TaskAdapter` that consumes auto-ack messages from `spring-reactive-queue`. To use it from within the app, subscribe to `taskAdapter.startReceivingMessages()` and log or inspect messages.

## 4) Troubleshooting

- **No messages visible in the queue:**
  - Check RabbitMQ is running and the app has connectivity to it (host/port/credentials).
  - Check logs for Sender/Receiver errors; `TaskAdapter` logs send failures in `queueTask`.
- **Duplicate messages:**
  - Remember that posting to the server that enqueues (createTask) plus publishing from the client will produce two messages.
- **Serialization problems:**
  - `TaskAdapter` uses Jackson to write the Task as JSON and `SerializationUtils.serialize(json)` to create bytes. If you send a raw JSON payload from a client, ensure the server-side deserialization matches the format expected by the consumer.
- **Auth / firewall / network:**
  - If RabbitMQ is remote or uses non-default credentials, update your `application.properties` or client code accordingly.

## 5) Configuration Hints (application.properties)

If you need to configure RabbitMQ connection for the app, set properties like:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

## 6) Assumptions and How to Adapt

- If the real REST endpoint differs from `POST /tasks`: replace the curl / WebClient URI accordingly.
- If the queue name or virtual host differs, change `spring-reactive-queue` and the vhost used in RabbitMQ commands.
- If you want to avoid server-side enqueueing, locate `TaskAdapter#createTask` and modify it to not call `queueTask` (or add a flag/alternate endpoint).

## Further Improvements (Suggested)

- Add a small runnable example in this repository that demonstrates a client program doing the parallel POST + publish flow.
- Add an endpoint that allows creating a Task without queueing for clients that want to control queueing themselves.

### If you'd like, I can:

- Add a runnable example Java client to the repo demonstrating the parallel pattern, or
- Locate the controller that exposes `POST /tasks` and update this document with the exact endpoint path used by your project.

## Requirements Coverage

| Requirement | Status | Notes |
|------------|--------|-------|
| Explain how API and queue interact | ✓ Done | References to `TaskAdapter.createTask` and `queueTask` |
| Show how to call API and send message at the same time | ✓ Done | curl + rabbitmqadmin + Java Reactor example |
| Verify and troubleshoot | ✓ Done | Multiple verification methods provided |
| Note assumptions and how to adapt | ✓ Done | Comprehensive adaptation guide |

