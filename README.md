# mvnexplore

Maven dependency format is a de-facto standard for all jvm dependencies. You can use gradle, bazel, sbt however mvnrepo with pom.xml is an entry point for your framework of choice.

This small cli can build a graph, simulate conflict resolution, download jar with code or even sources - it's just simplifies dependencies related routines.

## Features

- fast traverse local maven repository using only filesystem operations
- parse pom.xml and pretty print info about dep: group, artifact, version, description, parent, properties, dependencies, optionals, bom
- provide autocomplete for groupId, artifactId, version
- list artifacts for given groupId
- output details about artifact
- explore repository, IT'S FUN!

## Commands

| command | args | description |
| --- | --- | --- |
| local | -r, --repo, --latest | Query local maven repository and print gradle like format |
| info | -r, --repo, --latest, --pom, -g, -a, -v | Print all information about pom.xml Use ether path `--pom pom.xml` or `--a org.springframework -b spring-framework-bom'  |
| restsearch | --group, --artifact | Search maven default url is `https://search.maven.org/solrsearch/select?q=g:io.micrometer&rows=200&wt=json`, list all dependencies. |
| index | -i, --index, --latest, -g, -a, -v, --completion, --details | Filter and list all dependencies in index. |

## Options

- `-s, --settings` path to settings.xml
- `-r, --repo` file path to local maven repository
- `-u, --url` url path to remote maven repository
- `--username` optional username
- `--password` optional password
- `-i, --index` path to index
- `--latest` show only latest version
- `-g, -a, -v` filter by given glob like expressions
