# mvnexplore

Maven dependency format is a defactor standard for all jvm dependencies. You can use gradle, bazel, sbt however mvnrepo with pom.xml is an entry point for your framework of choice.

This small cli can build a graph, simulate conflict resolution, download jar with code or even sources - it's just simplifies dependencies related routines.

## Features

- traverse local maven repository
- query and download dependencies from remote

## Commands

| command | args | description |
| --- | --- | --- |
| local | -r, --repo, --latest | List all dependencies in gradle like format from local |

## Options

- `-s, --settings` path to settings.xml
- `-r, --repo` file path to local maven repository
- `-u, --url` url path to remote maven repository
- `--username` optional username
- `--password` optional password

