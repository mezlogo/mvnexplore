package mezlogo.mvnexplore.application.command

import ch.qos.logback.classic.Level
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import ch.qos.logback.classic.Logger as LogbackLogger

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

  val verbose: Boolean by
      option(
              "-v",
              "--verbose",
              help = "Enable TRACE logging",
          )
          .flag()

  override fun run() {
    applyVerboseLogging()
    log.trace(
        "RootCommand parsed options: verbose={}, settings={}, repo={}, url={}, username={}, passwordProvided={}",
        verbose,
        settings,
        repo,
        url,
        username,
        password != null,
    )
  }

  fun applyVerboseLogging() {
    if (verbose) {
      val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
      if (rootLogger is LogbackLogger) {
        rootLogger.level = Level.TRACE
      }
    }
  }
}
