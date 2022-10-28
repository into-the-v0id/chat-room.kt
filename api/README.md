# Chat Room API

Chat Room API Server 

## About

This is the primary server backing Chat Room. For now, it only offers a simple HTTP JSON API.

## Usage

### With Docker Compose

```bash
# Build API Server
$ docker compose build app
# Start API Server & Database
$ docker compose up
```

If you want to compile and run Kotlin manually or via your IDE, only start the Database:
```bash
$ docker compose up db
```
Also, make sure you've set the relevant environment variables (See [application.conf](./src/main/resources/application.conf)).

### On Bare Metal

First start your Postgres Database. Ensure you've set the relevant environment variables (See [application.conf](./src/main/resources/application.conf)). Then run the following command in order to build and start the API Server:
```bash
$ ../gradlew :api:run
```

## Example

```bash
$ curl 'http://localhost:8080/messages?room_id=e516ba91-cef8-4dd2-ba93-e7afe82c6226'
[
    {
        "id": "fd6cab76-890e-4b0e-a5e1-5bfa35ef091e",
        "memberId": "ded57fd7-4dc0-44a7-9e37-6bc9ad05dbc6",
        "content": "Hello",
        "dateCreated": "2022-10-16T08:28:43.979Z",
        "dateUpdated": "2022-10-16T08:28:43.979Z"
    },
    {
        "id": "6ffb6e22-872a-4322-b4b1-b287eaedfaf4",
        "memberId": "ded57fd7-4dc0-44a7-9e37-6bc9ad05dbc6",
        "content": "World",
        "dateCreated": "2022-10-16T08:29:05.953Z",
        "dateUpdated": "2022-10-16T08:29:05.953Z"
    }
]
```

## Limitations

- Authentication and Authorization are not yet implemented
- No real-time communication since connecting to a raw TCP Socket is not yet supported
- Error Reporting is limited to HTTP Status Codes as of now

## License

Copyright (C) Oliver Amann

This project is licensed under the GNU Affero General Public License Version 3 (AGPL-3.0-only). Please see [LICENSE](../LICENSE) for more information.