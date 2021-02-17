package de.unibi.citec.clf.bonsai.ros.actuators

import org.ros.namespace.GraphName
import de.unibi.citec.clf.bonsai.ros.RosNode
import de.unibi.citec.clf.bonsai.actuators.URIActuator
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException
import org.ros.node.ConnectedNode
import java.io.UnsupportedEncodingException
import java.lang.InterruptedException
import java.lang.StringBuilder
import java.net.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.*

/**
 * @author lruegeme
 */
class HttpClientActuator(private val nodeName: GraphName) : RosNode(),
    URIActuator {

    private val logger = org.apache.log4j.Logger.getLogger(javaClass)

    init {
        initialized = false
    }

    @Throws(ConfigurationException::class)
    override fun configure(conf: IObjectConfigurator) {
    }

    override fun onStart(connectedNode: ConnectedNode) {
        initialized = true
    }

    override fun destroyNode() {}
    override fun getDefaultNodeName(): GraphName {
        return nodeName
    }

    override fun getRequest(uri: URI, query: Map<String, String>): Future<String>? {
        val client = HttpClient.newHttpClient()
        val full = URI(uri.toString() + getQueryString(query))
        var request = HttpRequest.newBuilder()
            .uri(full)
            .build()
        logger.info("Sending request: $request")

        val a = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        return object : Future<String> {
            override fun cancel(b: Boolean): Boolean {
                return a.cancel(b)
            }

            override fun isCancelled(): Boolean {
                return a.isCancelled
            }

            override fun isDone(): Boolean {
                return a.isDone
            }

            @Throws(InterruptedException::class, ExecutionException::class)
            override fun get(): String {
                return a.get().body();
            }

            @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
            override fun get(l: Long, timeUnit: TimeUnit): String {
                return a[l, timeUnit].body()
            }
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getQueryString(params: Map<String, String>): String {
        val result = StringBuilder()
        var first = true
        for ((key, value) in params) {
            if (first) {
                result.append("?")
                first = false;
            } else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
        }
        return result.toString()
    }


}