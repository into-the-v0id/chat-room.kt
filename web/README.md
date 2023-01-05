# Chat Room Web

Chat Room Web Interface

## Setup

```bash
# Build Web Server
$ docker build -t chat-room/web .
# Start Web Server
$ docker run -itp 80:80 chat-room/web
```

For development you can use the following commands:
```bash
# Install dependencies
$ docker run -it node:18-alpine npm install
# Start dev server
$ docker run -itp 3000:3000 node:18-alpine npm run dev
```

## License

Copyright (C) Oliver Amann

This project is licensed under the GNU Affero General Public License Version 3 (AGPL-3.0-only). Please see [LICENSE](./LICENSE) for more information.
