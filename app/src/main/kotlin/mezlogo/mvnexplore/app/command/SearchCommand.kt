
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase
import org.slf4j.LoggerFactory

class SearchCommand(
    private val searchIndexUseCase: SearchIndexUseCase,
) : CliktCommand(name = "search") {
    private val log = LoggerFactory.getLogger(SearchCommand::class.java)

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
        log.trace("Running SearchCommand with group={}, artifact={}", group, artifact)
        val request = SearchArtifactsRequest(
            groupGlob = group,
            artifactGlob = artifact,
        )
        log.trace("Created SearchArtifactsRequest: {}", request)
        val artifacts =
            searchIndexUseCase.searchArtifacts(request)
        log.trace("Received artifacts: {}", artifacts)
        val sorted = artifacts.sortedWith(compareBy({ it.groupId }, { it.artifactId }, { it.version }))
        log.trace("Sorted artifacts: {}", sorted)
        sorted.forEach {
            log.trace("Printing artifact: {}", it)
            echo("${it.groupId}:${it.artifactId}:${it.version}")
        }
    }
}

    