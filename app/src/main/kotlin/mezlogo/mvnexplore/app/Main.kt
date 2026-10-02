
package mezlogo.mvnexplore.app

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.app.command.LocalCommand
import mezlogo.mvnexplore.app.command.RootCommand

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val root = RootCommand()
    root
        .subcommands(
            LocalCommand(root),
        )
        .completionOption()
        .main(args)
  }
}

  