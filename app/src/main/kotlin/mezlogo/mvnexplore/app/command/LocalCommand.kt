
package mezlogo.mvnexplore.app.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.nio.file.Files
import kotlin.io.path.*

class LocalCommand(private val root: RootCommand) : CliktCommand(name = "local") {
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

    val deps = mutableMapOf<String, String>()
    Files.walk(repo).use { stream ->
      stream
          .filter { it.isRegularFile() && it.extension == "pom" }
          .forEach { pom ->
            val relative = repo.relativize(pom.parent)
            val parts = relative.map { it.toString() }
            if (parts.size >= 3) {
              val version = parts.last()
              val artifact = parts[parts.size - 2]
              val group = parts.subList(0, parts.size - 2).joinToString(".")
              val key = "$group:$artifact"
              if (!latest || !deps.containsKey(key)) {
                deps[key] = version
              } else {
                deps[key] = version
              }
            }
          }
    }

    deps.entries.sortedBy { it.key }.forEach { (key, version) -> echo("$key:$version") }
  }
}

  