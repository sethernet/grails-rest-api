
# Grails REST API with OAUTH 2

This REST API uses Grails 3.1 and the OAuth 2 protocol for client authentication. All endpoints of the application are
secure and can only be accessed with a token supplied to authenticated users. As a demo, the application lets
users submit messages, which are then hashed using the MD5 algorithm. Messages are stored in the database and associated
with the user who submitted them. Saved messages can be retrieved with an access token. 

Currently the application is only testable with curl or another REST API tool, as there are no Web views implemented 
for use with a browser.

Additionally, note that this implementation is using PostgreSQL and there is a setup.sql file in the webapi/sql folder, 
which should be run to create the database. Alternatively, you can change the database to use the out-of-the-box
H2 database by changing the specifications in build.gradle and application.yml. Make sure to name the database demo_dev.

## Run

grails run-app  


## Usage

You can test the `register` endpoint:

```sh
curl http://localhost:8080/register
```

Which will return a JSON error response indicating that you are not authorized to access the resource:

```json
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```

The application supports two OAuth grant types: the `password` grant type and `client_credentials` grant type.
The `client_credentials` grant type should only be used for for initial username and password registration.
The `password` grant type should be used for API calls that require a particular user, such as the /digests endpoint
for submitting messages.
The only part of the API that is not secured by OAuth is the token endpoint `/oauth/token` which is secured by basic authentication and allows users to obtain an access token for registration.


### Client_Credentials Grant Type

For a person who is not signed up, request an OAuth token with `client_credentials` grant type using the 
hardcoded `client_id` and `client_secret` (implemented in the Bootstrap.groovy file):

```sh
curl -X POST -u demo-client:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "grant_type=client_credentials"
```
A successful authorization will result in the JSON response:

```json
{
  "access_token": "7c82e451-1053-49be-a003-878a2a6e9fe8",
  "token_type": "bearer",
  "expires_in": 60,
  "scope": "read write"
}
```

The token is set to expire in 60 seconds. You can extend this by changing (name: "access_token_validity", value: 60)
in the changelog-security.groovy file, but you will then need to update the database.

Next, use this `access_token` to call the `/register` endpoint and create a new user:

```sh
curl http://localhost:8080/register -H "Authorization: Bearer 7c82e451-1053-49be-a003-878a2a6e9fe8" -H "Content-Type: application/json" -X POST -d '{"username":"johndoe", "password":"abc123", "email":"jd@example.com", "fullName": "John Doe"}'
```

A successful registration will result in the JSON response:

```json
{
  "fullName": "John Doe",
  "email": "jd@example.com",
  "username": "johndoe"
}
```

Note that the password is omitted.


### Password Grant Type

In order to send user-specific API requests, you need an OAuth authorization with `password` grant type 
using the `client_id` and `client_secret` for basic authentication in addition to the username and password for the 
user:

```sh
curl -X POST -u demo-client:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=abc123&username=johndoe&grant_type=password&scope=read%20write"
```

A successful authorization will result in the JSON response:

```json
{
  "access_token": "5afb2e2d-ea73-4d63-ba72-2e64f7144f92",
  "token_type": "bearer",
  "refresh_token": "00ce824b-f4ee-417c-941e-cd1fa297e287",
  "expires_in": 60
  "scope": "read write"
}
```

This token can now be used to make authorized requests to any other protected endpoints, for instance, sending
a POST to /digests, where the message will be hashed and saved.

```sh
curl http://localhost:8080/digests -H "Authorization: Bearer 5afb2e2d-ea73-4d63-ba72-2e64f7144f92" -H "Content-Type: application/json" -X POST -d {"input":"new message"}
```

And to retreive the messages:

```sh
curl http://localhost:8080/me/digests -H "Authorization: Bearer 5afb2e2d-ea73-4d63-ba72-2e64f7144f92" -H "Content-Type: application/json" -X GET
```

The 'me/digests' endpoint can also be extended to 'me/digests/$size/$from' in order to specify pagination queries.
For instance, sending 'me/digests/5/20' will send back 5 results starting from the 20th entry in the digests table.
By default, 'size' is set to 3 and 'from' is set to 0.  


### Refresh Token Grant Type

After the token has expired, you can use the `refresh_token` that was returned in the `password` authorization to retrieve a new token:

```sh
curl -X POST -u demo-client:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "grant_type=refresh_token&refresh_token=00ce824b-f4ee-417c-941e-cd1fa297e287"
