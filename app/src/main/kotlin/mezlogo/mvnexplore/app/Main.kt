
package mezlogo.mvnexplore.app

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.app.command.InfoCommand
import mezlogo.mvnexplore.app.command.ListLocalCommand
import mezlogo.mvnexplore.app.command.RootCommand
import mezlogo.mvnexplore.adapter.out.localrepo.LocalRepoAdapter
import mezlogo.mvnexplore.adapter.out.pom.PomInfoService

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val localRepoUseCase = LocalRepoAdapter()
    val pomInfoUseCase = PomInfoService()
    val root = RootCommand()
    root
        .subcommands(
            ListLocalCommand(root, localRepoUseCase),
            InfoCommand(root, pomInfoUseCase),
        )
        .completionOption()
        .main(args)
  }
}

    