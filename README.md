# Introduction

OpenExchange is the beginnings of an open source reference implementation of a single leg securities exchange.

# Structure
The dtos directory contains the Data Transfer Objects that are used by OpenExchange, and can be leveraged by Java or Kotlin web clients, for the REST interactions.

The exchange directory contains the source for the running exchange.  It is currently to only support two instruments, with ID's 0 and 1.

## To Run:
$ make run

## To run in docker:
$ make run-docker
