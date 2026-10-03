package mezlogo.mvnindex.application.command

import com.github.ajalt.clikt.core.CliktCommand

class RootCommand : CliktCommand(name = "mvnindex") {
  override fun run() = Unit
}
