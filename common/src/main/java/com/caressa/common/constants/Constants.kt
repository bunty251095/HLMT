package com.caressa.common.constants

import android.os.Environment

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
//    const val strProxyUrl = "DocumentProxy/"
    const val strProxyUrl = "Proxy/"
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
    const val strProxyUrl = "Proxy/"
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
    const val strWebChatDoctorURL: String =
        "https://tawk.to/chat/5c4181c951410568a1073185/default/?\$_tawk_popout=true"
    const val sirWebChatDietitianURL =
        "https://tawk.to/chat/5c418189ab5284048d0d7c0b/1d4mv5the/?\$_tawk_popout=true"
    //const val CHAT_URL: String = "https://tawk.to/chat/5b49c72b6d961556373dbb8b/default/?\$_tawk_popout=true"

    const val MAIN_DATABASE_NAME = Configuration.strAppIdentifier + ".db"
    const val DATABASE_NAME = "AIH_2.0"
    const val BASE_DB_NAME = "AIH_2.0"

    const val strEnableFamilyProfile: Boolean = true
    const val strApolloHtml = "file:///android_asset/apollo/apollo_SOP.html"

    val primaryStorage = Environment.getExternalStorageDirectory().toString()

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
    const val MEDICATION_GET_MEDICINE_IN_TAKE_API =
        "PHR/api/Medication/ListMedicationInTakeByScheduleID/"

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
    const val SECONDARY_COLOR = "secondaryColor"
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

    const val QUIZ_CODE_HEART_AGE = "HRTAGECAL"
    const val QUIZ_CODE_DIABETES = "DIAB_RISK_ASMT"
    const val QUIZ_CODE_HYPERTENSION = "HYPERTENSIONCAL"
    const val QUIZ_CODE_STRESS_ANXIETY = "DASS-21"
    const val QUIZ_CODE_SMART_PHONE = "SMARTPH"

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
    const val URI = "uri"
    const val CODE = "code"
    const val UPLOAD = "Upload"
    const val VIEW = "View"
    const val SHARE = "Share"
    const val DIGITIZE = "Digitize"
    const val DASHBOARD = "Dashboard"
    const val DOWNLOAD = "Download"
    const val RESTART = "Restart"

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
    const val REGISTER = "REGISTER"
    const val LOGIN = "LOGIN"
    const val FORGET = "FORGET"

    // BMI CALCULATION VALUES
    const val HEIGHT_MIN = 119
    const val HEIGHT_MAX = 245
    const val WEIGHT_MIN_METRIC = 29
    const val WEIGHT_MAX_METRIC = 150

    const val GALLERY_SELECT_CODE = 2291
    const val FILE_SELECT_CODE = 2292
    const val CAMERA_SELECT_CODE = 2293
    const val REQ_CODE_SAF = 2294
    const val REQ_CODE_STORAGE = 2296

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

object ApiConstants {
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

object FirebaseConstants {
    const val PARTNER_IDENTIFIER = "hlmt_"

    const val IS_HLMT_USER_YES_EVENT = "is_hlmt_user_yes"
    const val IS_HLMT_USER_NO_EVENT = "is_hlmt_user_no"
    const val HLMT_LOGIN_SUCCESSFUL_EVENT = "hlmt360_login_successful"
    const val HLMT_LOGIN_FAIL_EVENT = "hlmt360_login_fail"
    const val HLMT_REG_SUCCESSFUL_EVENT = "hlmt360_registration_successful"
    const val HLMT_REG_FAIL_EVENT = "hlmt360_registration_fail"
    const val SEND_OTP_SUCCESSFUL_EVENT = "send_otp_successful"
    const val SEND_OTP_FAIL_EVENT = "send_otp_fail"
    const val SEND_OTP_VERIFICATION_SUCCESSFUL_EVENT = "otp_verification_successful"
    const val SEND_OTP_VERIFICATION_FAIL_EVENT = "otp_verification_fail"
    const val RESEND_OTP_EVENT = "resend_otp"
    const val NON_HLMT_LOGIN_SUCCESSFUL_EVENT = "regular_login_successful"
    const val NON_HLMT_LOGIN_FAIL_EVENT = "regular_login_fail"
    const val NON_HLMT_REGISTRATION_SUCCESSFUL_EVENT = "regular_registration_successful"
    const val NON_HLMT_REGISTRATION_FAIL_EVENT = "regular_registration_fail"
    const val LINK_ACCOUNT_SUCCESSFUL = "link_account_successful"
    const val LINK_ACCOUNT_FAIL = "link_account_fail"

    //**********************Home**********************
    // Screen event
    const val HOME_DASHBOARD = "home_dashboard"
    const val CONTACT_US_SCREEN = "contact_us_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val FEEDBACK_SCREEN = "feedback_screen"
    const val LINK_ACCOUNT_SCREEN = "link_account_screen"
    const val PASSWORD_CHANGE_SCREEN = "password_change_screen"

    const val MY_PROFILE_SCREEN = "my_profile_screen"
    const val FAMILY_MEMBER_LIST_SCREEN = "family_member_list_screen"
    const val FAMILY_MEMBER_ADD_SCREEN = "family_member_add_screen"
    const val FAMILY_MEMBER_UPDATE_SCREEN = "family_member_update_screen"
    const val FAMILY_MEMBER_SELECT_SCREEN = "family_member_select_screen"

    const val FAMILY_DOCTOR_LIST_SCREEN = "family_doctor_list_screen"
    const val FAMILY_DOCTOR_ADD_SCREEN = "family_doctor_add_screen"
    const val FAMILY_DOCTOR_UPDATE_SCREEN = "family_doctor_update_screen"

    // Button & Api Events
    const val MENU_LINK_ACCOUNT_CLICK = "menu_link_account_click"
    const val MENU_MY_PROFILE_CLICK = "menu_my_profile_click"
    const val MENU_FAMILY_MEMBERS_CLICK = "menu_family_members_click"
    const val MENU_FAMILY_DOCTORS_CLICK = "menu_family_doctors_click"
    const val MENU_CONTACT_US_MNU_CLICK = "menu_contact_us_mnu_click"
    const val MENU_SPREAD_THE_WORD_CLICK = "menu_spread_the_word_click"
    const val MENU_SETTINGS_CLICK = "menu_settings_click"
    const val RATE_US_CLICK = "rate_us_click"
    const val FEEDBACK_SUBMIT = "feedback_submit"
    const val LOGOUT_CLICK = "logout_click"

    const val PROFILE_EDIT_CLICK = "profile_edit_click"
    const val PROFILE_EDIT_CANCELLED = "profile_edit_cancelled"
    const val PROFILE_PIC_EDIT_CLICK = "profile_pic_edit_click"
    const val PROFILE_SWITCH_CLICK = "profile_switch_click"
    const val PROFILE_UPDATE_SUCCESS = "profile_update_success"
    const val PROFILE_UPDATE_FAILED = "profile_update_failed"

    const val FAMILY_MEMBER_ADD = "family_member_add"
    const val FAMILY_MEMBER_UPDATE = "family_member_update"
    const val FAMILY_MEMBER_ADD_SUCCESS = "family_member_add_success"
    const val FAMILY_MEMBER_ADD_FAILED = "family_member_add_failed"

    const val FAMILY_DOCTOR_ADD = "family_doctor_add"
    const val FAMILY_DOCTOR_UPDATE = "family_doctor_update"
    const val FAMILY_DOCTOR_ADD_SUCCESS = "family_doctor_add_success"
    const val FAMILY_DOCTOR_ADD_FAILED = "family_doctor_add_failed"

    const val CONTACT_US_SUBMIT = "contact_us_submit"
    const val CONTACT_US_SUBMIT_SUCCESS = "contact_us_submit_success"
    const val CONTACT_US_SUBMIT_FAILED = "contact_us_submit_failed"
    //**********************Home**********************

    //**********************Health Library**********************
    // Screen event
    const val BLOGS_DASHBOARD_SCREEN = "blogs_dashboard_screen"
    const val BLOGS_DETAILS_SCREEN = "blogs_details_screen"

    // Button & Api Events
    const val BLOGS_SHARE_CLICK = "blogs_share_click"
    //**********************Health Library**********************

    //**********************Activity Tracker**********************
    // Screen event
    const val ACTIVITY_TRACKER_DASHBOARD_SCREEN = "activity_tracker_dashboard_screen"
    const val ACTIVITY_TRACKER_MONTHLY_DETAIL_SCREEN = "activity_monthly_detail_screen"

    // Button & Api Events
    const val STEPS_GOAL_UPDATE = "steps_goal_update"
    const val STEPS_FORCE_SYNC = "steps_force_sync"
    //**********************Activity Tracker**********************

    //**********************HRA**********************
    // Screen event
    const val HRA_START_SCREEN = "hra_start_screen"
    const val HRA_COMPLETED_SCREEN  = "hra_completed_screen"
    const val HRA_FAMILY_MEMBER_SELECTION_SCREEN = "hra_family_member_selection_screen"
    const val HRA_SUMMERY_SCREEN = "hra_summery_screen"

    // Button & Api Events
    const val HRA_INITIATED = "hra_initiated"
    const val HRA_DOWNLOAD_REPORT = "hra_download_report"
    const val HRA_RESTART_CLICK = "hra_restart_click"
    const val HRA_VIEW_REPORT_CLICK = "hra_view_report_click"
    //**********************HRA**********************

    //**********************Store Health Records**********************
    // Screen event
    const val HEALTH_RECORDS_HOME_SCREEN = "health_records_home_screen"
    const val HEALTH_RECORDS_VIEW_SCREEN = "health_records_view_screen"
    const val HEALTH_RECORDS_UPLOAD_SCREEN = "health_records_upload_screen"
    const val HEALTH_RECORDS_SELECT_RELATION_SCREEN = "health_records_select_relation_screen"
    const val HEALTH_RECORDS_DOCUMENT_TYPE_SCREEN = "health_records_document_type_screen"
    const val HEALTH_RECORDS_DIGITIZE_SCREEN = "health_records_digitize_screen"
    const val HEALTH_RECORDS_DIGITIZE_LIST_SCREEN = "health_records_digitize_list_screen"

    // Button & Api Events
    const val HEALTH_RECORDS_UPLOADED = "health_records_uploaded"
    const val HEALTH_RECORD_DOWNLOAD = "health_record_download"
    const val HEALTH_RECORD_SHARED = "health_record_shared"
    const val HEALTH_RECORD_DELETED = "health_record_deleted"
    //**********************Store Health Records**********************

    //**********************Medicine Tracker**********************
    // Screen event
    const val MEDICINE_TRACKER_HOME_SCREEN = "medicine_tracker_home_screen"
    const val MEDICINE_TRACKER_DASHBOARD_SCREEN = "medicine_tracker_dashboard_screen"
    const val MEDICINE_TRACKER_ADD_SCREEN = "medicine_tracker_add_screen"
    const val MEDICINE_TRACKER_SCHEDULE_SCREEN = "medicine_tracker_schedule_screen"
    const val MEDICINE_TRACKER_MY_MEDICATIONS_SCREEN = "medicine_tracker_my_medications_screen"

    // Button & Api Events
    const val MEDICATION_ADDED = "medication_added"
    const val MEDICATION_NOTIFICATION_ENABLED = "medication_notification_enabled"
    const val MEDICATION_NOTIFICATION_DISABLED = "medication_notification_disabled"
    const val MEDICATION_EDIT = "medication_edit"
    const val MEDICATION_DELETED = "medication_deleted"
    const val MEDICINE_TRACKER_UPLOAD_PRESCRIPTION = "medicine_tracker_upload_prescription"
    //**********************Medicine Tracker**********************

    //**********************Tools Calculators**********************
    // Screen event
    const val TOOLS_CALCULATORS_HOME_SCREEN = "tools_calculators_home_screen"

    const val HEART_AGE_CALCULATE_SCREEN = "heart_age_calculate_screen"
    const val HEART_AGE_RECALCULATE_SCREEN = "heart_age_recalculate_screen"
    const val HEART_AGE_SUMMARY_SCREEN = "heart_age_summary_screen"
    const val HEART_AGE_REPORT_SCREEN = "heart_age_report_screen"

    const val DIABETES_CALCULATOR_INPUT_SCREEN = "diabetes_calculator_input_screen"
    const val DIABETES_SUMMARY_SCREEN = "diabetes_summary_screen"
    const val DIABETES_REPORT_SCREEN = "diabetes_report_screen"

    const val HYPERTENSION_CALCULATOR_INPUT_SCREEN = "hypertension_input_screen"
    const val HYPERTENSION_RECALCULATE_SCREEN = "hypertension_recalculate_screen"
    const val HYPERTENSION_SUMMARY_SCREEN = "hypertension_summary_screen"
    const val HYPERTENSION_REPORT_SCREEN = "hypertension_report_screen"

    const val STRESS_ANXIETY_CALCULATOR_INPUT_SCREEN = "stress_anxiety_calculator_input_screen"
    const val STRESS_ANXIETY_CALCULATOR_SUMMARY_SCREEN = "stress_anxiety_calculator_summary_screen"

    const val SMART_PHONE_CALCULATOR_INPUT_SCREEN = "smart_phone_calculator_input_screen"
    const val SMART_PHONE_CALCULATOR_SUMMARY_SCREEN = "smart_phone_calculator_summary_screen"

    const val DUE_DATE_CALCULATOR_INPUT_SCREEN = "due_date_calculator_input_screen"
    const val DUE_DATE_CALCULATOR_REPORT_SCREEN = "due_date_calculator_report_screen"

    // Button & Api Events
    const val HEART_AGE_CALCULATE_CLICK = "heart_age_calculate_click"
    const val HEART_AGE_RECALCULATE_CLICK = "heart_age_recalculate_click"
    const val HEART_AGE_VIEW_DETAILED_REPORT_CLICK = "heart_age_view_detailed_report_click"
    const val HEART_AGE_TAB_CLICKED = "heart_age_tab_clicked"
    const val HEART_RISK_TAB_CLICKED = "heart_risk_tab_clicked"

    const val DIABETES_CALCULATE_CLICK = "diabetes_calculate_click"
    const val DIABETES_VIEW_DETAILED_REPORT_CLICK = "diabetes_view_detailed_report_click"

    const val HYPERTENSION_CALCULATE_CLICK = "hypertension_calculate_click"
    const val HYPERTENSION_VIEW_DETAILED_REPORT_CLICK = "hypertension_view_detailed_report_click"

    const val DAS_CALCULATE_CLICK = "das_calculate_click"
    const val DEPRESSION_TAB_CLICKED = "depression_tab_clicked"
    const val ANXIETY_TAB_CLICKED = "anxiety_tab_clicked"
    const val STRESS_TAB_CLICKED = "stress_tab_clicked"

    const val SPA_CALCULATE_CLICK = "spa_calculate_click"

    const val DUE_DATE_CALCULATE_CLICK = "due_date_calculate_click"
    const val DUE_DATE_RECALCULATE_CLICK = "due_date_recalculate_click"
    //**********************Tools Calculators**********************

    //**********************Track Parameters**********************
    // Screen event
    const val TRACK_PARAMETERS_HOME_SCREEN = "track_parameters_home_screen"
    const val TRACK_PARAMETERS_SELECT_SCREEN = "track_parameters_select_screen"
    const val TRACK_PARAMETERS_UPDATE_SCREEN = "track_parameters_update_screen"
    const val TRACK_PARAMETERS_DASHBOARD_SCREEN = "track_parameters_dashboard_screen"
    const val TRACK_PARAMETERS_HISTORY_SCREEN = "track_parameters_history_screen"
    const val TRACK_PARAMETERS_COMPLETE_HISTORY_SCREEN = "track_parameters_complete_history_screen"

    // Button & Api Events
    const val TRACK_PARAMETER_SAVED = "track_parameter_saved"
    const val TRACK_PARAMETER_DETAIL_VIEW_CLICK = "track_parameter_detail_view_click"
    const val TRACK_PARAMETER_GRAPH_VIEW_CLICK = "track_parameter_graph_view_click"
    const val TRACK_PARAMETER_COMPLETE_HISTORY = "track_parameter_complete_history"
    const val TRACK_PARAMETER_UPLOAD_REPORT = "track_parameter_upload_report"
    const val TRACK_PARAMETER_ADD_MEDICATION = "track_parameter_add_medication"
    //**********************Track Parameters**********************

    //**********************Security**********************
    // Screen event
    const val NON_HLMT_LOGIN_SCREEN = "non_hlmt_user_login_screen"
    const val HLMT_USER_LOGIN_SCREEN = "hlmt_user_login_screen"
    const val LINK_ACCOUNT_STATUS_SCREEN = "link_account_status_screen"

    // Button & Api Events

    //**********************Security**********************
    // Screen event

    // New Events
    const val ACCOUNT_SELECTION = "hlmt_account_selection_screen"
    const val REGISTRATION_USER_INFO_SCREEN = "registration_user_information_screen"
    const val ACC = ""

}