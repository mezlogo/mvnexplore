package mezlogo.mvnexplore.adapter.out.localrepo

import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.port.out.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.port.out.localrepo.LocalRepositoryConfig
import org.slf4j.LoggerFactory
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

/** Use only file names, don't read pom.xml at all, however check that pom.xml is here. */
class LocalRepoAdapter : LocalRepoUseCase {
  private val log = LoggerFactory.getLogger(LocalRepoAdapter::class.java)

  override fun selectArtifacts(
      localRepositoryConfig: LocalRepositoryConfig,
      listArtifactsCommand: ListArtifactsCommand,
  ): List<MavenArtifact> {
    log.trace(
        "selectArtifacts called with config={}, command={}",
        localRepositoryConfig,
        listArtifactsCommand,
    )
    val repository = localRepositoryConfig.m2repository
    log.trace("Repository path: {}", repository)
    if (!Files.isDirectory(repository)) {
      log.trace("Repository is not a directory, returning empty list")
      return emptyList()
    }

    val artifacts = mutableListOf<MavenArtifact>()
    log.trace("Walking repository: {}", repository)
    Files.walk(repository).use { stream ->
      stream
          .filter { it.isRegularFile() && it.extension == "pom" }
          .forEach { pom ->
            log.trace("Found pom file: {}", pom)
            val relative = repository.relativize(pom.parent)
            val parts = relative.map { it.toString() }
            log.trace("Relative path: {}, parts: {}", relative, parts)
            if (parts.size >= 3) {
              val version = parts.last()
              val artifactId = parts[parts.size - 2]
              val groupId = parts.subList(0, parts.size - 2).joinToString(".")
              val artifact = MavenArtifact(groupId, artifactId, version)
              log.trace("Constructed artifact: {}", artifact)
              val matches = matchesFilters(artifact, listArtifactsCommand)
              log.trace("Artifact {} matches filters? {}", artifact, matches)
              if (matches) {
                artifacts += artifact
              }
            } else {
              log.trace("Skipping pom because parts.size < 3: {}", parts)
            }
          }
    }

    log.trace("Collected artifacts: {}", artifacts)
    return if (listArtifactsCommand.onlyLatestVersions) {
      log.trace("Filtering only latest versions")
      artifacts
          .groupBy { it.groupId to it.artifactId }
          .mapNotNull { (key, versions) ->
            log.trace("Selecting latest for groupId+artifactId={}, versions={}", key, versions)
            val latest =
                versions.maxWithOrNull(
                    Comparator { left, right ->
                      val cmp = compareVersions(left.version, right.version)
                      log.trace(
                          "Comparing versions: {} vs {} -> {}",
                          left.version,
                          right.version,
                          cmp,
                      )
                      cmp
                    }
                )
            log.trace("Latest for {} is {}", key, latest)
            latest
          }
    } else {
      log.trace("Distincting artifacts")
      artifacts.distinct()
    }
  }

  private fun matchesFilters(artifact: MavenArtifact, command: ListArtifactsCommand): Boolean {
    log.trace("matchesFilters for artifact={}, command={}", artifact, command)
    val includeGroup = matchesInclude(artifact.groupId, command.includeGlobGroupIds)
    val excludeGroup = matchesAny(artifact.groupId, command.excludeGlobGroupIds)
    val includeArtifact = matchesInclude(artifact.artifactId, command.includeGlobArtifactIds)
    val excludeArtifact = matchesAny(artifact.artifactId, command.excludeGlobArtifactIds)
    log.trace(
        "Filter results: includeGroup={}, excludeGroup={}, includeArtifact={}, excludeArtifact={}",
        includeGroup,
        excludeGroup,
        includeArtifact,
        excludeArtifact,
    )
    return includeGroup && !excludeGroup && includeArtifact && !excludeArtifact
  }

  private fun matchesInclude(value: String, patterns: List<String>): Boolean {
    log.trace("matchesInclude value={}, patterns={}", value, patterns)
    val result = patterns.isEmpty() || matchesAny(value, patterns)
    log.trace("matchesInclude result: {}", result)
    return result
  }

  private fun matchesAny(value: String, patterns: List<String>): Boolean {
    log.trace("matchesAny value={}, patterns={}", value, patterns)
    val result = patterns.any { globToRegex(it).matches(value) }
    log.trace("matchesAny result: {}", result)
    return result
  }

  private fun globToRegex(glob: String): Regex {
    log.trace("globToRegex glob={}", glob)
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
    log.trace("globToRegex result: {}", regex)
    return Regex(regex)
  }

  private fun compareVersions(left: String, right: String): Int {
    log.trace("compareVersions left={}, right={}", left, right)
    val leftParts = left.split('.', '-', '_').mapNotNull { it.toIntOrNull() }
    val rightParts = right.split('.', '-', '_').mapNotNull { it.toIntOrNull() }
    log.trace("leftParts={}, rightParts={}", leftParts, rightParts)
    val maxSize = maxOf(leftParts.size, rightParts.size)
    for (i in 0 until maxSize) {
      val l = leftParts.getOrElse(i) { 0 }
      val r = rightParts.getOrElse(i) { 0 }
      if (l != r) {
        val cmp = l.compareTo(r)
        log.trace("Comparing index {}: {} vs {} -> {}", i, l, r, cmp)
        return cmp
      }
    }
    val cmp = left.compareTo(right)
    log.trace(
        "All numeric parts equal, fallback to string compare: {} vs {} -> {}",
        left,
        right,
        cmp,
    )
    return cmp
  }
}
