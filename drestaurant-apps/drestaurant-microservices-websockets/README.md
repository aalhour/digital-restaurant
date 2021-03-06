### Microservices / Websockets / AxonDB and AxonHub

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

We designed and structured our [loosely coupled components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enable us to choose different deployment strategy and take first step towards Microservices architectural style.

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

 - has its own bounded context,
 - has shared event(sourcing) storage (AxonDB)
 - and we distribute messages between them via AxonHub
 
#### AxonHub

[AxonHub](https://axoniq.io/product-overview/axonhub) is a messaging platform specifically built to support distributed Axon Framework applications. It is a drop in replacement for the other CommandBus, EventBus and QueryBus implementations.

The key characteristics for AxonHub are:
 - Dedicated infrastructure for exchanging messages in a message-driven micro-services environment.
 - Easy-to-use and easy-to-manage
 - Built-in knowledge on CQRS message patterns


#### AxonDB

AxonDB is a purpose-built database system optimized for the storage of event data of the type that is generated by applications that use the event sourcing architecture pattern.
It has been primarily designed with the use case of Axon Framework-based Java applications in mind, although there is nothing in the architecture that restricts its use to these applications only.

AxonHub and AxonDB are commercial software products by AxonIQ B.V.
Free 'developer' editions are available.

### STOMP over WebSockets API


#### Customer (command side)

WebSocket SockJS endpoint: `ws://localhost:8081/customer/websocket`


 - `/app/customers/createcommand`, messageType=[MESSAGE]
 
#### Courier (command side) 

WebSocket SockJS endpoint: `ws://localhost:8082/courier/websocket`


 - `/app/couriers/createcommand`, messageType=[MESSAGE]
 - `/app/couriers/orders/assigncommand`, messageType=[MESSAGE]
 - `/app/couriers/orders/markdeliveredcommand`, messageType=[MESSAGE]
 
#### Restaurant (command side)

WebSocket SockJS endpoint: `ws://localhost:8084/restaurant/websocket`


 - `/app/restaurants/createcommand`, messageType=[MESSAGE]
 - `/app/restaurants/orders/markpreparedcommand`, messageType=[MESSAGE]
 
#### Order (command side)

WebSocket SockJS endpoint: `ws://localhost:8083/order/websocket`

 - `/app/orders/createcommand`, messageType=[MESSAGE]

#### Query side

WebSocket SockJS endpoint: `ws://localhost:8085/query/websocket`


 - `/app/customers`, messageType=[SUBSCRIBE]
 - `/app/customers/{id}`, messageType=[SUBSCRIBE]
 - `/app/couriers`, messageType=[SUBSCRIBE]
 - `/app/couriers/{id}`, messageType=[SUBSCRIBE]
 - `/app/couriers/orders`, messageType=[SUBSCRIBE]
 - `/app/couriers/orders/{id}`, messageType=[SUBSCRIBE]
 - `/app/restaurants`, messageType=[SUBSCRIBE]
 - `/app/restaurants/{id}`, messageType=[SUBSCRIBE]
 - `/app/orders`, messageType=[SUBSCRIBE]
 - `/app/orders/{id}`, messageType=[SUBSCRIBE]
 - `/app/restaurants/orders`, messageType=[SUBSCRIBE]
 - `/app/restaurants/orders/{id}`, messageType=[SUBSCRIBE]
 - `/topic/couriers.updates` (courier list has been updated, e.g. new courier has been created)
 - `/topic/customers.updates` (customer list has been updated, e.g. new customer has been created)
 - `/topic/orders.updates` (order list has been updated, e.g. new order has been created)
 - `/topic/restaurants.updates` (restaurant list has been updated, e.g. new restaurant has been created)
 - `/topic/couriers/orders.updates` (courier order list has been updated, e.g. new courier order has been created)
 - `/topic/restaurants/orders.updates`(restaurant order list has been updated, e.g. new restaurant order has been created)



## Development

This project is driven using [Maven][mvn].

### Clone

```bash
$ git clone https://github.com/idugalic/digital-restaurant
```

### Build

```bash
$ cd digital-restaurant
$ mvn clean install
```


### Run microservices

[AxonHub](https://axoniq.io/product-overview/axonhub) and [AxonDB](https://axoniq.io/product-overview/axondb) are required.
Developer editions are available for free, and you should have them up and running before you start the services.

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-order
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-query
$ mvn spring-boot:run
```


## Continuous delivery

We have one deployment pipeline for all applications and libraries within this repository. In addition, all projects in the repository share the same dependencies. Hence, there are no version conflicts because everyone has to use the same/the latest (SNAPSHOTS) version. And you don't need to deal with a private NPM (JavaScript) or Maven (Java) registry when you just want to use your own libraries.
This setup and project structure is usually addressed as a [monorepo](https://medium.com/@maoberlehner/monorepos-in-the-wild-33c6eb246cb9).

## Technology

### Language
- [Kotlin][kotlin]

### Frameworks and Platforms
- [Spring (spring boot, spring cloud, spring data, spring data rest)][spring]
- [Axonframework (eventsourcing, CQRS)][axonframework]

### Continuous Integration and Delivery 
- Travis

### Infrastructure
- [AxonDB](https://axoniq.io/product-overview/axondb)
- [AxonHub](https://axoniq.io/product-overview/axonhub)

 

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[rabbitMQ]: https://www.rabbitmq.com/
[kafka]: https://kafka.apache.org/
[pivotalCF]: https://run.pivotal.io/
