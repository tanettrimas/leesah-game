package no.nav.quizmaster

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.util.*

enum class MessageType {
    QUESTION, ANSWER, ASSESSMENT
}

interface Message {
    fun id(): String
    fun type(): MessageType
    fun json(): String {
        return objectMapper.writeValueAsString(this)
    }
}

data class Question private constructor(
    val messageId: String = UUID.randomUUID().toString(),
    val type: MessageType,
    val category: String,
    val question: String
) : Message {
    override fun id() = messageId
    override fun type() = type

    constructor(
        messageId: String = UUID.randomUUID().toString(),
        category: String,
        question: String
    ) : this(messageId, MessageType.QUESTION, category, question)
}

data class Answer private constructor(
    val messageId: String = UUID.randomUUID().toString(),
    val type: MessageType,
    val category: String,
    val teamName: String,
    val answer: String
) : Message {
    override fun id() = messageId
    override fun type() = type

    constructor(
        messageId: String = UUID.randomUUID().toString(),
        category: String,
        teamName: String,
        answer: String
    ) : this(messageId, MessageType.ANSWER, category, teamName, answer)
}

data class Assessment private constructor(
    val messageId: String = UUID.randomUUID().toString(),
    val type: MessageType,
    val category: String,
    val teamName: String,
    val answerId: String,
    val sign: String,
) : Message {
    override fun id() = messageId
    override fun type() = type

    companion object {
        private fun sign() = ""
    }

    constructor(
        messageId: String = UUID.randomUUID().toString(),
        category: String,
        teamName: String,
        answerId: String,
    ) : this(messageId, MessageType.ASSESSMENT, category, teamName, answerId, sign())
}

val objectMapper: ObjectMapper
    get() = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .registerKotlinModule()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

inline fun <reified T> tryFromRaw(json: String, validate: (json: JsonNode) -> Boolean): T? {
    return if (validate(objectMapper.readTree(json))) objectMapper.readValue(json, object : TypeReference<T>() {})
    else null
}

internal fun JsonNode.containsValue(key: String, value: String) = this.findValue(key)?.textValue() == value
internal fun JsonNode.containsKey(key: String) = !this.findValue(key).isNull