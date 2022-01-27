import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import utils.toUHexString
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun main(vararg args: String) {
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
    println("Fuck Mirai Device!")

    var deviceFile = File("device.json")
    var deviceInfo = arrayOf("mi11p", "mipad5").random()

    if (args.isNotEmpty()) {
        args.apply {
            when {
                contains("--help") -> {
                    println(
                        """
                --file 指定输出文件名
                --device 生成指定设备的device.json
                --useadb 使用adb命令读取当前手机信息并生成随机信息
            """.trimIndent()
                    )
                    return
                }
                else -> {
                    if (contains("--file")) {
                        val arg = indexOf("--file") + 1
                        deviceFile = File(args[arg])
                    }
                    if (contains("--device")) {
                        val arg = indexOf("--device") + 1
                        deviceInfo = args[arg]
                    }
                    if (contains("--useadb")) {
                        val results = arrayListOf<String>()
                        arrayOf(
                            "adb.exe shell getprop ro.build.id",
                            "adb.exe shell getprop ro.build.product",
                            "adb.exe shell getprop ro.product.vendor.device",
                            "adb.exe shell getprop ro.product.board",
                            "adb.exe shell getprop ro.product.brand",
                            "adb.exe shell getprop ro.product.model",
                            "adb.exe shell getprop ro.vendor.build.fingerprint",
                            "adb.exe shell cat /proc/version",
                            "adb.exe shell getprop ro.baseband"
                        ).forEach {
                            Runtime.getRuntime().exec(it).inputStream.use { input ->
                                results.add(input.bufferedReader().readLine())
                            }
                        }
                        if (results[2].isBlank()) results[2] =
                            Runtime.getRuntime()
                                .exec("adb.exe shell getprop ro.product.vendor.device").inputStream.use {
                                it.bufferedReader()
                                    .readLine()
                            }
                        println(results)
                        val data = buildJsonObject {
                            put("display", results[0])
                            put("product", results[1])
                            put("device", results[2])
                            put("board", results[3])
                            put("brand", results[4])
                            put("model", results[5])
                            put("bootloader", "unknown")
                            put("fingerprint", results[6])
                            put("bootId", UUID.randomUUID().toString().uppercase())
                            put("procVersion", results[7])
                            put("baseBand", results[8].toByteArray().toUHexString("").lowercase())
                            put("version", buildJsonObject {
                                val version = arrayListOf<String>()
                                arrayOf(
                                    "adb.exe shell getprop ro.product.build.version.incremental",
                                    "adb.exe shell getprop ro.product.build.version.release",
                                    "adb.exe shell getprop ro.build.version.codename"
                                ).forEach {
                                    Runtime.getRuntime().exec(it).inputStream.use { input ->
                                        version.add(input.bufferedReader().readLine())
                                    }
                                }
                                put("incremental", version[0])
                                put("release", version[1])
                                put("codename", version[2])
                            })
                            put("simInfo", "T-Mobile")
                            put("osType", "android")
                            put("macAddress", generateMac())
                            put("wifiBSSID", generateMac())
                            put("wifiSSID", randomWifiSSID())
                            put("imsiMd5", getRandomByteArray(16).md5().toUHexString("").lowercase())
                            put("imei", getRandomIntString(14).run { this + luhn(this) })
                            put("apn", "wifi")
                        }
                        buildJsonObject {
                            put("deviceInfoVersion", 2)
                            put("data", data)
                        }.apply {
                            val fos = FileOutputStream(deviceFile)
                            fos.write(toString().toByteArray())
                        }
                        return
                    }
                }
            }
        }
    }


    val data = buildJsonObject {
        put("display", "")
        put("product", "")
        put("device", "")
        put("board", "")
        put("brand", "")
        put("model", "")
        put("bootloader", "unknown")
    }

    buildJsonObject {
        put("deviceInfoVersion", 2)
        put("data", data)
    }.apply {
        val fos = FileOutputStream(deviceFile)
        fos.write(toString().toByteArray())
    }
}