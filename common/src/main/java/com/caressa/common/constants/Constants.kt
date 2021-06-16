package com.caressa.common.constants

object Constants {

    //for PRODUCTION
/*    const val environment: String = "PROD"
    const val strAPIUrl: String = "https://core.vivant.me/"
    const val strHANDLERUrl: String = "Handler/RegistrationHandler.ashx?R="
    const val strProxyUrl = "Proxy/"
    const val strProxyRegistrationUrl: String = "Proxy/Registration/Registration?"
    const val strProxyLoginUrl: String = "Proxy/Registration/Login?"
    const val strProxyValidationUrl: String = "Proxy/Registration/Validation?"
    //const val strBlogsURL ="https://blogs.vivant.me/wp-json/wp/v2/posts?per_page="
    const val strBlogsBaseURL = "https://blogs.vivant.me/"
    const val strBlogsProxyURL = "wp-json/wp/v2/posts?per_page="
    const val strBookAppointmentURL = "https://booking.vivant.me/bookappointment/mvivant.html"
    const val strToolsAndCalculatorURL = "https://portal.vivant.me/ToolsAndCalculators"*/

    //for UAT
    /*const val environment: String = "UAT"
    const val strAPIUrl: String = "https://coreuat.vivant.me/"
    const val strHANDLERUrl: String = "DocumentProxy/Handler/RegistrationHandler.ashx?R="
    const val strProxyUrl = "DocumentProxy/"
    const val strProxyRegistrationUrl: String = "DocumentProxy/Registration/Registration?"
    const val strProxyLoginUrl: String = "DocumentProxy/Registration/Login?"
    const val strProxyValidationUrl: String = "DocumentProxy/Registration/Validation?"
    //const val strBlogsURL = "https://blogs.vivant.me/wp-json/wp/v2/posts?per_page="
    const val strBlogsBaseURL = "https://blogs.vivant.me/"
    const val strBlogsProxyURL = "wp-json/wp/v2/posts?per_page="
    const val strBookAppointmentURL = "https://labkhojuat.vivant.me/bookappointment/mvivant.html"
    const val strToolsAndCalculatorURL = "https://newphr.vivant.me/ToolsAndCalculators"*/

    //for HLMT UAT
    const val environment: String = "UAT"
    const val strAPIUrl: String = "https://hlmtcoreuat.vivant.me/"
    const val strHANDLERUrl: String = "DocumentProxy/Handler/RegistrationHandler.ashx?R="
    const val strProxyUrl = "DocumentProxy/"
    const val strProxyRegistrationUrl: String = "DocumentProxy/Registration/Registration?"
    const val strProxyLoginUrl: String = "PHR/api/Person/RegistrationAndLogin"
    const val strProxyValidationUrl: String = "DocumentProxy/Registration/Validation?"
    //const val strBlogsURL = "https://blogs.vivant.me/wp-json/wp/v2/posts?per_page="
    const val strBlogsBaseURL = "https://blogs.vivant.me/"
    const val strBlogsProxyURL = "wp-json/wp/v2/posts?per_page="
    const val strBookAppointmentURL = "https://labkhojuat.vivant.me/bookappointment/mvivant.html"
    const val strToolsAndCalculatorURL = "https://newphr.vivant.me/ToolsAndCalculators"

    const val SAVE_CLOUD_MESSAGING_ID_API = "Security/api/Account/SaveCloudMessagingID"
    const val CHECK_APP_UPDATE_API = "PHR/api/App/GetVersion"
    const val SAVE_FEEDBACK_API = "PHR/api/App/SaveFeedback"
    const val PASSWORD_CHANGE_API = "Security/api/Security/ChangePassword"
    const val CONTACT_US_API = "messaging/api/message/ContactUs"

    //Dashboard
    const val strWebChatDoctorURL: String = "https://tawk.to/chat/5c4181c951410568a1073185/default/?\$_tawk_popout=true"
    const val sirWebChatDietitianURL = "https://tawk.to/chat/5c418189ab5284048d0d7c0b/1d4mv5the/?\$_tawk_popout=true"
    //const val CHAT_URL: String = "https://tawk.to/chat/5b49c72b6d961556373dbb8b/default/?\$_tawk_popout=true"

    const val MAIN_DATABASE_NAME = Configuration.strAppIdentifier + ".db"
    const val DATABASE_NAME = "AIH_2.0"
    const val BASE_DB_NAME = "AIH_2.0"

    const val strEnableFamilyProfile: Boolean = true
    const val strApolloHtml = "file:///android_asset/apollo/apollo_SOP.html"

    //User APIs
    const val GET_USER_DETAIL_API = "PHR/api/Person/Get"
    const val UPDATE_PERSONAL_DETAIL_API = "PHR/api/Person/Update"
    //const val SAVE_PROFILE_IMAGE_API = "PHR/api/Person/SaveProfileImage"
    const val UPLOAD_PROFILE_IMAGE_API = "Proxy/Document/UploadProfileImage"
    const val REMOVE_PROFILE_IMAGE_API = "PHR/api/Person/DeleteProfileImage"

    //Fitness Tracker APIs
    const val FITNESS_GET_LATEST_GOAL_API = "PHR/api/Steps/GetLatestGoal"
    const val FITNESS_LIST_HISTORY_API = "PHR/api/Steps/ListHistory"
    const val FITNESS_SET_GOAL_API = "PHR/api/Steps/SetGoal"
    const val FITNESS_STEP_SAVE_LIST_API = "PHR/api/Steps/SaveList"
    const val FITNESS_STEP_SAVE_API = "PHR/api/Steps/Save"

    //Family Member APIs
    const val RELATIVES_LIST_API = "PHR/api/Person/ListRelatives"
    const val ADD_RELATIVE_API = "PHR/api/Person/Add"
    const val UPDATE_RELATIVE_API = "PHR/api/Person/Update"
    const val REMOVE_RELATIVE_API = "PHR/api/Person/RemoveRelative"

    //Family Doctors APIs
    const val DOCTORS_LIST_API = "PHR/api/FamilyDoctor/List"
    const val DOCTORS_LIST_SPECIALITY_API = "PHR/api/FamilyDoctor/Listspeciality"
    const val ADD_DOCTOR_API = "PHR/api/FamilyDoctor/Add"
    const val UPDATE_DOCTOR_API = "PHR/api/FamilyDoctor/Update"
    const val REMOVE_DOCTOR_API = "PHR/api/FamilyDoctor/Delete"

    //HRA APIs
    const val HRAurl = "Document/DownloadBookReport?"
    const val HRA_START_API = "PHR/api/PrimaryHRA/Start"
    const val BMI_EXIST_API = "PHR/api/BMI/GetCurrent"
    const val BP_EXIST_API = "PHR/api/BloodPressure/GetCurrent"
    const val LAB_RECORDS_API = "PHR/api/LabRecord/ListCurrent"
    const val SAVE_AND_SUBMIT_HRA_API = "PHR/api/PrimaryHRA/SaveAndSubmitHRA"
    const val MEDICAL_PROFILE_SUMMARY_API = "phr/api/Report/GetMedicalProfileSummary"
    const val GET_HRA_SUMMARY_API = "PHR/api/PrimaryHRA/GetLastHRAHistory"
    const val ASSESSMENT_SUMMARY_API = "PHR/api/MedicalProfile/GetAssessmentSummary"
    const val LIST_RECOMMENDED_TESTS_API = "PHR/api/MedicalProfile/ListRecommendedTests"

    //HealthRecords APIs
    const val SHR_DOCUMENT_TYPE_API = "PHR/api/Document/ListDocumentTypes"
    const val SHR_DOCUMENT_LIST_API = "PHR/api/Document/List"
    const val SHR_DOCUMENT_SAVE_API = "PHR/api/Document/Save "
    const val SHR_DOCUMENT_DELETE_API = "PHR/api/Document/Delete"
    const val SHR_DOCUMENT_DOWNLOAD_API = "PHR/api/Document/GetByHealthDocumentID"
    const val SHR_OCR_EXTRACT_DOCUMENT = "OCR/OCR/ExtractDocument"
    const val SHR_OCR_DOCUMENT_SAVE = "PHR/api/LabRecord/Save"
    const val SHR_OCR_UNIT_EXIST = "PHR/api/LabRecord/CheckIfUnitExist"

    // Medication Tracker New API's
    const val MEDICATION_GET_API = "PHR/api/Medication/Get/"
    const val MEDICATION_DRUGS_LIST_API = "PHR/api/Drug/List/"
    const val MEDICATION_LIST_BY_DATE_API = "PHR/api/Medication/ListByDate/"
    const val MEDICATION_LIST_BY_DAY_API = "PHR/api/Medication/ListByDay"
    const val MEDICATION_SAVE_API = "PHR/api/Medication/Save"
    const val MEDICATION_UPDATE_API = "PHR/api/Medication/UpdateMedication"
    const val MEDICATION_SET_ALERT_API = "PHR/api/Medication/SetAlert"
    const val MEDICATION_ADD_IN_TAKE_API = "PHR/api/Medication/AddInTake"
    const val MEDICATION_DELETE_TAKE_API = "PHR/api/Medication/Delete/"
    const val MEDICATION_GET_MEDICINE_IN_TAKE_API = "PHR/api/Medication/ListMedicationInTakeByScheduleID/"

    //ToolsTrackers APIs
    const val TOOLS_START_QUIZ_API = "Quiz/api/Quiz/StartQuiz"
    const val TOOLS_HEART_AGE_API = "Quiz/api/Quiz/GetHeartAge"
    const val TOOLS_DIABETES_API = "Quiz/api/Quiz/GetDiabeticRisk"
    const val TOOLS_HYPERTENSION_API = "Quiz/api/Quiz/GetHypertensionRisk"
    const val TOOLS_STRESS_ANXIETY_API = "Quiz/api/Quiz/GetStressAndAnxietyRisk"
    const val TOOLS_SMART_PHONE_ADDICTION_API = "Quiz/api/Quiz/SaveResponse"

    // Application Folder Name
    const val APPLICATION_SUBFOLDER_PROFILE_IMAGES = "Profile Images"
    const val APPLICATION_SUBFOLDER_RECORDS = "Records"

    //Template Constants
    const val PRIMARY_COLOR = "primaryColor"
    const val TEXT_COLOR = "textColor"
    const val ICON_TINT_COLOR = "iconTintColor"
    const val LEFT_BUTTON_COLOR = "leftButtonColor"
    const val RIGHT_BUTTON_COLOR = "rightButtonColor"
    const val LEFT_BUTTON_TEXT_COLOR = "leftButtonTextColor"
    const val RIGHT_BUTTON_TEXT_COLOR = "rightButtonTextColor"
    const val SELECTION_COLOR = "selectionColor"
    const val DESELECTION_COLOR = "deselectionColor"

    //RELATIONSHIP CODE
    const val SELF_RELATIONSHIP_CODE = "SELF"
    const val FATHER_RELATIONSHIP_CODE = "FAT"
    const val MOTHER_RELATIONSHIP_CODE = "MOT"
    const val SON_RELATIONSHIP_CODE = "SON"
    const val DAUGHTER_RELATIONSHIP_CODE = "DAU"
    const val BROTHER_RELATIONSHIP_CODE = "BRO"
    const val SISTER_RELATIONSHIP_CODE = "SIS"
    const val GRANDFATHER_RELATIONSHIP_CODE = "GRF"
    const val GRANDMOTHER_RELATIONSHIP_CODE = "GRM"
    const val WIFE_RELATIONSHIP_CODE = "WIF"
    const val HUSBAND_RELATIONSHIP_CODE = "HUS"
    const val OTHER_RELATIONSHIP_CODE = "OTH"

    //Medication Constants
    const val MEDICINE_ID = "MedicineID"
    const val MEDICATION_ID = "MedicationID"
    const val Drug_ID = "DrugID"
    const val SCHEDULE_ID = "IdSchedule"
    const val SERVER_SCHEDULE_ID = "ScheduleID"
    const val MEDICINE_NAME = "MedicineName"
    const val DRUG_TYPE_CODE = "DrugTypeCode"
    const val SCHEDULE_TIME = "ScheduleTime"
    const val MED_DATE = "MedDate"
    const val MEDICATION_UNIT = "Unit"
    const val DESCRIPTION = "Description"
    const val DOSAGE = "Dosage"
    const val INSTRUCTION = "Instruction"
    const val DOSAGE_REMAINING = "DosageRemaining"
    //const val MEDICINE_IN_TAKE_ID = "IdMedicineInTake"
    const val MEDICINE_IN_TAKE_ID = "MedicationInTakeID"
    const val TAKEN = "Taken"
    const val MISSED = "Missed"
    const val SKIPPED = "Skipped"
    const val IN_TAKE = "InTake"
    const val MEDICINE_DETAILS = "MedicineDetails"
    const val TAKE_STATUS = "Status"
    const val BETTER = "Better"
    const val SAME = "Same"
    const val WORSE = "Worse"
    const val ADD = "Add"
    const val UPDATE = "Update"
    const val CANCEL = "Cancel"
    const val OK = "Ok"
    const val DATA = "Data"
    const val CLEAR_FITNESS_DATA = "ClearFitnessData"
    const val MEDICATION = "Medication"
    const val HRA = "HRA"
    const val TRACK_PARAMETER = "TrackParameter"
    const val CODE = "code"
    const val VIEW = "View"
    const val SHARE = "Share"
    const val DIGITIZE = "Digitize"
    const val DASHBOARD = "Dashboard"
    const val DOWNLOAD = "Download"

    const val NOTIFICATION = "Notification"
    const val NOTIFICATION_ID = "NotificationId"
    const val NOTIFICATION_ACTION = "NotificationAction"
    const val NOTIFICATION_TITLE = "title"
    const val NOTIFICATION_MESSAGE = "message"
    const val NOTIFICATION_URL = "url"
    const val TIME = "Time"
    const val TITLE = "Title"
    const val SUB_TITLE = "SubTitle"

    const val TRUE = "true"
    const val FALSE = "false"
    const val SUCCESS = "SUCCESS"
    const val FAILURE = "Failure"
    const val FROM = "from"
    const val RECORD = "record"
    const val PROFILE = "profile"
    const val SSO = "SSO"
    const val NSSO = "NSSO"
    const val WEB_URL = "WebUrl"
    const val HAS_COOKIES = "HasCookies"
    const val RELATION = "Relation"
    const val RELATION_CODE = "RelationShipCode"
    const val RELATION_SHIP_ID = "RelationShipID"
    const val RELATIVE_ID = "RelativeID"
    const val PERSON_ID = "PersonID"
    const val HRA_TEMPLATE_ID = "TemplateId"
    const val GENDER = "GENDER"
    const val BODY = "body"

    const val HEIGHT = "Height"
    const val WEIGHT = "Weight"
    const val SYSTOLIC = "Systolic"
    const val DIASTOLIC = "Diastolic"

    const val RANDOM_SUGAR = "Random Sugar"
    const val FASTING_SUGAR = "Fasting Sugar"
    const val POST_MEAL_SUGAR = "Post Meal Blood Sugar"
    const val HBA1C = "HbA1c"

    // Fitness
    const val SYNC: String = "sync"
    const val STEP_ID: String = "StepId"
    const val GOAL_ID: String = "GoalId"
    const val DISTANCE = "Distance"
    const val ACTIVE_TIME = "ActiveTime"
    const val STEPS_COUNT = "StepsCount"
    const val TOTAL_GOAL = "TotalGoal"
    const val GOAL_PERCENTILE = "GoalPercentile"
    const val CALORIES = "Calories"
    const val RECORD_DATE = "RecordDate"
    const val STEP_NOTIFICATION = "StepNotification"
    const val LAST_UPDATED_TIME = "LastUpdatedTime"
    const val LAST_SYNC_DATE = "LastSyncDate"

    const val USER = "User"
    const val RELATIVE = "Relative"
    const val EMAIL_ADDRESS = "EmailAddress"
    const val PHONE_NUMBER = "PhoneNumber"
    const val IS_PASSWORD_UPDATE = "IsPasswordUpdate"
    const val PROFILE_IMAGE_ID = "ProfileImageID"
    const val Toobar_Title = "ToolbarTitle"
    const val TOOLBAR_TOOLS_TRACKERS = "Tools and Trackers"
    const val TOOLBAR_HEALTH_PACKAGES = "Health Packages"
    const val TOOLBAR_CHAT_WITH_DOCTOR = "Chat With Doctor"
    const val TOOLBAR_APOLLO = "Apollo"
    const val TOOLBAR_MEDLIFE = "Med Life"
    const val TOOLBAR_CHAT_WITH_DIETICIAN = "Chat With Dietician"
    const val UNITS_MGDL = "mg/dL"
    const val MONTH = "Month"
    const val YEAR = "Year"
    const val DAYOFWEEK = "DayOfWeek"
    const val DAYOFMONTH = "DayOfMonth"
    const val MONTHOFYEAR = "MonthOfYear"
    const val IS_TODAY = "IsToday"
    const val DATE = "date"
    const val FIRST_NAME = "FIRSTNAME"
    const val PRIMARY_PHONE = "PrimaryPhone"
    const val AGE = "Age"
    const val DATE_OF_BIRTH = "DateOfBirth"
    const val DIALING_CODE = "DialingCode"
    const val NAME = "name"
    const val LOGIN_NAME = "LoginName"
    const val LOGIN_PHR: String = "LOGIN"
    const val REGISTER_PHR: String = "REGISTER"
    const val USER_INFO: String = "PREF_USER_INFO"
    const val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    // BMI CALCULATION VALUES
    const val HEIGHT_MIN = 120
    const val HEIGHT_MAX = 240
    const val WEIGHT_MIN_METRIC = 30
    const val WEIGHT_MAX_METRIC = 150

    object UserConstants {
        const val EMAIL_ADDRESS = "EmailAddress"
        const val PASSWORD = "Password"
        const val GENDER = "GENDER"
        const val AUTH_TYPE = "AuthenticationType"
        const val PHONE_NUMBER = "PhoneNumber"
        const val PARTNER_CODE = "PartnerCode"
        const val OTP = "OTP"
        const val CLUSTER_CODE = "ClusterCode"
        const val NAME = "Name"
        const val DOB = "DOB"
        const val EMPLOYEE_ID = "EmployeeID"

    }

}

object ApiConstants{
    const val TRACK_PARAM_LIST_MASTER = "trackParamListMaster"
    const val DOC_TYPE_MASTER = "docTypeMaster"
    const val PARAMETER_HISTORY = "paramHistory"

    const val BMI_HISTORY = "bmiHistory"
    const val BLOOD_PRESSURE_HISTORY = "bloodPressureHistory"
    const val WHR_HISTORY = "whrHistory"

    const val RELATIVE_LIST = "relativeList"
    const val STEPS_GOAL = "stepsGoal"
    const val MEDICAL_PROFILE_SUMMERY = "medicalProfileSummery"
    const val MEDICATION_LIST = "medicationList"
}