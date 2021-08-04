package com.caressa.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRAQuesTable", primaryKeys = ["AnswerCode"])
data class HRAQuestions(
    @field:SerializedName("QuestionCode")
    val QuestionCode: String = "",
    @field:SerializedName("AnswerCode")
    val AnswerCode: String = "",
    @field:SerializedName("AnsDescription")
    val AnsDescription: String = "",
    @field:SerializedName("Category")
    val Category: String = "",
    @field:SerializedName("TabName")
    val TabName: String = "",
    @field:SerializedName("OthersVal")
    val OthersVal: String = "",
    @field:SerializedName("Code")
    val Code: String = "",
    @field:SerializedName("IsSelected")
    val IsSelected: String = ""
)