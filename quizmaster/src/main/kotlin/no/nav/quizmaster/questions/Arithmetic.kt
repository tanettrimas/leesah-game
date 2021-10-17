package no.nav.quizmaster.questions

import no.nav.quizmaster.Answer
import no.nav.quizmaster.Question
import java.time.Duration
import java.time.LocalDateTime.now
import kotlin.random.Random

class Arithmetic(private val frequency: Duration): QuestionFactory("arithmetic") {

    private val operators = listOf(Operator.ADD, Operator.MULTI, Operator.SUB, Operator.DIV)
    private var nextQuestion = now() + frequency
    private val publishedQuestions = mutableMapOf<String, Int>()

    override fun handle(answer: Answer) {
        try {
            publishedQuestions[answer.questionId]?.checkAnswer(answer)
        } catch (e: NumberFormatException ) {
            logger.warn("answer = $answer contains invalid data = ${answer.answer}")
            logger.debug(e.toString())
        }
    }

    private fun Int.checkAnswer(answer: Answer) {
        if(this == answer.answer.toInt()) publish(answer.teamName, answer.questionId, answer.messageId)
    }

    override fun questions(): List<Question> {
        return if (now() > nextQuestion) listOf(newQuestion())
        else emptyList()
    }

    private fun newQuestion(): Question {
        val (exp, fasit) = generateExpression()
        val newQuestion = Question(category = category, question = exp)
        storeQuestion(newQuestion, fasit)
        return newQuestion
    }

    private fun storeQuestion(newQuestion: Question, fasit: Int) {
        publishedQuestions[newQuestion.messageId] = fasit
    }

    private fun generateExpression(): Pair<String, Int> {
        val first = Random.nextInt(101)
        val last = Random.nextInt(101)
        val op = operators[Random.nextInt(operators.size)]
        val exp = "$first ${op.symbol} $last"
        val fasit = op.operation(first, last)
        return Pair(exp, fasit)
    }

    private enum class Operator(val symbol: String, val operation: (Int, Int) -> Int) {
        ADD("+", {first, last -> first + last}),
        MULTI("*", {first, last -> first * last}),
        SUB("-", {first, last -> first - last}),
        DIV("/", {first, last -> first / last}),
    }
}