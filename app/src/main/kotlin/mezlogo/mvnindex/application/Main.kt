package mezlogo.mvnindex.application

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import mezlogo.mvnindex.adapter.out.index.MavenIndexAdapter
import mezlogo.mvnindex.application.command.ListCommand
import mezlogo.mvnindex.application.command.RootCommand

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val root = RootCommand()
    val indexUseCase = MavenIndexAdapter()
    root
        .subcommands(
            ListCommand(root, indexUseCase),
        )
        .completionOption()
        .main(args)
  }
}
