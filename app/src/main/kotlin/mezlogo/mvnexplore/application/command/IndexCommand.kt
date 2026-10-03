package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import mezlogo.mvnexplore.port.out.index.ListArtifactsCommand

class IndexCommand(
    private val root: RootCommand,
    private val indexUseCase: IndexUseCase,
) : CliktCommand(name = "index") {
  private val index: Path by
      option("-i", "--index", help = "Path to local maven repository index gzip file")
          .path()
          .required()

  private val latest: Boolean by
      option("--latest", help = "Show only latest version").flag(default = false)

  private val groupIds: List<String> by option("-g", help = "Filter by group id glob").multiple()

  private val artifactIds: List<String> by
      option("-a", help = "Filter by artifact id glob").multiple()

  private val versions: List<String> by option("-v", help = "Filter by version glob").multiple()

  override fun run() {
    val indexConfig = IndexConfig(pathToIndexGzip = index)
    val command =
        ListArtifactsCommand(
            includeGlobGroupIds = groupIds,
            excludeGlobGroupIds = emptyList(),
            includeGlobArtifactIds = artifactIds,
            excludeGlobArtifactIds = emptyList(),
            includeGlobVersions = versions,
            onlyLatestVersions = latest,
        )
    val artifacts = indexUseCase.selectArtifacts(indexConfig, command)
    artifacts.forEach { echo("${it.groupId}:${it.artifactId}:${it.version}") }
  }
}
