package mezlogo.mvnexplore.adapter.out.index

import java.nio.file.Files
import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import mezlogo.mvnexplore.port.out.index.ListArtifactsCommand
import org.apache.maven.index.reader.ChunkReader

class MavenIndexAdapter : IndexUseCase {
  override fun selectArtifacts(
      indexConfig: IndexConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact> {
    val indexFileGzip = indexConfig.pathToIndexGzip
    val includeGroupRegexes = listArtifactsCommand.includeGlobGroupIds.map(::globToRegex)
    val excludeGroupRegexes = listArtifactsCommand.excludeGlobGroupIds.map(::globToRegex)
    val includeArtifactRegexes = listArtifactsCommand.includeGlobArtifactIds.map(::globToRegex)
    val excludeArtifactRegexes = listArtifactsCommand.excludeGlobArtifactIds.map(::globToRegex)
    val includeVersionRegexes = listArtifactsCommand.includeGlobVersions.map(::globToRegex)

    val artifacts = mutableListOf<MavenArtifact>()

    Files.newInputStream(indexFileGzip).use { fis ->
      ChunkReader("nexus-maven-repository-index", fis).use { chunkReader ->
        /**
         * {d=This project contains libraries to interface with Zimbabwe's Leading Payments Gateway
         * Paynow, u=zw.co.paynow|java-sdk|1.1.2|javadoc|jar.asc.sha256,
         * i=jar.asc.sha256|1738155103000|64|2|2|0|jar.asc.sha256, m=1765378927627, n=java-sdk}
         */
        for (record in chunkReader.take(10)) {

          //          if (record.type == Record.Type.DOCUMENT) {
          //            val properties = record.properties
          //            val groupId = properties[Record.GROUP_ID]
          //            val artifactId = properties[Record.ARTIFACT_ID]
          //            val version = properties[Record.VERSION]
          //            if (groupId != null &&
          //                artifactId != null &&
          //                version != null &&
          //                matches(groupId, includeGroupRegexes, excludeGroupRegexes) &&
          //                matches(artifactId, includeArtifactRegexes, excludeArtifactRegexes) &&
          //                matches(version, includeVersionRegexes, emptyList())) {
          //              artifacts.add(MavenArtifact(groupId, artifactId, version))
          //            }
          //          }

          println(record)
        }
      }
    }

    return if (listArtifactsCommand.onlyLatestVersions) {
      artifacts
          .groupBy { it.groupId to it.artifactId }
          .map { (_, versions) -> versions.maxWithOrNull(artifactVersionComparator())!! }
    } else {
      artifacts
    }
  }

  private fun matches(
      value: String,
      includes: List<Regex>,
      excludes: List<Regex>,
  ): Boolean {
    if (includes.isNotEmpty() && includes.none { it.matches(value) }) return false
    if (excludes.any { it.matches(value) }) return false
    return true
  }

  private fun globToRegex(glob: String): Regex {
    val regex = StringBuilder("^")
    for (ch in glob) {
      when (ch) {
        '*' -> regex.append(".*")
        '?' -> regex.append('.')
        '.',
        '(',
        ')',
        '[',
        ']',
        '{',
        '}',
        '+',
        '^',
        '$',
        '|',
        '\\' -> regex.append('\\').append(ch)
        else -> regex.append(ch)
      }
    }
    regex.append('$')
    return Regex(regex.toString())
  }

  private fun artifactVersionComparator(): Comparator<MavenArtifact> = Comparator { a, b ->
    versionComparator().compare(a.version, b.version)
  }

  private fun versionComparator(): Comparator<String> = Comparator { v1, v2 ->
    val major1 = v1.substringBefore('.').toIntOrNull() ?: 0
    val major2 = v2.substringBefore('.').toIntOrNull() ?: 0
    major1.compareTo(major2).takeIf { it != 0 } ?: v1.compareTo(v2)
  }
}
