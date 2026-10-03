package mezlogo.mvnexplore.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnexplore.adapter.out.index.MavenIndexAdapter
import mezlogo.mvnexplore.adapter.out.localrepo.LocalRepoAdapter
import mezlogo.mvnexplore.adapter.out.pom.PomInfoAdapter
import mezlogo.mvnexplore.adapter.out.search.SearchIndexAdapter
import mezlogo.mvnexplore.application.command.*

object Main {

  @JvmStatic
  fun main(args: Array<String>) {
    val root = RootCommand()
    val localRepoUseCase = LocalRepoAdapter()
    val pomInfoUseCase = PomInfoAdapter()
    val searchIndexUseCase = SearchIndexAdapter()
    val indexUseCase = MavenIndexAdapter()

    root
        .subcommands(
            LocalCommand(root, localRepoUseCase),
            InfoCommand(root, pomInfoUseCase),
            SearchCommand(root, searchIndexUseCase),
            IndexCommand(root, indexUseCase),
        )
        .completionOption()
        .main(args)
  }
}
