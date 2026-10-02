
package mezlogo.mvnexplore.adapter.out.search

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mezlogo.mvnexplore.port.model.MavenArtifact
import mezlogo.mvnexplore.port.out.search.SearchArtifactsRequest
import mezlogo.mvnexplore.port.out.search.SearchIndexUseCase
import org.slf4j.LoggerFactory

/**
 * Uses the Maven Central Solr API documented in maven-central-search-api-openapi.yml.
 */
class SearchIndexAdapter(
    private val baseUrl: String = "https://search.maven.org/solrsearch",
    private val rows: Int = 200,
) : SearchIndexUseCase {
    private val log = LoggerFactory.getLogger(SearchIndexAdapter::class.java)
    private val httpClient = HttpClient.newHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override fun searchArtifacts(request: SearchArtifactsRequest): List<MavenArtifact> {
        log.trace("searchArtifacts called with request={}", request)
        val query = buildQuery(request)
        log.trace("Built query: {}", query)
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8)
        log.trace("Encoded query: {}", encodedQuery)
        val uri = URI.create("${baseUrl.trimEnd('/')}/select?q=$encodedQuery&rows=$rows&wt=json")
        log.trace("Created URI: {}", uri)
        val httpRequest = HttpRequest.newBuilder(uri).GET().build()
        log.trace("Sending HTTP request: {}", httpRequest)
        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        log.trace("Received HTTP response: status={}, body={}", response.statusCode(), response.body())
        if (response.statusCode() !in 200..299) {
            log.trace("HTTP request failed with status {}", response.statusCode())
            error("Search request failed with status ${response.statusCode()}: ${response.body()}")
        }
        val payload = json.decodeFromString<SolrSearchResponse>(response.body())
        log.trace("Decoded payload: {}", payload)
        val artifacts = payload.response.docs.mapNotNull { doc ->
            log.trace("Processing doc: {}", doc)
            val groupId = doc.g ?: return@mapNotNull null
            val artifactId = doc.a ?: return@mapNotNull null
            val version = doc.v ?: doc.latestVersion ?: return@mapNotNull null
            val artifact = MavenArtifact(groupId, artifactId, version)
            log.trace("Created MavenArtifact: {}", artifact)
            artifact
        }
        log.trace("Returning artifacts: {}", artifacts)
        return artifacts
    }

    private fun buildQuery(request: SearchArtifactsRequest): String {
        log.trace("buildQuery called with request={}", request)
        val parts = mutableListOf<String>()
        request.groupGlob?.takeIf { it.isNotBlank() }?.let {
            log.trace("Adding group glob: {}", it)
            parts += "g:${it.toSolrWildcard()}"
        }
        request.artifactGlob?.takeIf { it.isNotBlank() }?.let {
            log.trace("Adding artifact glob: {}", it)
            parts += "a:${it.toSolrWildcard()}"
        }
        val query = if (parts.isEmpty()) "*:*" else parts.joinToString(" AND ")
        log.trace("Built query: {}", query)
        return query
    }

    private fun String.toSolrWildcard(): String {
        log.trace("toSolrWildcard for string: {}", this)
        return this
    }
}

@Serializable
private data class SolrSearchResponse(
    val response: SolrResponseBody,
)

@Serializable
private data class SolrResponseBody(
    val docs: List<SolrArtifactDoc> = emptyList(),
)

@Serializable
private data class SolrArtifactDoc(
    val g: String? = null,
    val a: String? = null,
    val v: String? = null,
    val latestVersion: String? = null,
)

    