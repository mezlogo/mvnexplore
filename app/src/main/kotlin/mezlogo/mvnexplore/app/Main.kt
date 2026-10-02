
package mezlogo.mvnexplore.app

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.adapter.out.localrepo.LocalRepoAdapter
import mezlogo.mvnexplore.adapter.out.pom.PomInfoAdapter
import mezlogo.mvnexplore.adapter.out.search.SearchIndexAdapter
import mezlogo.mvnexplore.app.command.InfoCommand
import mezlogo.mvnexplore.app.command.ListLocalCommand
import mezlogo.mvnexplore.app.command.RootCommand
import mezlogo.mvnexplore.app.command.SearchCommand

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
      val root = RootCommand()
    val localRepoUseCase = LocalRepoAdapter()
    val pomInfoUseCase = PomInfoAdapter()
    val searchIndexUseCase = SearchIndexAdapter()
    root
        .subcommands(
            ListLocalCommand(root, localRepoUseCase),
            InfoCommand(root, pomInfoUseCase),
            SearchCommand(searchIndexUseCase),
        )
        .completionOption()
        .main(args)
  }
}

    