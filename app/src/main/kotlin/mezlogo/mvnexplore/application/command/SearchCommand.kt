package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase

class SearchCommand(
    private val root: RootCommand,
    private val searchIndexUseCase: SearchIndexUseCase,
) : CliktCommand(name = "search") {

  private val group: String? by
      option(
          "--group",
          help = "Group id glob",
      )

  private val artifact: String? by
      option(
          "--artifact",
          help = "Artifact id glob",
      )

  override fun run() {

    val request =
        SearchArtifactsRequest(
            groupGlob = group,
            artifactGlob = artifact,
        )

    val artifacts = searchIndexUseCase.searchArtifacts(request)

    val sorted = artifacts.sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))

    sorted.forEach {
      echo("${it.groupId}:${it.artifactId}:${it.version}")
    }
  }
}
