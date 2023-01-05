# Chat Room API

Chat Room API Server 

## About

This is the primary server backing Chat Room. For now, it only offers a simple HTTP JSON API.

The Data Model is built around a concept called Event Sourcing. Basically, every create/update/delete operation is represented as one or more events. The Database contains only those events. In order to get the current state, all events need to be layered on top of each other (aka. aggregated).

## Setup

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
Also, make sure you've set the relevant environment variables (See [application.conf](./api/src/main/resources/application.conf)).

### On Bare Metal

First start your Postgres Database. Ensure you've set the relevant environment variables (See [application.conf](./api/src/main/resources/application.conf)). Then run the following command in order to build and start the API Server:
```bash
$ ./gradlew :api:run
```

## Example

```bash
$ curl 'http://localhost:8080/messages?room_id=018562c4-fb64-7a84-b187-aec860bcbff8'
[
    {
        "id": "018562ef-631c-74eb-aadb-22b5a63ad507",
        "memberId": "018562ea-de2d-70d1-b39b-e058a13dc967",
        "content": "Hello",
        "dateCreated": "2022-12-30T12:08:53.788369Z",
        "dateUpdated": "2022-12-30T12:08:53.788369Z"
    },
    {
        "id": "018562ef-89b0-7284-be73-329a16c397aa",
        "memberId": "018562ea-de2d-70d1-b39b-e058a13dc967",
        "content": "World",
        "dateCreated": "2022-12-30T12:09:03.664871Z",
        "dateUpdated": "2022-12-30T12:09:03.664871Z"
    }
]
```

## Limitations

- Authentication and Authorization are not yet implemented
- No real-time communication since connecting to a raw TCP Socket is not yet supported
- Error Reporting is limited to HTTP Status Codes as of now

## License

Copyright (C) Oliver Amann

This project is licensed under the GNU Affero General Public License Version 3 (AGPL-3.0-only). Please see [LICENSE](./LICENSE) for more information.
