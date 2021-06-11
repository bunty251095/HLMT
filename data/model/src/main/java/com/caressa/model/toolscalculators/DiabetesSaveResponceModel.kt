package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiabetesSaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(  Header(authTicket = authToken) ) {

    data class JSONDataRequest(
        @SerializedName("Questions")
        val questions: List<Question> = listOf(),
        @SerializedName("ParticipationID")
        val participationID: Int = 0
    )

    data class Answer(
        @SerializedName("QuestionCode")
        val questionCode: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Score")
        val score: Int = 0
    )

    data class Question(
        @SerializedName("QuizID")
        val quizID: Int = 0,
        @SerializedName("TemplateID")
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

    data class DiabetesSaveResponce(
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("TotalScore")
        val totalScore: Double = 0.0,
        @SerializedName("RiskLabel")
        val riskLabel: String = "",
        @SerializedName("ProbabilityPercentage")
        val probabilityPercentage: Double = 0.0,
        @SerializedName("Observations")
        val observations: Observations = Observations(),
        @SerializedName("DetailedReport")
        val detailedReport: List<DetailedReport> = listOf(),
        @SerializedName("CommonInstructions")
        val commonInstructions: List<String> = listOf()
    )

    data class DetailedReport(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("SubSection")
        val subSection: List<SubSection> = listOf()
    )

    data class SubSection(
        @SerializedName("Type")
        val type: String = "",
        @SerializedName("Text")
        val text: String = "",
        @SerializedName("List")
        val list: List<String> = listOf()
    )

    data class Observations(
        @SerializedName("GoodIn")
        val goodIn: String = "",
        @SerializedName("NeedImprovements")
        val needImprovements: String = "",
        @SerializedName("NonModifiableRisk")
        val nonModifiableRisk: String = ""
    )

}