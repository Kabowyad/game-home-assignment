# Rock-Paper-Scissors Multiplayer Game

This project is a simple multiplayer Rock-Paper-Scissors game implemented using Java, Kryonet, and Spring Boot.

## Description

The game allows multiple players to connect to a server and play the classic Rock-Paper-Scissors game against each other. The server manages game sessions, player connections, and game state updates. The game features a simple text-based user interface for user input and interactions.

## Features

- Multiplayer support
- Text-based user interface
- User authentication (signup and signin)
- Game session management

## Technologies

- Java 16
- Kryonet for network communication
- Spring Boot for server-side application
- Gradle for building the project

## Getting Started

### Prerequisites

- Java 16
- Gradle

### Building the project

1. Clone the repository
    ```sh
    git clone https://github.com/Kabowyad/game-home-assignment
    ```

2. Navigate to root folder
    ```sh
    cd rock-paper-scissors
    ```
   
3. Build using gradle
    ```sh
   ./gradlew build
   ```

4. Go to server folder and start docker-compose
    ```sh
   cd server/
   docker-compose up --build
   ```

5. Go to client folder and start as many clients as you want
   ```sh
   cd game-home-assignment/client/build/libs/
   java -jar client-1.0-SNAPSHOT.jar
   ```
   
