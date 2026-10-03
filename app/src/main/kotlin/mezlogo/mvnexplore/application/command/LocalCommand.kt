package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import mezlogo.mvnexplore.port.out.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.port.out.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.port.out.localrepo.LocalRepositoryConfig
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class LocalCommand(
    private val root: RootCommand,
    private val localRepoUseCase: LocalRepoUseCase,
) : CliktCommand(name = "local") {

  private val latest: Boolean by
      option(
              "--latest",
              help = "Show only latest versions",
          )
          .flag()

  private val repo: Path? by
      option(
              "-r",
              "--repo",
              help = "Path to local maven repository",
          )
          .path()

  private val includeGroupIds: List<String> by
      option(
              "--include-group",
              help = "Include group id glob (repeatable or comma separated)",
          )
          .multiple()

  private val excludeGroupIds: List<String> by
      option(
              "--exclude-group",
              help = "Exclude group id glob (repeatable or comma separated)",
          )
          .multiple()

  private val includeArtifactIds: List<String> by
      option(
              "--include-artifact",
              help = "Include artifact id glob (repeatable or comma separated)",
          )
          .multiple()

  private val excludeArtifactIds: List<String> by
      option(
              "--exclude-artifact",
              help = "Exclude artifact id glob (repeatable or comma separated)",
          )
          .multiple()

  override fun run() {

    val resolvedRepo = repo ?: root.repo

    if (!resolvedRepo.exists() || !resolvedRepo.isDirectory()) {

      echo("Repository not found: $resolvedRepo")
      return
    }

    val command =
        ListArtifactsCommand(
            includeGlobGroupIds = includeGroupIds,
            excludeGlobGroupIds = excludeGroupIds,
            includeGlobArtifactIds = includeArtifactIds,
            excludeGlobArtifactIds = excludeArtifactIds,
            onlyLatestVersions = latest,
        )

    val config = LocalRepositoryConfig(resolvedRepo)

    val artifacts = localRepoUseCase.selectArtifacts(config, command)

    val sorted = artifacts.sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))

    sorted.forEach {
      echo("${it.groupId}:${it.artifactId}:${it.version}")
    }
  }
}
