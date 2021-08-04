package com.caressa.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caressa.model.entity.DocumentType
import com.caressa.model.entity.HealthDocument
import com.caressa.model.entity.RecordInSession
import com.caressa.model.entity.UserRelatives

@Dao
interface StoreRecordsDao {

    // *****************************  DocumentTypeTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocumentTypes(healthRecords: DocumentType)

    @Query("SELECT * FROM DocumentTypeTable ORDER BY Description ASC")
    fun getDocumentTypes(): List<DocumentType>

    @Query("DELETE FROM DocumentTypeTable")
    fun deleteDocumentTypesTable()
    // *****************************  DocumentTypeTable  *****************************

    // *****************************  HealthRecordsTable  *****************************

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun replaceHealthDocument(healthDocument: HealthDocument)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDocumentList(healthDocuments: List<HealthDocument>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDocument(healthDocument: HealthDocument)

    @Query("SELECT * FROM HealthDocumentTable WHERE Id=:id")
    fun getHealthDocumentById(id: Int): HealthDocument

    @Query("SELECT * FROM HealthDocumentTable ORDER BY RecordDate DESC")
    fun getHealthDocuments(): List<HealthDocument>

    @Query("SELECT * FROM HealthDocumentTable WHERE PersonId=:personId ORDER BY RecordDate DESC")
    fun getHealthDocumentsWherePersonId(personId: String): List<HealthDocument>

    @Query("SELECT * FROM HealthDocumentTable WHERE Code=:code and Sync!='YD' ORDER BY RecordDate DESC")
    fun getHealthDocumentsWhereCode(code: String): List<HealthDocument>

    @Query("SELECT * FROM HealthDocumentTable WHERE Code=:code or Code='DOC' or Code='PROFPIC' and Sync!='YD' ORDER BY RecordDate DESC")
    fun getHealthDocumentsWhereCodeWithOther(code: String): List<HealthDocument>

    @Query("SELECT * FROM HealthDocumentTable WHERE Sync=:sync ORDER BY RecordDate DESC")
    fun getHealthDocumentsWhere(sync: String): List<HealthDocument>

    @Query("UPDATE HealthDocumentTable SET Sync=:sync,RecordDate=:recordDate,PersonName=:name WHERE Id=:id")
    fun updateHealthDocument(id: String, sync: String, recordDate: String, name: String)

    @Query("UPDATE  HealthDocumentTable SET Path=:path , Sync=:sync WHERE Id=:id")
    fun updateHealthDocumentPathSync(id: String, path: String, sync: String)

    @Query("DELETE FROM HealthDocumentTable WHERE Id=:recordId")
    fun deleteHealthDocument(recordId: String)

    @Query("DELETE FROM HealthDocumentTable")
    fun deleteHealthDocumentTableTable()

    // *****************************  HealthRecordsTable  *****************************

    // *****************************  RecordInSession Table  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecordInSession(recordsInSession: RecordInSession)

    @Query("SELECT * FROM RecordsInSessionTable")
    fun getRecordsInSession(): List<RecordInSession>

    @Query("DELETE FROM RecordsInSessionTable WHERE Name=:name")
    fun deleteWhereRecordsInSessionTable(name: String)

    @Query("DELETE FROM RecordsInSessionTable WHERE Name=:name and Path=:path")
    fun deleteRecordInSession(name: String, path: String)

    @Query("DELETE FROM RecordsInSessionTable")
    fun deleteRecordsInSessionTable()
    // *****************************  RecordInSession Table  *****************************

    // *****************************  RelativesTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRelatives(userRelatives: UserRelatives)

    @Query("SELECT * FROM UserRelativesTable")
    fun getUserRelatives(): List<UserRelatives>

    @Query("SELECT Relationship FROM UserRelativesTable WHERE RelativeID=:relativeId")
    fun getRelationShip(relativeId: String): String

    @Query("SELECT FirstName FROM UserRelativesTable WHERE RelativeID=:relativeId")
    fun getPersonName(relativeId: String): String

    @Query("DELETE FROM UserRelativesTable")
    fun deleteUserRelativesTable()
    // *****************************  RelativesTable  *****************************
}