
package mezlogo.mvnexplore.app

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.app.command.LocalCommand
import mezlogo.mvnexplore.app.command.RootCommand
import mezlogo.mvnexplore.core.localrepo.impl.LocalRepoService

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val localRepoUseCase = LocalRepoService()
    val root = RootCommand()
    root
        .subcommands(
            LocalCommand(root, localRepoUseCase),
        )
        .completionOption()
        .main(args)
  }
}

  