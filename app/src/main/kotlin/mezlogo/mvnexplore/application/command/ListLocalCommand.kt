package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import mezlogo.mvnexplore.port.out.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.port.out.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.port.out.localrepo.LocalRepositoryConfig
import org.slf4j.LoggerFactory

class ListLocalCommand(
    private val root: RootCommand,
    private val localRepoUseCase: LocalRepoUseCase,
) : CliktCommand(name = "list") {
  private val log = LoggerFactory.getLogger(ListLocalCommand::class.java)

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
    root.applyVerboseLogging()

    log.trace(
        "Running ListLocalCommand with latest={}, repo={}, includeGroupIds={}, excludeGroupIds={}, includeArtifactIds={}, excludeArtifactIds={}",
        latest,
        repo,
        includeGroupIds,
        excludeGroupIds,
        includeArtifactIds,
        excludeArtifactIds,
    )
    val resolvedRepo = repo ?: root.repo
    log.trace("Resolved repo: {}", resolvedRepo)
    if (!resolvedRepo.exists() || !resolvedRepo.isDirectory()) {
      log.trace("Repository not found or not a directory: {}", resolvedRepo)
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
    log.trace("Created ListArtifactsCommand: {}", command)
    val config = LocalRepositoryConfig(resolvedRepo)
    log.trace("Created LocalRepositoryConfig: {}", config)
    val artifacts = localRepoUseCase.selectArtifacts(config, command)
    log.trace("Received artifacts: {}", artifacts)

    val sorted = artifacts.sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))
    log.trace("Sorted artifacts: {}", sorted)
    sorted.forEach {
      log.trace("Printing artifact: {}", it)
      echo("${it.groupId}:${it.artifactId}:${it.version}")
    }
  }
}
