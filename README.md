# Kalaha Game based on Microservices architecture

This application implements Kalaha Game

The solution uses Microservices architecture for building the game!

The implementation consists of two microservices implemented in Java using Spring Boot and Spring Cloud:

-   `kalaha-game-api`: A Kalaha Game API with two endpoints:

    - `POST /api/v1/users Endpoint`: To create a new user
    - `POST /api/v1/users/login Endpoint`: To authenticate the user and generate jwt token
    - `POST /api/v1/kalaha-games Endpoint`: To create a new Game
    - `PUT /api/v1/kalaha-games/{gameId}/pits/{pitId} Endpoint`: To sow specific pit index of the Game
     
    You can find several Tests created to make sure the implementation covers all possible cases within Kalah Game.

-   `kalaha-game-impl`:the Implementation for Game API.

Technologies
------------
- `Java 11`
- `Spring Boot`
- `MongoDB`, NoSQL database for persisting the Game information
- `Open API`, Swagger-UI, for API documentation

Solution Architecture
--
The overall architecture of Mancala Game based on Facade architecture pattern


Swagger API Documentation
------------------------
This API is documented using Swagger. To view the documentation, navigate to /swagger-ui.html once the application is running.


How To Run
----------
`./mvnw spring-boot:run`

How To Test
----------
`Create Game endpoint`
`POST http://localhost:8090/api/v1/kalaha-games`

Response
-------
```json{
"id": "643a7e73aa8cd74e5dfb2a6c",
"board": [
    6,
    6,
    6,
    6,
    6,
    6,
    0,
    6,
    6,
    6,
    6,
    6,
    6,
    0
    ],
"playerTurn": "643a7df6aa8cd74e5dfb2a6a",
"status": "NEW",
"bonusTurn": false,
"winner": null,
"players": [
    {
    "id": "643a7df6aa8cd74e5dfb2a6a",
    "userName": "test1",
    "email": "test1@test1.com"
    },
    {
    "id": "643a7dffaa8cd74e5dfb2a6b",
    "userName": "test2",
    "email": "test2@test1.com"
    }
]
}
```
------
`Sow Endpoint Sample`

`PUT http://localhost:8090/api/v1/kalaha-games/643a7e73aa8cd74e5dfb2a6c/pits/5`

Response
-------
```json{
"id": "643a7e73aa8cd74e5dfb2a6c",
"board": [
        1,
        1,
        0,
        12,
        12,
        0,
        25,
        1,
        1,
        2,
        0,
        3,
        3,
        11
        ],
"playerTurn": "643a7dffaa8cd74e5dfb2a6b",
"status": "IN_PROGRESS",
"bonusTurn": false,
"winner": null,
"players": [
    {
    "id": "643a7df6aa8cd74e5dfb2a6a",
    "userName": "test1",
    "email": "test1@test1.com"
    },
    {
    "id": "643a7dffaa8cd74e5dfb2a6b",
    "userName": "test2",
    "email": "test2@test1.com"
    }
]
}
```


