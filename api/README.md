# Chat Room API

Chat Room API Server 

## About

This is the primary server backing Chat Room. For now, it only offers a simple HTTP JSON API.

## Usage

First start the database:
```bash
$ docker-compose up -d
```

Then start the server:
```bash
$ ../gradlew :api:run
```

## Limitations

- Authentication and Authorization are not yet implemented
- No real-time communication since connecting to a raw TCP Socket is not yet supported
- Error Reporting is limited to HTTP Status Codes as of now

## License

Copyright (C) Oliver Amann

This project is licensed under the GNU Affero General Public License Version 3 (AGPL-3.0-only). Please see [LICENSE](../LICENSE) for more information.
