package com.altiran.velonet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tongfei.progressbar.ProgressBar
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.URI
import kotlin.system.measureTimeMillis

class Velonet {
    companion object {
        const val DOWNLOAD_URL = "https://github.com/BitDoctor/speed-test-file/raw/master/5mb.txt"
        const val ALTERNATE_DOWNLOAD_URL = "http://ipv4.download.thinkbroadband.com/10MB.zip"
        const val UPLOAD_URL = "https://httpbin.org/post"
        const val RETRY_LIMIT = 3
    }

    suspend fun start() {
        println("Starting Download Speed Test...")
        testDownloadSpeed()

        println("Starting Upload Speed Test...")
        testUploadSpeed()
    }

    private suspend fun testDownloadSpeed(): Double {
        return withContext(Dispatchers.IO) {
            var attempt = 0
            var success = false
            var downloadSpeed = 0.0

            while (attempt < RETRY_LIMIT && !success) {
                try {
                    val url = URI(if (attempt % 2 == 0) DOWNLOAD_URL else ALTERNATE_DOWNLOAD_URL).toURL()
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()

                    val fileSize = connection.contentLength
                    var downloadedSize = 0

                    val buffer = ByteArray(1024)
                    val inputStream = connection.inputStream

                    val progressBar = ProgressBar("Downloading", fileSize.toLong())
                    val timeTaken = measureTimeMillis {
                        progressBar.use { pb ->
                            while (inputStream.read(buffer).also { bytesRead ->
                                    if (bytesRead != -1) {
                                        downloadedSize += bytesRead
                                        pb.stepBy(bytesRead.toLong())
                                    }
                                } != -1) {
                            }
                        }
                    }

                    connection.disconnect()

                    downloadSpeed = (downloadedSize.toDouble() * 8 / (timeTaken / 1000.0) / (1024 * 1024))
                    println("Download Speed: $downloadSpeed Mbps")
                    success = true
                } catch (e: SocketException) {
                    println("Download failed, retrying... (${attempt + 1}/$RETRY_LIMIT)")
                    attempt++
                    if (attempt >= RETRY_LIMIT) {
                        println("Failed to complete download after $RETRY_LIMIT attempts.")
                    }
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}")
                    break
                }
            }
            downloadSpeed
        }
    }

    private suspend fun testUploadSpeed(): Double {
        return withContext(Dispatchers.IO) {
            var attempt = 0
            var success = false
            var uploadSpeed = 0.0

            while (attempt < RETRY_LIMIT && !success) {
                try {
                    val url = URI(UPLOAD_URL).toURL()
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doOutput = true
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                    val postData = "data=" + "a".repeat(1024 * 1024)
                    val totalSize = postData.length.toLong()

                    val progressBar = ProgressBar("Uploading", totalSize)
                    val timeTaken = measureTimeMillis {
                        progressBar.use { pb ->
                            connection.outputStream.use { outputStream ->
                                val chunkSize = 1024
                                var written = 0
                                while (written < postData.length) {
                                    val end = minOf(postData.length, written + chunkSize)
                                    outputStream.write(postData.substring(written, end).toByteArray())
                                    written = end
                                    pb.stepBy(chunkSize.toLong())
                                }
                            }
                        }
                    }

                    connection.disconnect()

                    uploadSpeed = (postData.length.toDouble() * 8 / (timeTaken / 1000.0) / (1024 * 1024))
                    println("Upload Speed: $uploadSpeed Mbps")
                    success = true
                } catch (e: SocketException) {
                    println("Upload failed, retrying... (${attempt + 1}/$RETRY_LIMIT)")
                    attempt++
                    if (attempt >= RETRY_LIMIT) {
                        println("Failed to complete upload after $RETRY_LIMIT attempts.")
                    }
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}")
                    break
                }
            }
            uploadSpeed
        }
    }
}
