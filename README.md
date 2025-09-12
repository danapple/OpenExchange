# Introduction

OpenExchange is the beginnings of an open source reference implementation of a single leg securities exchange.

# Structure
exchange: The exchange directory contains the source for the running exchange.  It is currently to only support two instruments, with ID's 0 and 1.

dtos: The dtos directory contains the Data Transfer Objects that are used by OpenExchange, and can be leveraged by Java or Kotlin web clients, for the REST interactions.

example-clients: The example-clients directory contains source code of example programs to interact with OpenExchange.  Currently, there are only some simply python programs and a simple bash wrapper.

## To Run:
$ make run

## To run in docker:
$ make run-docker
