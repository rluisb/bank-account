# Bank Account

A simple bank-account application for account management. 

## Tecnologies

 - [Kotlin](https://kotlinlang.org/) - Programing Language
 - [Spring Boot](https://spring.io/projects/spring-boot) - Framework Ecosystem
 - [Gradle](https://gradle.org/) - Build Tool
 - [Docker](https://www.docker.com/) - Containerization
 - [MongoDB](https://www.mongodb.com/) - Database

## Dependencies

This project depends on [simplecpfvalidator](https://github.com/LeoColman/SimpleCpfValidator) to make brazilian document (CPF) validations.

## Application execution

To run this project you need to execute the Makefile located in project root folder. 

### Running in Linux/MacOS
Execute in your terminal the following command:
```bash
	make
```

### Running in Windows
Execute in your terminal the following command:
```batch
	nmake
```

After this command start it`s execution, the application will be built, tested and run our docker-compose to startup mongodb and spring boot containers.

## How to use



### Features

> Follow the features in this application.

##### Create an account

> Create a new account for name and document.

**Request**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|

---------

**Response**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.accountNumber`   |*string* | it's the generated account number|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|
|`body.balance`   |*string* | it's the balance of created account. The default is 0.|

**Example**

```bash
curl -X POST "http://localhost:8080/api/bank/account" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"name\": \"John Doe\", \"document\": \"846.040.910-48\"}"
```

---------

##### List accounts

> List all registered accounts.

**Response**

Return a list of

| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.accountNumber`   |*string* | it's the generated account number|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|
|`body.balance`   |*string* | it's the balance of the account.|

**Example**

```bash
curl -X GET "http://localhost:8080/api/bank/account" -H "accept: application/json"
```

---------

##### Get specific accounts

> Get account by accountNumber.


**Request**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`path.accountNumber`   |*string* | it's just the accountNumber for request|

---------

**Response**


| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.accountNumber`   |*string* | it's the account number|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|
|`body.balance`   |*string* | it's the balance of the account.|

**Example**

```bash
curl -X GET "http://localhost:8080/api/bank/account/601b2852430a81472ed147ba" -H "accept: application/json"
```

---------

##### Deposit value to a specific account

> Execute a deposit operation for an accountNumber and amount

**Request**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`path.accountNumber`   |*string* | it's the accountNumber for deposit|
|`body.amount`   |*number* | it's the amount to deposit|

-------


**Response**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.accountNumber`   |*string* | it's the account number that received the deposit|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|
|`body.balance`   |*string* | it's the balance of the account but now with updated after deposit request.|


**Example**

```bash
curl -X PATCH "http://localhost:8080/api/bank/account/601b2852430a81472ed147ba/deposit" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"amount\": 100}"
```

---------

##### Get list of account`s deposits

> List all account`s deposits history.


**Request**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`path.accountNumber`   |*string* | it's just the accountNumber for request|

---------

**Response**


| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.amount`   |*number* | it's the amount of the deposit|
|`body.time`   |*date* | it's the time that occurred the deposit|

**Example**

```bash
curl -X GET "http://localhost:8080/api/bank/account/601b2852430a81472ed147ba/deposit" -H "accept: application/json"
```

---------

##### Trasnfer value between accounts

> Execute a transfer operation from an account to another account by them accountNumber and amount.


| Field | Type    | Options           |
|----------|---------|-------------------|
|`path.originAccountNumber`   |*string* | it's the accountNumber that will send the transfer|
|`body.targetAccountNumber`   |*string* | it's the accountNumber that will receive the transfer|
|`body.amount`   |*number* | it's the amount to transfer|

-------


**Response**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.accountNumber`   |*string* | it's the accountNumber that sent the transfer|
|`body.name`   |*string* | it's just the name of account owner|
|`body.document`   |*string* | it's the document (CPF) of the account owner|
|`body.balance`   |*string* | it's the balance of the account but now with updated value after transfer request.|


**Example**

```bash
curl -X PATCH "http://localhost:8080/api/bank/account/601b2852430a81472ed147ba/transfer" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"targetAccountNumber\": \"601b2b5f430a81472ed147bc\", \"amount\": 10}"
```

---------

##### Get list of account`s transfers

> List all account`s transfers history.


**Request**

| Field | Type    | Options           |
|----------|---------|-------------------|
|`path.accountNumber`   |*string* | it's just the accountNumber for request|

---------

**Response**


| Field | Type    | Options           |
|----------|---------|-------------------|
|`body.targetAccountNumber`   |*string* | it's the targetAccountNumber for transfer|
|`body.amount`   |*number* | it's the amount of the deposit|
|`body.time`   |*date* | it's the time that occurred the deposit|

**Example**

```bash
curl -X GET "http://localhost:8080/api/bank/account/601b2852430a81472ed147ba/transfer" -H "accept: application/json"
```

-----------
## Authors

| ![Ricardo Conceição](https://avatars1.githubusercontent.com/u/11237921?v=3&s=150)|
|:---------------------:|
|  [Ricardo Conceição](https://github.com/rluisb/)   |

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details