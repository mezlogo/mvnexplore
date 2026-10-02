
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import org.slf4j.LoggerFactory

class RootCommand : CliktCommand(name = "mvnexplore") {
    private val log = LoggerFactory.getLogger(RootCommand::class.java)

    val settings: Path? by
        option(
            "-s",
            "--settings",
            help = "Path to settings.xml",
        )
        .path()

    val repo: Path by
        option(
            "-r",
            "--repo",
            help = "Path to local maven repository",
        )
        .path()
        .default(Path.of(System.getProperty("user.home"), ".m2", "repository"))

    val url: String? by
        option(
            "-u",
            "--url",
            help = "URL to remote maven repository",
        )

    val username: String? by
        option(
            "--username",
            help = "Optional username",
        )

    val password: String? by
        option(
            "--password",
            help = "Optional password",
        )

    override fun run() {
        log.trace("RootCommand parsed options: settings={}, repo={}, url={}, username={}, passwordProvided={}",
            settings, repo, url, username, password != null)
    }
}

    