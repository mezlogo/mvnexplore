package mezlogo.mvnexplore.application.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

class RootCommand : CliktCommand(name = "mvnexplore") {

  val repo: Path by
      option(
              "-r",
              "--repo",
              help = "Path to local maven repository",
          )
          .path()
          .default(Path.of(System.getProperty("user.home"), ".m2", "repository"))

  override fun run() {}
}
