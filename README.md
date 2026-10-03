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
| info | -r, --repo, --latest, --pom, --dep | Print all information about pom.xml Use ether path `--pom pom.xml` or `--dep org.springframework:spring-framework-bom:7.0.8'  |
| search | --group, --artifact | Search maven default url is `https://search.maven.org/solrsearch/select?q=g:io.micrometer&rows=200&wt=json`, list all dependencies. |

## Options

- `-s, --settings` path to settings.xml
- `-r, --repo` file path to local maven repository
- `-u, --url` url path to remote maven repository
- `--username` optional username
- `--password` optional password

# mvnindex

Tiny cli for query local maven index.

## Features

- provide autocomplete for groupId, artifactId, version
- list artifacts for given groupId
- output details about artifact
- explore repository, IT'S FUN!

## Commands

| command | args | description |
| --- | --- | --- |
| list | -i, --index, --latest, -g, -a, -v, --completion | Filter and list all dependencies |
| info | -i, --index, --latest, -g, -a, -v | Show details for given dependencies |

## Options

- `-i, --index` path to index
- `--latest` show only latest version
- `-g, -a, -v` filter by given glob like expressions
