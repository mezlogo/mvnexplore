# mvnexplore

Maven dependency format is a de-facto standard for all jvm dependencies. You can use gradle, bazel, sbt however mvnrepo with pom.xml is an entry point for your framework of choice.

This small cli can build a graph, simulate conflict resolution, download jar with code or even sources - it's just simplifies dependencies related routines.

## Features

- fast traverse local maven repository using only filesystem operations
- parse pom.xml and pretty print info about dep: group, artifact, version, description, parent, properties, dependencies, optionals, bom

## Commands

| command | args | description |
| --- | --- | --- |
| list | -r, --repo, --latest | List all dependencies in gradle like format from local |
| info | -r, --repo, --latest, --pom, --dep | Print all information about  |

## Options

- `-s, --settings` path to settings.xml
- `-r, --repo` file path to local maven repository
- `-u, --url` url path to remote maven repository
- `--username` optional username
- `--password` optional password

