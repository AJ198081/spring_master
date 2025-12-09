# Requests for testing
## IntelliJ Client request configs
* ### Status check directly via name, and that route doesn't have any filters
GET http://localhost:8080/api-users/api/users/status/check

* ### Get a 'test' user credentials
GET http://localhost:8080/api-users/test/user

> {%
if (response.status === 200) {
client.global.set("username", response.body['username']);
client.global.set("password", response.body['password']);
}
%}

* ### Login and receive a JWT
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
"usernameOrEmail": "{{username}}",
"password": "{{password}}"
}

> if (response.status === 200) {
client.global.set("jwt-token", response.body);
}

* ### Status check on API via gateway
GET http://localhost:8080/api/users/status/check
Cookie: jar=jfklsajf298923989328
Authorization: Bearer {{jwt-token}}

* ### Broadcast config changes
POST http://localhost:8012/actuator/busrefresh


* ### Fetch properties from config server
GET http://localhost:8012/jwt/secret

* ### Get all properties being shared by config server to an application
GET http://localhost:8012/api-gateway/default
