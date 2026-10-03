package mezlogo.mvnexplore.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.adapter.out.localrepo.LocalRepoAdapter
import mezlogo.mvnexplore.adapter.out.pom.PomInfoAdapter
import mezlogo.mvnexplore.adapter.out.search.SearchIndexAdapter
import mezlogo.mvnexplore.application.command.InfoCommand
import mezlogo.mvnexplore.application.command.ListLocalCommand
import mezlogo.mvnexplore.application.command.RootCommand
import mezlogo.mvnexplore.application.command.SearchCommand

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
            SearchCommand(root, searchIndexUseCase),
        )
        .completionOption()
        .main(args)
  }
}
