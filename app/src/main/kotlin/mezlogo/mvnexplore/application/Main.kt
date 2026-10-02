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
import org.slf4j.LoggerFactory

object Main {
  private val log = LoggerFactory.getLogger(Main::class.java)

  @JvmStatic
  fun main(args: Array<String>) {
    log.trace("Starting mvnexplore with args: {}", args.toList())
    val root = RootCommand()
    log.trace("Created RootCommand")
    val localRepoUseCase = LocalRepoAdapter()
    log.trace("Created LocalRepoAdapter")
    val pomInfoUseCase = PomInfoAdapter()
    log.trace("Created PomInfoAdapter")
    val searchIndexUseCase = SearchIndexAdapter()
    log.trace("Created SearchIndexAdapter")
    root
        .subcommands(
            ListLocalCommand(root, localRepoUseCase),
            InfoCommand(root, pomInfoUseCase),
            SearchCommand(root, searchIndexUseCase),
        )
        .completionOption()
        .main(args)
    log.trace("mvnexplore finished")
  }
}
