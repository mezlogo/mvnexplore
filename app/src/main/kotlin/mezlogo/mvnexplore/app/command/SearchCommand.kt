
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase

class SearchCommand(
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
        val artifacts =
            searchIndexUseCase.searchArtifacts(
                SearchArtifactsRequest(
                    groupGlob = group,
                    artifactGlob = artifact,
                ),
            )
        artifacts
            .sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))
            .forEach { echo("${it.groupId}:${it.artifactId}:${it.version}") }
    }
}

    