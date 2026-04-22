package es.udc.emergencyapp.net

import android.content.Context

object HttpClient {
    val defaultHosts = listOf(
        "http://10.0.2.2:8080",
        "http://10.0.3.2:8080",
    )


    private fun getFromHosts(
        path: String,
        jwt: String?,
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        for (host in defaultHosts) {
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

    /**
     * Convenience overload: read jwt from SharedPreferences if context provided and use default hosts.
     */
    fun getFromHosts(
        path: String,
        context: Context?,
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        val jwt = try {
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                ?.getString("jwt_token", null)
        } catch (e: Exception) {
            null
        }
        return getFromHosts(path, jwt, connectTimeout, readTimeout)
    }


    private fun postToHosts(
        path: String,
        jwt: String?,
        bodyPayload: String,
        contentType: String = "application/json",
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        for (host in defaultHosts) {
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


    fun postToHosts(
        path: String,
        context: Context?,
        bodyPayload: String,
        contentType: String = "application/json",
        connectTimeout: Int = 8000,
        readTimeout: Int = 8000
    ): Pair<String?, String?> {
        val jwt = try {
            context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                ?.getString("jwt_token", null)
        } catch (e: Exception) {
            null
        }
        return postToHosts(path, jwt, bodyPayload, contentType, connectTimeout, readTimeout)
    }
}
