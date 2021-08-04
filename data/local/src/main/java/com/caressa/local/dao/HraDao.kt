package com.caressa.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caressa.model.entity.HRALabDetails
import com.caressa.model.entity.HRAQuestions
import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.HRAVitalDetails

@Dao
interface HRADao {

    // *****************************  HRAQuesTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHraQuesResponse(hraQuestions: HRAQuestions)

    @Query("SELECT * FROM HRAQuesTable where QuestionCode = :questionCode")
    fun getHRASaveDetailsWhereQuestionCode(questionCode: String): List<HRAQuestions>

    @Query("SELECT * FROM HRAQuesTable where Code = :code")
    fun getHRASaveDetailsWhereCode(code: String): List<HRAQuestions>

    @Query("SELECT * FROM HRAQuesTable Where TabName = :tabName ORDER BY Category DESC")
    fun getHRASaveDetailsTabName(tabName: String): List<HRAQuestions>

    @Query("SELECT AnswerCode FROM HRAQuesTable where QuestionCode = :questionCode")
    fun getHraSaveAnswerCode(questionCode: String): String

    @Query("DELETE FROM HRAQuesTable WHERE QuestionCode=:questionCode")
    fun deleteHRASaveWhereQuestionCode(questionCode: String)

    @Query("DELETE FROM HRAQuesTable")
    fun deleteHraQuesTable()
    // *****************************  HRAQuesTable  *****************************


    // *****************************  HRASaveVitalTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHraVitalDetails(hraVitalDetails: HRAVitalDetails)

    @Query("SELECT * FROM HRASaveVitalTable")
    fun getHraSaveVitalDetails(): List<HRAVitalDetails>

    @Query("DELETE FROM HRASaveVitalTable")
    fun deleteHraVitalDetailsTable()

    @Query("DELETE FROM HRASaveVitalTable WHERE VitalsKey =:vitalKey")
    fun deleteHraVitalDetailsWhereKey(vitalKey: String)

    @Query("DELETE  FROM HRASaveVitalTable WHERE VitalsKey IN ( :Height,:Weight,:BMI) ")
    fun deleteHraVitalBmiDetails(Height: String, Weight: String, BMI: String)
    // *****************************  HRASaveVitalTable  *****************************


    // *****************************  HRASaveLabTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHraLabDetails(hraLabDetails: HRALabDetails)

    @Query("SELECT * FROM HRASaveLabTable")
    fun getHraSaveLabDetails(): List<HRALabDetails>

    @Query("SELECT LabValue  FROM HRASaveLabTable WHERE ParameterCode =:parameterCode")
    fun getHRASaveLabValue(parameterCode: String): String

    @Query("DELETE FROM HRASaveLabTable WHERE ParameterCode =:parameterCode")
    fun deleteHraLabDetailsWhereParameterCode(parameterCode: String)

    @Query("DELETE FROM HRASaveLabTable")
    fun deleteHraLabDetailsTable()
    // *****************************  HRASaveLabTable  *****************************


    // ***************************** HRASummaryTable *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHRASummaryRecord(hraSummary: HRASummary)

    @Query("SELECT * FROM HRASummaryTable")
    fun getHRASummary(): HRASummary

    @Query("SELECT * FROM HRASummaryTable WHERE PersonID = :personID")
    fun getHRASummaryRecord(personID: String): List<HRASummary>

    @Query("DELETE FROM HRASummaryTable")
    fun deleteHRASummaryTable()
    // ***************************** HRASummaryTable *****************************

}