package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import mezlogo.mvnexplore.port.out.index.ListArtifactsCommand
import java.nio.file.Path

class CreateIndexCommand(
    private val root: RootCommand,
    private val indexUseCase: IndexUseCase,
) : CliktCommand(name = "create-index") {
  private val index: Path by
      option("-i", "--index", help = "Path to local maven repository index gzip file")
          .path()
          .required()

  private val latest: Boolean by
      option("--latest", help = "Show only latest version").flag(default = false)

  private val groupIds: List<String> by option("-g", help = "Filter by group id glob").multiple()

  private val artifactIds: List<String> by
      option("-a", help = "Filter by artifact id glob").multiple()

  private val tookLastDays: Int? by
      option("--last-days", help = "Create index with updates from last days.").int()

  private val versions: List<String> by option("-v", help = "Filter by version glob").multiple()

  private val output: Path by option("-o", help = "Path to new index").path().required()

  override fun run() {
    val indexConfig = IndexConfig(pathToIndexGzip = index)

    val updatedAfterTimestamp: Long? = tookLastDays?.let { days ->
      val now = System.currentTimeMillis()
      val millisPerDay = 24L * 60L * 60L * 1000L
      now - days.toLong() * millisPerDay
    }

    val command =
        ListArtifactsCommand(
            includeGlobGroupIds = groupIds,
            excludeGlobGroupIds = emptyList(),
            includeGlobArtifactIds = artifactIds,
            excludeGlobArtifactIds = emptyList(),
            includeGlobVersions = versions,
            onlyLatestVersions = latest,
            tookOnlyUpdatedAfterTimestamps = updatedAfterTimestamp,
        )
    indexUseCase.createGroupAndArtifactId(indexConfig, output)
  }
}
