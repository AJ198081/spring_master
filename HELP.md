# Requests for testing
### IntelliJ Client request configs
 * ### Status check directly via name, and that route doesn't have any filters
GET http://localhost:8080/api-users/api/users/status/check


* ### Status check on API via gateway
GET http://localhost:8080/api/users/status/check
Cookie: jar=jfklsajf298923989328
Authorization: Bearer {{jwt-token}}

* ### Login and received JWT
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
"usernameOrEmail": "shashikala.sethi",
"password": "69b4AcPo7LKw5546pKrv82j5WJ3g33utPd2p6KE"
}

> {%
if (response.status === 200) {
client.global.set("jwt-token", response.body);
}
%}

* ### Broadcast config changes
GET http://localhost:8012/actuator/bus-refresh
