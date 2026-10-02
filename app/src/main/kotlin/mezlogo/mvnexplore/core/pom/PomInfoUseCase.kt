
package mezlogo.mvnexplore.core.pom

import java.nio.file.Path

interface PomInfoUseCase {
    fun readPomInfo(pomPath: Path): PomInfo
}

    