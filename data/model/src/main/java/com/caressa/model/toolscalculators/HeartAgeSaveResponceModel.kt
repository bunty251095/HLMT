package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HeartAgeSaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(  Header(authTicket = authToken) ) {

    data class JSONDataRequest(
        @SerializedName("Questions")
        val questions: List<Question> = listOf(),
        @SerializedName("ParticipationID")
        val participationID: Int = 0,
        @SerializedName("SaveResponse")
        val saveResponse: Boolean = false,
        @SerializedName("CalculationModel")
        val calculationModel: String = ""
    )

    data class Question(
        @SerializedName("QuizID")
        val quizID: Int = 0,
        @SerializedName("TemplateID ")
        val templateID: Int = 0,
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Description")
        val description: Any? = Any(),
        @SerializedName("SectionID")
        val sectionID: Int = 0,
        @SerializedName("QuestionTypeCode")
        val questionTypeCode: Int = 0,
        @SerializedName("Answers")
        val answers: List<Answer> = listOf()
    )

    data class Answer(
        @SerializedName("QuestionCode")
        val questionCode: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Score")
        val score: Int = 0
    )

    data class HeartAgeSaveResponce(
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("RiskLabel")
        val riskLabel: String = "",
        @SerializedName("RiskScorePercentage")
        val riskScorePercentage: Double = 0.0,
        @SerializedName("HeartAge")
        val heartAge: Int = 0,
        @SerializedName("ParameterReport")
        val parameterReport: List<ParameterReport> = listOf(),
        @SerializedName("HeartAgeReport")
        val heartAgeReport: HeartAgeReport = HeartAgeReport(),
        @SerializedName("HeartRiskReport")
        val heartRiskReport: List<HeartRiskReport> = listOf()
    )

    data class HeartAgeReport(
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("Title")
        val title: String = ""
    )

    data class HeartRiskReport(
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("Title")
        val title: String = ""
    )

    data class ParameterReport(
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("Title")
        val title: String = ""
    )

}