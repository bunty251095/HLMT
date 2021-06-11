package com.caressa.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caressa.model.entity.AppVersion
import com.caressa.model.entity.UserRelatives
import com.caressa.model.entity.Users

@Dao
interface VivantUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Users)

    @Query("SELECT * FROM Users")
    fun getUsersData(): Users

    @Query("SELECT * FROM Users ")
    fun getUser(): Users

    @Query("UPDATE  Users SET PhoneNumber=:phone")
    fun updateUserMobileNumber( phone : String )

    @Query("UPDATE  Users SET firstName=:name , PhoneNumber=:phone")
    fun updateUserDetails( name : String ,phone : String )

    @Query("UPDATE  Users SET name=:name , path=:path")
    fun updateUserProfileImgPath( name : String ,path : String )

    @Query("DELETE FROM Users")
    fun deleteAllRecords()

    // *****************************  RelativesTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRelative( userRelatives : UserRelatives)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRelative( userRelatives : List<UserRelatives>)

    @Query("SELECT * FROM UserRelativesTable")
    fun geAllRelativeList() : List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelationshipCode != 'SELF'")
    fun getUserRelatives(): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelationshipCode =:relationShipCode")
    fun getUserRelativeSpecific( relationShipCode : String ): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelativeID =:relativeId")
    fun getUserRelativeForRelativeId( relativeId : String ): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelativeID =:relativeId")
    fun getUserRelativeDetailsByRelativeId( relativeId : String ): UserRelatives

    @Query("SELECT Relationship FROM UserRelativesTable WHERE RelativeID=:relativeId")
    fun getRelationShip(relativeId: String) : String

    @Query("DELETE FROM UserRelativesTable")
    fun deleteUserRelativesTable()

    //*****************************  RelativesTable  *****************************

    //**************************  AppUpdateDetailsTable  **************************

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppVersionDetails(appVersion: AppVersion)

    @Query("SELECT * FROM AppUpdateDetailsTable")
    fun getAppVersionDetails(): AppVersion

    //**************************  AppUpdateDetailsTable  **************************
}