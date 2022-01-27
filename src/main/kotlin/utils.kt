import kotlinx.serialization.Serializable
import java.security.MessageDigest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random
import kotlin.random.nextInt

internal val devices = arrayOf("")

/**
 * Generate a random MAC address
 * @return MAC address string
 */
fun generateMac(): String {
    val random = Random
    val mac = arrayOf(
        String.format("%02x", random.nextInt(0xff)),
        String.format("%02x", random.nextInt(0xff)),
        String.format("%02x", random.nextInt(0xff)),
        String.format("%02x", random.nextInt(0xff)),
        String.format("%02x", random.nextInt(0xff)),
        String.format("%02x", random.nextInt(0xff))
    )
    return mac.joinToString(":")
}

@OptIn(ExperimentalContracts::class)
inline fun buildProcess(vararg command: String, builderAction: ProcessBuilder.() -> Unit): Process {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    val builder = ProcessBuilder(*command)
    builder.builderAction()
    return builder.start()
}

@Serializable
class Version(
    val incremental: ByteArray = "5891938".toByteArray(),
    val release: ByteArray = "10".toByteArray(),
    val codename: ByteArray = "REL".toByteArray(),
    val sdk: Int = 29
) {
    /**
     * @since 2.9
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Version

        if (!incremental.contentEquals(other.incremental)) return false
        if (!release.contentEquals(other.release)) return false
        if (!codename.contentEquals(other.codename)) return false
        if (sdk != other.sdk) return false

        return true
    }

    /**
     * @since 2.9
     */
    override fun hashCode(): Int {
        var result = incremental.contentHashCode()
        result = 31 * result + release.contentHashCode()
        result = 31 * result + codename.contentHashCode()
        result = 31 * result + sdk
        return result
    }
}

internal fun randomWifiSSID(): String {
    val model = listOf("Xiaomi", "TP-Link", "ASUS", "Tenda").random()
    val randomSSID = arrayOf(
        String.format("%04X", Random.nextInt(0xffff)) ,
        String.format("%04X", Random.nextInt(0xffff))
    )
    return "${model}_${randomSSID.joinToString("_")}"
}

fun getRandomByteArray(length: Int, random: Random = Random): ByteArray =
    ByteArray(length) { random.nextInt(0..255).toByte() }

@JvmOverloads
fun ByteArray.md5(offset: Int = 0, length: Int = size - offset): ByteArray {
    checkOffsetAndLength(offset, length)
    return MessageDigest.getInstance("MD5").apply { update(this@md5, offset, length) }.digest()
}

private fun ByteArray.checkOffsetAndLength(offset: Int, length: Int) {
    require(offset >= 0) { "offset shouldn't be negative: $offset" }
    require(length >= 0) { "length shouldn't be negative: $length" }
    require(offset + length <= this.size) { "offset ($offset) + length ($length) > array.size (${this.size})" }
}


private val intCharRanges: Array<CharRange> = arrayOf('0'..'9')
/**
 * 根据所给 [charRanges] 随机生成长度为 [length] 的 [String].
 */
fun getRandomString(length: Int, vararg charRanges: CharRange, random: Random = Random): String =
    CharArray(length) { charRanges[random.nextInt(0..charRanges.lastIndex)].random(random) }.concatToString()


fun getRandomIntString(length: Int, random: Random = Random): String =
    getRandomString(length, *intCharRanges, random = random)

/**
 * 计算 imei 校验位
 */
fun luhn(imei: String): Int {
    var odd = false
    val zero = '0'
    val sum = imei.sumOf { char ->
        odd = !odd
        if (odd) {
            char.code - zero.code
        } else {
            val s = (char.code - zero.code) * 2
            s % 10 + s / 10
        }
    }
    return (10 - sum % 10) % 10
}