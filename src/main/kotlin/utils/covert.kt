/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 *  此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 *  Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 *  https://github.com/mamoe/mirai/blob/master/LICENSE
 */
@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_API_USAGE", "unused")

@file:JvmMultifileClass
@file:JvmName("MiraiUtils")

package utils


/*
 * 类型转换 Utils.
 * 这些函数为内部函数, 可能会改变
 */

/**
 * 255 -> 00 FF
 */
fun Short.toByteArray(): ByteArray = with(toInt()) {
    byteArrayOf(
        (shr(8) and 0xFF).toByte(),
        (shr(0) and 0xFF).toByte()
    )
}

/**
 * 255 -> 00 00 00 FF
 */
fun Int.toByteArray(): ByteArray = byteArrayOf(
    ushr(24).toByte(),
    ushr(16).toByte(),
    ushr(8).toByte(),
    ushr(0).toByte()
)

/**
 * 255 -> 00 00 00 FF
 */
fun Long.toByteArray(): ByteArray = byteArrayOf(
    (ushr(56) and 0xFF).toByte(),
    (ushr(48) and 0xFF).toByte(),
    (ushr(40) and 0xFF).toByte(),
    (ushr(32) and 0xFF).toByte(),
    (ushr(24) and 0xFF).toByte(),
    (ushr(16) and 0xFF).toByte(),
    (ushr(8) and 0xFF).toByte(),
    (ushr(0) and 0xFF).toByte()
)

fun Int.toUHexString(separator: String = " "): String = this.toByteArray().toUHexString(separator)

/**
 * 255 -> 00 FF
 */
fun UShort.toByteArray(): ByteArray = with(toUInt()) {
    byteArrayOf(
        (shr(8) and 255u).toByte(),
        (shr(0) and 255u).toByte()
    )
}

fun Short.toUHexString(separator: String = " "): String = this.toUShort().toUHexString(separator)

fun UShort.toUHexString(separator: String = " "): String =
    this.toInt().shr(8).toUShort().toUByte().toUHexString() + separator + this.toUByte().toUHexString()

fun ULong.toUHexString(separator: String = " "): String =
    this.toLong().toUHexString(separator)

fun Long.toUHexString(separator: String = " "): String =
    this.ushr(32).toUInt().toUHexString(separator) + separator + this.toUInt().toUHexString(separator)

/**
 * 255 -> 00 FF
 */
fun UByte.toByteArray(): ByteArray = byteArrayOf((this and 255u).toByte())

fun UByte.toUHexString(): String = this.toByte().toUHexString()

/**
 * 255u -> 00 00 00 FF
 */
fun UInt.toByteArray(): ByteArray = byteArrayOf(
    (shr(24) and 255u).toByte(),
    (shr(16) and 255u).toByte(),
    (shr(8) and 255u).toByte(),
    (shr(0) and 255u).toByte()
)

/**
 * 转 [ByteArray] 后再转 hex
 */
fun UInt.toUHexString(separator: String = " "): String = this.toByteArray().toUHexString(separator)

/**
 * 转无符号十六进制表示, 并补充首位 `0`.
 * 转换结果示例: `FF`, `0E`
 */
fun Byte.toUHexString(): String = this.toUByte().fixToUHex()

/**
 * 转无符号十六进制表示, 并补充首位 `0`.
 */
fun Byte.fixToUHex(): String = this.toUByte().fixToUHex()

/**
 * 转无符号十六进制表示, 并补充首位 `0`.
 */
fun UByte.fixToUHex(): String =
    if (this.toInt() in 0..15) "0${this.toString(16).uppercase()}" else this.toString(16).uppercase()

/**
 * 将 [this] 前 4 个 [Byte] 的 bits 合并为一个 [Int]
 *
 * 详细解释:
 * 一个 [Byte] 有 8 bits
 * 一个 [Int] 有 32 bits
 * 本函数将 4 个 [Byte] 的 bits 连接得到 [Int]
 */
fun ByteArray.toUInt(): UInt =
    (this[0].toUInt().and(255u) shl 24) + (this[1].toUInt().and(255u) shl 16) + (this[2].toUInt()
        .and(255u) shl 8) + (this[3].toUInt().and(
        255u
    ) shl 0)

fun ByteArray.toUShort(): UShort =
    ((this[0].toUInt().and(255u) shl 8) + (this[1].toUInt().and(255u) shl 0)).toUShort()

fun ByteArray.toInt(): Int =
    (this[0].toInt().and(255) shl 24) + (this[1].toInt().and(255) shl 16) + (this[2].toInt()
        .and(255) shl 8) + (this[3].toInt().and(
        255
    ) shl 0)


///////////////////////////////////////////////////////////////////////////
// hexToBytes
///////////////////////////////////////////////////////////////////////////


private val byteStringCandidates = arrayOf('a'..'f', 'A'..'F', '0'..'9', ' '..' ')
private const val CHUNK_SPACE = -1

fun String.hexToBytes(): ByteArray {
    val array = ByteArray(countHexBytes())
    forEachHexChunkIndexed { index, char1, char2 ->
        array[index] = Byte.parseFromHexChunk(char1, char2)
    }
    return array
}

fun String.hexToUBytes(): UByteArray {
    val array = UByteArray(countHexBytes())
    forEachHexChunkIndexed { index, char1, char2 ->
        array[index] = Byte.parseFromHexChunk(char1, char2).toUByte()
    }
    return array
}

fun Byte.Companion.parseFromHexChunk(char1: Char, char2: Char): Byte {
    return (char1.digitToInt(16).shl(SIZE_BITS / 2) or char2.digitToInt(16)).toByte()
}

private inline fun String.forEachHexChunkIndexed(block: (index: Int, char1: Char, char2: Char) -> Unit) {
    var index = 0
    forEachHexChunk { char1: Char, char2: Char ->
        block(index++, char1, char2)
    }
}

private inline fun String.forEachHexChunk(block: (char1: Char, char2: Char) -> Unit) {
    var chunkSize = 0
    var char1: Char = 0.toChar()
    for ((index, c) in this.withIndex()) { // compiler optimization
        if (c == ' ') {
            if (chunkSize != 0) {
                throw IllegalArgumentException("Invalid size of chunk at index ${index.minus(1)}")
            }
            continue
        }
        if (c in 'a'..'f' || c in 'A'..'F' || c in '0'..'9') { // compiler optimization
            when (chunkSize) {
                0 -> {
                    chunkSize = 1
                    char1 = c
                }
                1 -> {
                    block(char1, c)
                    chunkSize = 0
                }
            }
        } else {
            throw IllegalArgumentException("Invalid char '$c' at index $index")
        }
    }
    if (chunkSize != 0) {
        throw IllegalArgumentException("Invalid size of chunk at end of string")
    }
}

fun String.countHexBytes(): Int {
    var chunkSize = 0
    var count = 0
    for ((index, c) in this.withIndex()) {
        if (c == ' ') {
            if (chunkSize != 0) {
                throw IllegalArgumentException("Invalid size of chunk at index ${index.minus(1)}")
            }
            continue
        }
        if (c in 'a'..'f' || c in 'A'..'F' || c in '0'..'9') {
            when (chunkSize) {
                0 -> {
                    chunkSize = 1
                }
                1 -> {
                    count++
                    chunkSize = 0
                }
            }
        } else {
            throw IllegalArgumentException("Invalid char '$c' at index $index")
        }
    }
    if (chunkSize != 0) {
        throw IllegalArgumentException("Invalid size of chunk at end of string")
    }
    return count
}

@OptIn(ExperimentalUnsignedTypes::class)
@JvmOverloads
@Suppress("DuplicatedCode") // false positive. foreach is not common to UByteArray and ByteArray
fun ByteArray.toUHexString(
    separator: String = " ",
    offset: Int = 0,
    length: Int = this.size - offset
): String {
    this.checkOffsetAndLength(offset, length)
    if (length == 0) {
        return ""
    }
    val lastIndex = offset + length
    return buildString(length * 2) {
        this@toUHexString.forEachIndexed { index, it ->
            if (index in offset until lastIndex) {
                var ret = it.toUByte().toString(16).uppercase()
                if (ret.length == 1) ret = "0$ret"
                append(ret)
                if (index < lastIndex - 1) append(separator)
            }
        }
    }
}

fun ByteArray.checkOffsetAndLength(offset: Int, length: Int) {
    require(offset >= 0) { "offset shouldn't be negative: $offset" }
    require(length >= 0) { "length shouldn't be negative: $length" }
    require(offset + length <= this.size) { "offset ($offset) + length ($length) > array.size (${this.size})" }
}