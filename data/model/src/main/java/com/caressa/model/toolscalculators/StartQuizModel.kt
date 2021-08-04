package com.caressa.model.toolscalculators

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StartQuizModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ParticipationDetails")
        val participationDetails: ParticipationDetails = ParticipationDetails(),
        @SerializedName("TemplatesCode")
        val templatesCode: String = ""
    )

    data class ParticipationDetails(
        @SerializedName("ID")
        val id: String = "",
        @SerializedName("Contact")
        val contact: Contact = Contact(),
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("PartnerCode")
        val partnerCode: String = Configuration.PartnerCode,
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("ClusterID")
        val clusterID: String = ""
    )

    data class Contact(
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )

    data class StartQuizResponse(
        @SerializedName("ParticipantID")
        val participantID: String = "",
        @SerializedName("SendMailer")
        val sendMailer: String = "",
        @SerializedName("Template")
        val template: Template = Template()
    )

    data class Template(
        @SerializedName("AssemblyName")
        val assemblyName: Any? = Any(),
        @SerializedName("AssociatedQuizID")
        val associatedQuizID: Int = 0,
        @SerializedName("ClassName")
        val className: Any? = Any(),
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("IsActive")
        val isActive: Any? = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("PartnerCode")
        val partnerCode: Any? = Any(),
        @SerializedName("Questions")
        val questions: List<Question> = listOf(),
        @SerializedName("QuizCode")
        val quizCode: String = "",
        @SerializedName("QuizType")
        val quizType: String = "",
        @SerializedName("Title")
        val title: String = "",
        @SerializedName("WelcomeNote")
        val welcomeNote: String = ""
    )

    data class Question(
        @SerializedName("AnswerExplanation")
        val answerExplanation: Any? = Any(),
        @SerializedName("Answers")
        val answers: List<Any> = listOf(),
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Description")
        val description: String = "",
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
        val questionTypeCode: String = "",
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