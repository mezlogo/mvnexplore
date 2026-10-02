
package mezlogo.mvnexplore.core.localrepo.impl

import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import mezlogo.mvnexplore.core.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.core.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.core.localrepo.LocalRepositoryConfig
import mezlogo.mvnexplore.port.model.MavenArtifact

/**
 * Use only file names, don't read pom.xml at all, however check that pom.xml is here.
 */
class LocalRepoService : LocalRepoUseCase {
    override fun selectArtifacts(
        localRepositoryConfig: LocalRepositoryConfig,
        listArtifactsCommand: ListArtifactsCommand
    ): List<MavenArtifact> {
        val repository = localRepositoryConfig.m2repository
        if (!Files.isDirectory(repository)) return emptyList()

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
                        if (matchesFilters(artifact, listArtifactsCommand)) {
                            artifacts += artifact
                        }
                    }
                }
        }

        return if (listArtifactsCommand.onlyLatestVersions) {
            artifacts
                .groupBy { it.groupId to it.artifactId }
                .mapNotNull { (_, versions) ->
                    versions.maxWithOrNull(Comparator { left, right ->
                        compareVersions(left.version, right.version)
                    })
                }
        } else {
            artifacts.distinct()
        }
    }

    private fun matchesFilters(artifact: MavenArtifact, command: ListArtifactsCommand): Boolean {
        return matchesInclude(artifact.groupId, command.includeGlobGroupIds) &&
            !matchesAny(artifact.groupId, command.excludeGlobGroupIds) &&
            matchesInclude(artifact.artifactId, command.includeGlobArtifactIds) &&
            !matchesAny(artifact.artifactId, command.excludeGlobArtifactIds)
    }

    private fun matchesInclude(value: String, patterns: List<String>): Boolean =
        patterns.isEmpty() || matchesAny(value, patterns)

    private fun matchesAny(value: String, patterns: List<String>): Boolean =
        patterns.any { globToRegex(it).matches(value) }

    private fun globToRegex(glob: String): Regex {
        val regex = buildString {
            append('^')
            glob.forEach { ch ->
                when (ch) {
                    '*' -> append(".*")
                    '?' -> append('.')
                    '.', '\\', '+', '(', ')', '[', ']', '{', '}', '^', '$', '|' -> {
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
            if (l != r) return l.compareTo(r)
        }
        return left.compareTo(right)
    }
}

  