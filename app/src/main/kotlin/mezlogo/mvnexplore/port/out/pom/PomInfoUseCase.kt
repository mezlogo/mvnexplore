package mezlogo.mvnexplore.port.out.pom

import java.nio.file.Path

interface PomInfoUseCase {
  fun readPomInfo(pomPath: Path): PomInfo
}
