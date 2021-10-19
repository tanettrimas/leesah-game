package no.nav.quizmaster

import com.fasterxml.jackson.core.JacksonException
import no.nav.quizmaster.questions.Arithmetic
import no.nav.quizmaster.questions.RegisterTeam
import no.nav.quizrapid.*
import org.slf4j.LoggerFactory
import java.time.Duration


class QuizMaster: QuizParticipant {
    private val questions = listOf(RegisterTeam(), Arithmetic(Duration.ofMinutes(1)))

    fun events(): List<String> {
        val outQuestions: List<String> = questions
            .fold(emptyList()) { list, question ->
                list + question.questions().json()
            }

        val outEvents: List<String> = questions
            .fold(emptyList()) { list, question ->
                list + question.events().json()
            }
        return outEvents + outQuestions
    }

    override fun handle(question: Question) = false

    override fun handle(answer: Answer): Boolean {
        questions.forEach { question -> question.handle(answer) }
        return true
    }

    override fun handle(assessment: Assessment) = false

    override fun messages(): List<Message> {
        val outQuestions: List<Message> = questions
            .fold(emptyList()) { list, question ->
                list + question.questions()
            }

        val outEvents: List<Message> = questions
            .fold(emptyList()) { list, question ->
                list + question.events()
            }
        return outEvents + outQuestions
    }

    fun categories(): List<String> = questions.map { it.category }
}

private fun Iterable<Message>.json() = map { it.json() }