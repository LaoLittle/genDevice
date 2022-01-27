package utils

import copy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import map

@Serializable(HexStringSerializer::class)
@JvmInline
value class HexString(
    val data: ByteArray
)

object HexStringSerializer : KSerializer<HexString> by String.serializer().map(
    String.serializer().descriptor.copy("utils.HexString"),
    deserialize = { HexString(it.hexToBytes()) },
    serialize = { it.data.toUHexString("").lowercase() }
)