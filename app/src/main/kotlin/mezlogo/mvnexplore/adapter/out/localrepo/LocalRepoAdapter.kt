package mezlogo.mvnexplore.adapter.out.localrepo

import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.port.out.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.port.out.localrepo.LocalRepositoryConfig
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

/** Use only file names, don't read pom.xml at all, however check that pom.xml is here. */
class LocalRepoAdapter : LocalRepoUseCase {

  override fun selectArtifacts(
      localRepositoryConfig: LocalRepositoryConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact> {

    val repository = localRepositoryConfig.m2repository

    if (!Files.isDirectory(repository)) {

      return emptyList()
    }

    val artifacts = mutableListOf<MavenArtifact>()

    Files.walk(repository).use { stream ->
      stream
          .filter { it.isRegularFile() && it.extension == "pom" }
          .forEach { pom ->
            val relative = repository.relativize(pom.parent)
            val parts = relative.map { it.toString() }

            if (parts.size >= 3) {
              val version = parts.last()
              val artifactId = parts[parts.size - 2]
              val groupId = parts.subList(0, parts.size - 2).joinToString(".")
              val artifact = MavenArtifact(groupId, artifactId, version)

              val matches = matchesFilters(artifact, listArtifactsCommand)

              if (matches) {
                artifacts += artifact
              }
            } else {}
          }
    }

    return if (listArtifactsCommand.onlyLatestVersions) {

      artifacts
          .groupBy { it.groupId to it.artifactId }
          .mapNotNull { (key, versions) ->
            val latest =
                versions.maxWithOrNull(
                    Comparator { left, right ->
                      val cmp = compareVersions(left.version, right.version)

                      cmp
                    }
                )

            latest
          }
    } else {

      artifacts.distinct()
    }
  }

  private fun matchesFilters(artifact: MavenArtifact, command: ListArtifactsCommand): Boolean {

    val includeGroup = matchesInclude(artifact.groupId, command.includeGlobGroupIds)
    val excludeGroup = matchesAny(artifact.groupId, command.excludeGlobGroupIds)
    val includeArtifact = matchesInclude(artifact.artifactId, command.includeGlobArtifactIds)
    val excludeArtifact = matchesAny(artifact.artifactId, command.excludeGlobArtifactIds)

    return includeGroup && !excludeGroup && includeArtifact && !excludeArtifact
  }

  private fun matchesInclude(value: String, patterns: List<String>): Boolean {

    val result = patterns.isEmpty() || matchesAny(value, patterns)

    return result
  }

  private fun matchesAny(value: String, patterns: List<String>): Boolean {

    val result = patterns.any { globToRegex(it).matches(value) }

    return result
  }

  private fun globToRegex(glob: String): Regex {

    val regex = buildString {
      append('^')
      glob.forEach { ch ->
        when (ch) {
          '*' -> append(".*")
          '?' -> append('.')
          '.',
          '\\',
          '+',
          '(',
          ')',
          '[',
          ']',
          '{',
          '}',
          '^',
          '$',
          '|' -> {
            append('\\')
            append(ch)
          }
          else -> append(ch)
        }
      }
      append('$')
    }

    return Regex(regex)
  }

  private fun compareVersions(left: String, right: String): Int {

    val leftParts = left.split('.', '-', '_').mapNotNull { it.toIntOrNull() }
    val rightParts = right.split('.', '-', '_').mapNotNull { it.toIntOrNull() }

    val maxSize = maxOf(leftParts.size, rightParts.size)
    for (i in 0 until maxSize) {
      val l = leftParts.getOrElse(i) { 0 }
      val r = rightParts.getOrElse(i) { 0 }
      if (l != r) {
        val cmp = l.compareTo(r)

        return cmp
      }
    }
    val cmp = left.compareTo(right)

    return cmp
  }
}
