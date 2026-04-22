package es.udc.emergencyapp.net

object HttpClient {
    // Default hosts tried by other screens; keep in one place
    val defaultHosts = listOf(
        "http://localhost:8080",
        "http://10.0.2.2:8080",
        "http://10.0.2.2:8000",
        "http://10.0.3.2:8000",
        "http://127.0.0.1:8000",
        "http://192.168.1.100:8000"
    )


    fun getFromHosts(
        path: String,
        jwt: String?,
        hosts: List<String>? = null,
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        val hostsToTry = hosts ?: defaultHosts
        for (host in hostsToTry) {
            try {
                val full = if (path.startsWith("/")) "$host$path" else "$host/$path"
                val url = java.net.URL(full)
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "GET"
                    this.connectTimeout = connectTimeout
                    this.readTimeout = readTimeout
                    if (!jwt.isNullOrEmpty()) setRequestProperty("Authorization", "Bearer $jwt")
                }
                val code = conn.responseCode
                val body = if (code in 200..299) conn.inputStream.bufferedReader()
                    .use { it.readText() } else null
                conn.disconnect()
                if (body != null) return Pair(body, host)
            } catch (e: Exception) {
                android.util.Log.w("HttpClient", "Failed GET $host$path", e)
            }
        }
        return Pair(null, null)
    }


    fun postToHosts(
        path: String,
        jwt: String?,
        bodyPayload: String,
        hosts: List<String>? = null,
        contentType: String = "application/json",
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        val hostsToTry = hosts ?: defaultHosts
        for (host in hostsToTry) {
            try {
                val full = if (path.startsWith("/")) "$host$path" else "$host/$path"
                val url = java.net.URL(full)
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    this.connectTimeout = connectTimeout
                    this.readTimeout = readTimeout
                    setRequestProperty("Content-Type", contentType)
                    if (!jwt.isNullOrEmpty()) setRequestProperty("Authorization", "Bearer $jwt")
                }
                conn.outputStream.bufferedWriter().use { it.write(bodyPayload) }
                val code = conn.responseCode
                val resp = if (code in 200..299) conn.inputStream.bufferedReader()
                    .use { it.readText() } else null
                conn.disconnect()
                if (resp != null) return Pair(resp, host)
            } catch (e: Exception) {
                android.util.Log.w("HttpClient", "Failed POST $host$path", e)
            }
        }
        return Pair(null, null)
    }
}
