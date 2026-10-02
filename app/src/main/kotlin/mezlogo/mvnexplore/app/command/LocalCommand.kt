
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import mezlogo.mvnexplore.core.localrepo.ListArtifactsCommand
import mezlogo.mvnexplore.core.localrepo.LocalRepoUseCase
import mezlogo.mvnexplore.core.localrepo.LocalRepositoryConfig

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

  override fun run() {
    val repo = root.repo
    if (!repo.exists() || !repo.isDirectory()) {
      echo("Repository not found: $repo")
      return
    }

    val artifacts =
        localRepoUseCase.selectArtifacts(
            LocalRepositoryConfig(repo),
            ListArtifactsCommand(
                includeGlobGroupIds = emptyList(),
                excludeGlobGroupIds = emptyList(),
                includeGlobArtifactIds = emptyList(),
                excludeGlobArtifactIds = emptyList(),
                onlyLatestVersions = latest,
            ),
        )

    artifacts
        .sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))
        .forEach { echo("${it.groupId}:${it.artifactId}:${it.version}") }
  }
}

  