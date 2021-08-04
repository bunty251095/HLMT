package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StressAndAnxietySaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ParticipationID")
        val participationID: Int = 0,
        @SerializedName("Questions")
        val questions: List<Question> = listOf()
    )

    data class Question(
        @SerializedName("Answers")
        val answers: List<Answer> = listOf(),
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Description")
        val description: Any? = Any(),
        @SerializedName("QuestionTypeCode")
        val questionTypeCode: Int = 0,
        @SerializedName("QuizID")
        val quizID: Int = 0,
        @SerializedName("SectionID")
        val sectionID: Int = 0,
        @SerializedName("TemplateID")
        val templateID: Int = 0
    )

    data class Answer(
        @SerializedName("QuestionCode")
        val questionCode: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Score")
        val score: Int = 0
    )

    data class StressAndAnxietySaveResponse(
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("Stage")
        val stage: Int = 0,
        @SerializedName("Depression")
        val depression: Depression = Depression(),
        @SerializedName("Anxiety")
        val anxiety: Anxiety = Anxiety(),
        @SerializedName("Stress")
        val stress: Stress = Stress(),
        @SerializedName("ParameterReport")
        val parameterReport: List<ParameterReport> = listOf()
    )

    data class Depression(
        @SerializedName("RiskLabel")
        val riskLabel: String = "",
        @SerializedName("Scale")
        val scale: Scale = Scale(),
        @SerializedName("Score")
        val score: Int = 0,
        @SerializedName("Stage")
        val stage: Int = 0
    )

    data class Anxiety(
        @SerializedName("RiskLabel")
        val riskLabel: String = "",
        @SerializedName("Scale")
        val scale: Scale = Scale(),
        @SerializedName("Score")
        val score: Int = 0,
        @SerializedName("Stage")
        val stage: Int = 0
    )

    data class Stress(
        @SerializedName("RiskLabel")
        val riskLabel: String = "",
        @SerializedName("Scale")
        val scale: Scale = Scale(),
        @SerializedName("Score")
        val score: Int = 0,
        @SerializedName("Stage")
        val stage: Int = 0
    )

    data class ParameterReport(
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("Title")
        val title: String = ""
    )

    data class Scale(
        @SerializedName("Normal")
        val normal: String = "",
        @SerializedName("Mild")
        val mild: String = "",
        @SerializedName("Moderate")
        val moderate: String = "",
        @SerializedName("Severe")
        val severe: String = "",
        @SerializedName("Extremely Severe")
        val extremelySevere: String = ""
    )

}