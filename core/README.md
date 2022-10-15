# Chat Room Core

Chat Room Core classes

## About

This module is intended to be used as a library in other modules. It can not be run standalone.

The Core module contains classes for events, aggregates, repositories and more.  
The Data Model is built around a concept called Event Sourcing. Basically, every create/update/delete operation is represented as one or more events. The Database contains only those events. In order to get the current state, all events need to be layered on top of each other (aka. aggregated).

## License

Copyright (C) Oliver Amann

This project is licensed under the GNU Affero General Public License Version 3 (AGPL-3.0-only). Please see [LICENSE](../LICENSE) for more information.
