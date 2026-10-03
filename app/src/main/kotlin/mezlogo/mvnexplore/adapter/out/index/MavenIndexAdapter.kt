package mezlogo.mvnexplore.adapter.out.index

import java.nio.file.Files
import java.nio.file.Path
import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import org.apache.maven.index.reader.ChunkReader

fun String.indexOfNth(char: Char, n: Int): Int {
  if (n <= 0) return -1
  var index = -1
  for (i in 1..n) {
    index = this.indexOf(char, index + 1)
    if (index == -1) break
  }
  return index
}

class MavenIndexAdapter : IndexUseCase {

  override fun createGroupAndArtifactId(
      indexConfig: IndexConfig,
      indexToCreate: Path,
  ): String {
    val indexFileGzip = indexConfig.pathToIndexGzip

    TODO("create output file with gzip AND best compression")

    Files.newInputStream(indexFileGzip).use { fis ->
      ChunkReader("nexus-maven-repository-index", fis).use { chunkReader ->
        /**
         * example of chunk: {d=This project contains libraries to interface with Zimbabwe's Leading
         * Payments Gateway Paynow, u=zw.co.paynow|java-sdk|1.1.2|javadoc|jar.asc.sha256,
         * i=jar.asc.sha256|1738155103000|64|2|2|0|jar.asc.sha256, m=1765378927627, n=java-sdk}
         */
        var previousGroupAndArtifactId = ""
        for (record in chunkReader) {
          /** example of u: "zone.src.sheaf|web-sheaf|0.1.7|sources|jar" */
          val unique = record["u"] ?: continue
          val groupAndArtifactId = extractGroupAndArtifactId(unique)

          /** This preverns any duplications. */
          if (groupAndArtifactId != previousGroupAndArtifactId) {
            previousGroupAndArtifactId = groupAndArtifactId
            TODO("write a new line separated string to output file")
          }
        }
      }
    }
  }

  private fun extractGroupAndArtifactId(u: String): String {
    val indexForSplit = u.indexOfNth('|', 2)
    return u.substring(0, indexForSplit)
  }
}
