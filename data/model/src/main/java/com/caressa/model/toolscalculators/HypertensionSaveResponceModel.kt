package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HypertensionSaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

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

    data class Answer(
        @SerializedName("QuestionCode")
        val questionCode: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Score")
        val score: Int = 0
    )

    data class HypertensionSaveResponce(
        @SerializedName("Result")
        val result: Result = Result()
    )

    data class Result(
        @SerializedName("Risk1")
        val risk1: Double = 0.0,
        @SerializedName("Risk2")
        val risk2: Double = 0.0,
        @SerializedName("Risk4")
        val risk4: Double = 0.0,
        @SerializedName("OptRisk1")
        val optRisk1: Double = 0.0,
        @SerializedName("OptRisk2")
        val optRisk2: Double = 0.0,
        @SerializedName("OptRisk4")
        val optRisk4: Double = 0.0,
        @SerializedName("Stage")
        val stage: Stage = Stage(),
        @SerializedName("Recommendation")
        val recommendation: Recommendation = Recommendation(),
        @SerializedName("SmokingReport")
        val smokingReport: SmokingReport = SmokingReport(),
        @SerializedName("BPReport")
        val bPReport: BPReport = BPReport(),
        @SerializedName("ParameterReport")
        val parameterReport: ParameterReport = ParameterReport(),
        @SerializedName("Status")
        val status: String = "",
        @SerializedName("Color")
        val color: String = "",
        @SerializedName("SystolicBp")
        val systolicBp: Int = 0,
        @SerializedName("DiastolicBp")
        val diastolicBp: Int = 0
    )

    data class Stage(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("SubTitle")
        val subTitle: String = "",
        @SerializedName("Description")
        val description: String = ""
    )

    data class Recommendation(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("SubTitle")
        val subTitle: String = "",
        @SerializedName("Description")
        val description: String = ""
    )

    data class SmokingReport(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("Description")
        val description: String = ""
    )

    data class BPReport(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("Description")
        val description: String = ""
    )

    data class ParameterReport(
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("Description")
        val description: String = ""
    )

}