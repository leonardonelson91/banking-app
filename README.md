Distributed banking application, composed by REST APIs for account/transaction management and reporting. A React application is also available to operate the APIs by providing features like account/transaction creation, list of accounts, balances and list of acccount transactions.
# Pre-requisites

You must have the following tools installed in your system in order to run the application:

  - JDK 15
  - Node
  - Docker

# Installation

**Services (PostgreSQL Database, Redis Cache, RabbitMQ)**

Open a new terminal window and run the follwing commands:

```sh
$ cd scripts
$ docker-compose up -V
```
`Once all the three service containers are up and running, please take a note of the generated Customer ID in the PostgreSQL's initialization logs. You'll need it to create new accounts using the API or the Web App. You should see something like:`

> db_1        |              customer_id              
db_1        | --------------------------------------
db_1        |  **0d8cf7fa-098d-470c-a09a-bbbb3927436c**
db_1        | (1 row)


**Reports Web Application**

Open a new terminal window and run the follwing commands:

```sh
$ cd webapp
$ npm install
$ npm start
```
The app must be opened automatically in your browser. If it doesn't, then just open your browser and go to http://localhost:3000

**Accounts API**

Open a new terminal window and run the follwing commands:

```sh
$ cd appplications/account
$ JAVA_HOME=<YourJDK15HomePath> ./gradlew :applications:account:bootRun
```
The API should be accessible at http://localhost:8080/accounts

**Reports API**

Open a new terminal window and run the follwing commands:

```sh
$ cd appplications/reports
$ JAVA_HOME=<YourJDK15HomePath> ./gradlew :applications:reports:bootRun
```
The API should be accessible at http://localhost:8082/reports/accounts