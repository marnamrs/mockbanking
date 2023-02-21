
# mockBank

Banking REST API project developed with Spring. 

This API provides controlled access and basic CRUD operations for user, account and transaction management. 



![GitHub last commit](https://img.shields.io/github/last-commit/marnamrs/mockbanking)

[Run](#run-locally) | [Authentication](#security) | [API Reference](#api-reference) | [Model Class Diagram](#diagram)

## Tech Stack

- Java (v. 17)
- Spring Boot (v. 3.0.2) | Maven
    - DevTools
    - Lombok (v.1.18.24)
    - Spring Web
    - Spring Security
    - Spring Data JPA
    - MySQL JDBC Driver (v.8.0.32)
    - Validation with Hibernate
- Oracle OpenJDK (v19.0.2)

Enrironments and Tools: 
- IntelliJ IDEA Community Ed. 2022.3.2
- MySQL Workbench Community (v.8.0)
- [@Postman](https://github.com/postmanlabs) for Linux (v. 9.31.27)
- [@JGraph](https://github.com/jgraph) drawio (v. 20.8.16)





## Run Locally

To setup the API locally, you will need to run MySQL server. Create an `application.properties` file in `src/main/resources` and populate it with the following code:

```
spring.datasource.url=jdbc:mysql://localhost:(1)/(2)?serverTimezone=UTC
spring.datasource.username=(3)
spring.datasource.password=(4)
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
server.error.include-stacktrace=never

spring.jpa.show-sql=true

```
(1) localhost port
(2) dedicated mysql schema name
(3) mysql server user
(4) mysql server password



## Security

The API requires authentication for most of its operations (check the [API Reference](#api-reference)) for endpoints available).

Access to `/api/admin/*` and `/api/client/*` paths requires regular login with username/password through the open `/api/login` endpoint. The login request will return the required Bearer Token.

It is possible to access limited functionality through the `/api/external/*` path for third parties without username/password credentials, providing a key in the request header `access-key`. 

Creation of users and obtention of third party keys can only be executed by users with a `ROLE_ADMIN` level.







## API Reference

Below is a summarized list of endpoints available.

#### Access: Authentication with `ROLE_ADMIN` | Path: api/admin/*

<details>
<summary>GET methods</summary>
<br>

```
  GET /api/admin/users
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| - | - | Returns list of all users. |

```
  GET /api/admin/users/id?id={id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

```
  GET /api/admin/externals
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| - | - | Returns list of all third parties. |

```
  GET /api/admin/externals/id?id={id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Id of item to fetch |

```
  GET /api/admin/accounts
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| - | - | Returns list of all accounts. |

```
  GET /api/admin/accounts/id?id={id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Id of item to fetch |

</details>

<details>
<summary>POST methods</summary>
<br>

```
  POST /api/admin/users/add
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody) | UserDTO | **Required**. Check UserDTO class for details. |

```
  POST /api/admin/externals/add
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody) | `string` | **Required**. Third Party name. |

Note: access key is generated upon creation of ThirdParty and logged before being encoded and saved to database. Raw access key needs to be provided to third party.

```
  POST /api/admin/accounts/add/checking
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody)  | AccountDTO | **Required**. Check AccountDTO class for details. |

```
  POST /api/admin/accounts/add/savings
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody)  | AccountDTO | **Required**. Check AccountDTO class for details. |

```
  POST /api/admin/accounts/add/credit
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody)  | AccountDTO | **Required**. Check AccountDTO class for details. |

    
</details>

<details>
<summary>Other methods</summary>
<br>
```
  PUT /api/admin/accounts/update/balance?accountId={accountId}&amount={amount}
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `accountId` | `long` | **Required**. Id of item to update. |
| `amount` | `double` | **Required**. New balance. |

```
  DELETE /api/admin/users/delete/id?id={id}
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Id of item to delete |
    
    
</details>

#### Access: Authentication with `ROLE_CLIENT` | Path: api/client/*

<details>
<summary>GET methods</summary>
<br>

```
  GET /api/client/info
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| - | - | Returns information of authenticated user. |

```
  GET /api/client/accounts
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| -      | - | Returns accounts of authenticated user |

```
  GET /api/client/accounts/id?id={id}
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Id of user account to fetch |

```
  GET /api/client/accounts/balance
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| -      | - | Returns global balance of authenticated user |
    
</details>

<details>
<summary>POST methods</summary>
<br>
    
```
  POST /api/client/transaction/new
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody)  | TransactionDTO | **Required**. Check TransactionDTO class for details. |    

</details>

#### Access: `access-key` Request Header | Path: api/external/*


<details>
<summary>POST methods</summary>
<br>
    
```
  POST /api/external/transaction/new
```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| (RequestBody)  | ExternalTransactionDTO | **Required**. Check ExternalTransactionDTO class for details. |    
| (RequestHeader) `access-key`  | String | **Required** |   

</details>


## Diagram

<details>
<summary>Model class diagram</summary>
<br>

Click on picture to view full size.

![model class UML diagram](https://github.com/marnamrs/mockbanking/blob/main/models-UMLclassdiagram.png)

</details>





