package mezlogo.mvnexplore.adapter.out.index

import java.nio.file.Files
import java.nio.file.Path
import mezlogo.mvnexplore.port.out.index.IndexConfig
import mezlogo.mvnexplore.port.out.index.IndexUseCase
import org.apache.maven.index.reader.ChunkReader

fun indexOfNth(str: String, char: Char, n: Int): Int {
  if (n <= 0) return -1
  var index = -1
  for (i in 1..n) {
    index = str.indexOf(char, index + 1)
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
         * {d=This project contains libraries to interface with Zimbabwe's Leading Payments Gateway
         * Paynow, u=zw.co.paynow|java-sdk|1.1.2|javadoc|jar.asc.sha256,
         * i=jar.asc.sha256|1738155103000|64|2|2|0|jar.asc.sha256, m=1765378927627, n=java-sdk}
         */
        var previousGroupAndAritfactId = ""
        for (record in chunkReader) {
          // zone.src.sheaf|web-sheaf|0.1.7|sources|jar
          val unique = record["u"] ?: continue
          val indexForSplit = indexOfNth(unique, '|', 2)
          val groupAndArtifactId = unique.substring(0, indexForSplit)
          if (groupAndArtifactId != previousGroupAndAritfactId) {
            previousGroupAndAritfactId = groupAndArtifactId
            //            println(previousGroupAndAritfactId)

            TODO("write a new line separated string to output file")
          }
        }
      }
    }
  }
}
