package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmartPhoneSaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(  Header(authTicket = authToken) ) {

    data class JSONDataRequest(
        @SerializedName("ParticipationID")
        val participationID: String = "",
        @SerializedName("Questions")
        val questions: List<Question> = listOf()
    )

    data class Question(
        @SerializedName("QuizID")
        val quizID: String = "",
        @SerializedName("TemplateID")
        val templateID: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Description")
        val description: String = "",
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
        val score: String = ""
    )

    data class SmartPhoneSaveResponce(
        @SerializedName("Question")
        val question: RespQuestion = RespQuestion()
    )

    data class RespQuestion(
        @SerializedName("AnswerExplanation")
        val answerExplanation: String = "",
        @SerializedName("Answers")
        val answers: List<Any> = listOf(),
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Description")
        val description: Any? = Any(),
        @SerializedName("DisplayOrder")
        val displayOrder: Int = 0,
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("IsCheck")
        val isCheck: Any? = Any(),
        @SerializedName("IsDisable")
        val isDisable: Any? = Any(),
        @SerializedName("IsEnable")
        val isEnable: Any? = Any(),
        @SerializedName("IsHide")
        val isHide: Any? = Any(),
        @SerializedName("IsShow")
        val isShow: Any? = Any(),
        @SerializedName("IsUncheck")
        val isUncheck: Any? = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("ParticipantID")
        val participantID: Int = 0,
        @SerializedName("QuestionTypeCode")
        val questionTypeCode: Any? = Any(),
        @SerializedName("QuizID")
        val quizID: Int = 0,
        @SerializedName("Score")
        val score: Double = 0.0,
        @SerializedName("SectionID")
        val sectionID: Any? = Any(),
        @SerializedName("TempletID")
        val templetID: Int = 0
    )

}