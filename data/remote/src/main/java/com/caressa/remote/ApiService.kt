package com.caressa.remote

import com.caressa.common.constants.Constants
import com.caressa.model.*
import com.caressa.model.blogs.BlogModel
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.entity.Users
import com.caressa.model.fitness.*
import com.caressa.model.home.*
import com.caressa.model.hra.*
import com.caressa.model.medication.*
import com.caressa.model.parameter.*
import com.caressa.model.security.*
import com.caressa.model.shr.*
import com.caressa.model.shr.ListRelativesModel
import com.caressa.model.toolscalculators.*
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.GET



interface ApiService{

    /**
     * Security Module Api Calls
     */
    @POST(Constants.strProxyLoginUrl)
    fun loginDocumentProxyAPI(@Query("Data") loginEncryptedQuery: String) : Deferred<ApiResponse<Users>>

    @POST(Constants.strProxyRegistrationUrl)
    fun registerDocumentProxyAPI(@Query("Data") registerEncryptedQuery: String): Deferred<ApiResponse<Users>>

    // **********New API**************
    @POST("Security/api/Account/CheckIfLoginNameExists/")
    fun checkLoginNameExistsAPI(@Body loginNameExistsModel: LoginNameExistsModel): Deferred<BaseResponse<LoginNameExistsModel.IsExistResponse>>

    @POST(Constants.strProxyLoginUrl)
    fun hlmtLoginAPI(@Body loginModel: LoginModel): Deferred<BaseResponse<LoginModel.Response>>


    // ************************
    @POST("security/api/account/CheckIfEmailExists/")
    fun checkEmailExistsAPI(@Body emailOrPhoneExistsModel: EmailExistsModel): Deferred<BaseResponse<EmailExistsModel.IsExistResponse>>

    @POST("security/api/account/CheckIfPrimaryPhoneExists/")
    fun checkPhoneExistsAPI(@Body phoneExistsModel: PhoneExistsModel): Deferred<BaseResponse<PhoneExistsModel.IsExistResponse>>

    @POST("Security/api/Account/GetTermsAndConditionsForPartner")
    fun getTermsAndConditionsForPartnerAPI(@Body termsConditionsModel: TermsConditionsModel): Deferred<BaseResponse<TermsConditionsModel.TermsConditionsResponse>>

    @POST("security/api/OTP/Generate")
    fun generateOTPforUserAPI(@Body generateOtpModel: GenerateOtpModel): Deferred<BaseResponse<GenerateOtpModel.GenerateOTPResponse>>

    @POST("security/api/OTP/Validate")
    fun validateOTPforUserAPI(@Body generateOtpModel: GenerateOtpModel): Deferred<BaseResponse<GenerateOtpModel.GenerateOTPResponse>>

    @POST("Security/api/Security/UpdateNewPassword")
    fun updatePasswordAPI(@Body changePasswordModel: ChangePasswordModel): Deferred<BaseResponse<ChangePasswordModel.ChangePasswordResponse>>

    @POST(Constants.SAVE_CLOUD_MESSAGING_ID_API)
    fun saveCloudMessagingId(@Body saveCloudMessagingIdModel: SaveCloudMessagingIdModel): Deferred<BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>

    /**
     * Medication Module Api Calls
     */
    @POST(Constants.MEDICATION_GET_API)
    fun getMedicine(@Body getMedicine: GetMedicineModel): Deferred<BaseResponse<GetMedicineModel.GetMedicineResponse>>

    @POST(Constants.MEDICATION_DRUGS_LIST_API)
    fun fetchDrugsList(@Body drugsModel: DrugsModel): Deferred<BaseResponse<DrugsModel.DrugsResponse>>

    @POST(Constants.MEDICATION_LIST_BY_DATE_API)
    fun fetchMedicationList(@Body medicationList: MedicationListModel): Deferred<BaseResponse<MedicationListModel.Response>>

    @POST(Constants.MEDICATION_LIST_BY_DAY_API)
    fun fetchMedicationListByDay(@Body request: MedicineListByDayModel): Deferred<BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>>

    @POST(Constants.MEDICATION_SAVE_API)
    fun saveMedicine(@Body request: AddMedicineModel): Deferred<BaseResponse<AddMedicineModel.AddMedicineResponse>>

    @POST(Constants.MEDICATION_UPDATE_API)
    fun UpdateMedicine(@Body request: UpdateMedicineModel): Deferred<BaseResponse<UpdateMedicineModel.UpdateMedicineResponse>>

    @POST(Constants.MEDICATION_SET_ALERT_API)
    fun setAlert(@Body request: SetAlertModel): Deferred<BaseResponse<SetAlertModel.SetAlertResponse>>

    @POST(Constants.MEDICATION_ADD_IN_TAKE_API)
    fun addInTake(@Body request: AddInTakeModel): Deferred<BaseResponse<AddInTakeModel.AddInTakeResponse>>

    @POST(Constants.MEDICATION_DELETE_TAKE_API)
    fun deleteMedicine(@Body updateMedicine: DeleteMedicineModel): Deferred<BaseResponse<DeleteMedicineModel.DeleteMedicineResponse>>

    @POST(Constants.MEDICATION_GET_MEDICINE_IN_TAKE_API)
    fun fetchMedicationInTakeByScheduleID(@Body medicineDetails: MedicineInTakeModel): Deferred<BaseResponse<MedicineInTakeModel.MedicineDetailsResponse>>
    /**
     * Parameter Module Api Calls
     */

    @POST("phr/api/labrecord/listmaster")
    fun fetchParamList(@Body paramList: ParameterListModel): Deferred<BaseResponse<ParameterListModel.Response>>

    @POST("phr/api/LabRecord/ListHistoryByProfileCodes")
    fun getLabRecordList(@Body recordList: LabRecordsListModel): Deferred<BaseResponse<TrackParameterMaster.HistoryResponse>>

    @POST("PHR/api/LabRecord/ListParameterTrackingPreferences")
    fun getParameterPreferences(@Body request: ParameterPreferenceModel): Deferred<BaseResponse<ParameterPreferenceModel.Response>>

    @POST("phr/api/BMI/ListHistory")
    fun getBMIHistory(@Body request: BMIHistoryModel):Deferred<BaseResponse<BMIHistoryModel.Response>>

    @POST("phr/api/WHR/ListHistory")
    fun getWHRHistory(@Body request: WHRHistoryModel):Deferred<BaseResponse<WHRHistoryModel.Response>>

    @POST("phr/api/BloodPressure/ListHistory")
    fun getBloodPressureHistory(@Body request: BloodPressureHistoryModel):Deferred<BaseResponse<BloodPressureHistoryModel.Response>>

    @POST("PHR/api/LabRecord/SaveParameterTrackingPreference")
    fun saveParameterTrackingPreferences()

    @POST("phr/api/LabRecord/Synchronize")
    fun saveLabRecords(@Body data: SaveParameterModel): Deferred<BaseResponse<SaveParameterModel.Response>>

    @POST("phr/api/WHR/Synchronize")
    fun saveWhrRecord()

    @POST("phr/api/BMI/Synchronize")
    fun saveBmiRecord()



    /**
     * Fitness Lib Api Calls
     */

    @POST(Constants.FITNESS_LIST_HISTORY_API)
    fun fetchStepsListHistory(@Body request: StepsHistoryModel): Deferred<BaseResponse<StepsHistoryModel.Response>>

    @POST(Constants.FITNESS_GET_LATEST_GOAL_API)
    fun fetchLatestGoal(@Body request: GetStepsGoalModel): Deferred<BaseResponse<GetStepsGoalModel.Response>>

    @POST(Constants.FITNESS_SET_GOAL_API)
    fun saveStepsGoal(@Body request: SetGoalModel): Deferred<BaseResponse<SetGoalModel.Response>>

    @POST(Constants.FITNESS_STEP_SAVE_LIST_API)
    fun saveStepsList(@Body request: StepsSaveListModel): Deferred<BaseResponse<StepsSaveListModel.StepsSaveListResponse>>

    @POST(Constants.FITNESS_STEP_SAVE_API)
    fun saveStepsData(@Body request: FitnessModel): Deferred<BaseResponse<FitnessModel.Response>>


    //  ****************HRA Module Api Calls ****************
    @POST(Constants.HRA_START_API)
    fun hraStartAPI(@Body hraStartModel : HraStartModel): Deferred<BaseResponse<HraStartModel.HraStartResponse>>

    @POST(Constants.BMI_EXIST_API)
    fun checkBMIExistAPI(@Body bmiExistModel: BMIExistModel): Deferred<BaseResponse<BMIExistModel.BMIExistResponse>>

    @POST(Constants.BP_EXIST_API)
    fun checkBPExistAPI(@Body bpExistModel: BPExistModel): Deferred<BaseResponse<BPExistModel.BPExistResponse>>

    @POST(Constants.LAB_RECORDS_API)
    fun fetchLabRecordsAPI(@Body labRecordsModel: LabRecordsModel): Deferred<BaseResponse<LabRecordsModel.LabRecordsExistResponse>>

    @POST(Constants.SAVE_AND_SUBMIT_HRA_API)
    fun saveAndSubmitHraAPI(@Body saveAndSubmitHraModel: SaveAndSubmitHraModel): Deferred<BaseResponse<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>>

    @POST(Constants.MEDICAL_PROFILE_SUMMARY_API)
    fun getMedicalProfileSummaryAPI(@Body bmiExistModel: HraMedicalProfileSummaryModel): Deferred<BaseResponse<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>

    @POST(Constants.ASSESSMENT_SUMMARY_API)
    fun getAssessmentSummaryAPI(@Body hraAssessmentSummaryModel: HraAssessmentSummaryModel): Deferred<BaseResponse<HraAssessmentSummaryModel.AssessmentSummaryResponce>>

    @POST(Constants.GET_HRA_SUMMARY_API)
    fun getHRAHistory(@Body hraHistoryModel: HraHistoryModel): Deferred<BaseResponse<HraHistoryModel.HRAHistoryResponse>>

    @POST(Constants.LIST_RECOMMENDED_TESTS_API)
    fun getListRecommendedTestsAPI(@Body listRecommendedTestsModel: HraListRecommendedTestsModel): Deferred<BaseResponse<HraListRecommendedTestsModel.ListRecommendedTestsResponce>>
    //  ****************HRA Module Api Calls ****************

    //  ****************SHR Module Api Calls ****************
    @POST(Constants.SHR_DOCUMENT_TYPE_API)
    fun fetchDocumentTypeApi(@Body request: ListDocumentTypesModel): Deferred<BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>>

    @POST(Constants.SHR_DOCUMENT_LIST_API)
    fun fetchDocumentListApi(@Body request: ListDocumentsModel): Deferred<BaseResponse<ListDocumentsModel.ListDocumentsResponse>>


    @POST(Constants.SHR_DOCUMENT_SAVE_API)
    fun saveRecordToServerApi(@Body request: SaveDocumentModel): Deferred<BaseResponse<SaveDocumentModel.SaveDocumentsResponse>>

    @POST(Constants.SHR_DOCUMENT_DELETE_API)
    fun deleteRecordsFromServerApi(@Body request: DeleteDocumentModel): Deferred<BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>>

    @POST(Constants.SHR_DOCUMENT_DOWNLOAD_API)
    fun downloadDocumentFromServerApi(@Body request: DownloadDocumentModel): Deferred<BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>>

    @POST(Constants.SHR_OCR_UNIT_EXIST)
    fun checkUnitExist(@Body request: OCRUnitExistModel): Deferred<BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>>

    @POST(Constants.SHR_OCR_DOCUMENT_SAVE)
    fun ocrSaveDocument(@Body request: OCRSaveModel): Deferred<BaseResponse<OCRSaveModel.OCRSaveResponse>>

    @Multipart
    @POST(Constants.SHR_OCR_EXTRACT_DOCUMENT)
    fun ocrDigitizeDocument(@Part("FileBytes") fileBytes: RequestBody,
                            @Part("PartnerCode") partnerCode: RequestBody,
                            @Part("FileExtension") fileExtension: RequestBody): Deferred<BaseResponse<OcrResponce>>
    //  ****************SHR Module Api Calls ****************

    //  ****************Tools Calculators Module Api Calls ****************
    @POST(Constants.TOOLS_START_QUIZ_API)
    fun toolsStartQuizApi(@Body request: StartQuizModel): Deferred<BaseResponse<StartQuizModel.StartQuizResponse>>

    @POST(Constants.TOOLS_HEART_AGE_API)
    fun toolsHeartAgeSaveResponceApi(@Body request: HeartAgeSaveResponceModel): Deferred<BaseResponse<HeartAgeSaveResponceModel.HeartAgeSaveResponce>>

    @POST(Constants.TOOLS_DIABETES_API)
    fun toolsDiabetesSaveResponceApi(@Body request: DiabetesSaveResponceModel): Deferred<BaseResponse<DiabetesSaveResponceModel.DiabetesSaveResponce>>

    @POST(Constants.TOOLS_HYPERTENSION_API)
    fun toolsHypertensionSaveResponceApi(@Body request: HypertensionSaveResponceModel): Deferred<BaseResponse<HypertensionSaveResponceModel.HypertensionSaveResponce>>

    @POST(Constants.TOOLS_STRESS_ANXIETY_API)
    fun toolsStressAndAnxietySaveResponceApi(@Body request: StressAndAnxietySaveResponceModel): Deferred<BaseResponse<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>>

    @POST(Constants.TOOLS_SMART_PHONE_ADDICTION_API)
    fun toolsSmartPhoneSaveResponceApi(@Body request: SmartPhoneSaveResponceModel): Deferred<BaseResponse<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>>
    //  ****************Tools Calculators Module Api Calls ****************

    //  ****************Family Member Api Calls ****************
    @POST(Constants.RELATIVES_LIST_API)
    fun fetchRelativesListApi(@Body request: ListRelativesModel): Deferred<BaseResponse<ListRelativesModel.ListRelativesResponse>>

    @POST(Constants.ADD_RELATIVE_API)
    fun addRelativeApi(@Body request: AddRelativeModel): Deferred<BaseResponse<AddRelativeModel.AddRelativeResponse>>

    @POST(Constants.UPDATE_RELATIVE_API)
    fun updateRelativeApi(@Body request: UpdateRelativeModel): Deferred<BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>>

    @POST(Constants.REMOVE_RELATIVE_API)
    fun removeRelativeApi(@Body request: RemoveRelativeModel): Deferred<BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>>
    //  ****************Family Member Api Calls ****************

    //  ****************User Api Calls ****************

    @POST(Constants.GET_USER_DETAIL_API)
    fun getUserDetailsApi(@Body request: UserDetailsModel): Deferred<BaseResponse<UserDetailsModel.UserDetailsResponse>>

    @POST(Constants.UPDATE_PERSONAL_DETAIL_API)
    fun updateUserDetailsApi(@Body request: UpdateUserDetailsModel): Deferred<BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>>

    @POST(Constants.SHR_DOCUMENT_DOWNLOAD_API)
    fun getProfileImageApi(@Body request: ProfileImageModel): Deferred<BaseResponse<ProfileImageModel.ProfileImageResponse>>

    @Multipart
    @POST(Constants.UPLOAD_PROFILE_IMAGE_API)
    fun uploadProfileImage(
        @Part("PersonID") personID: RequestBody,
        @Part("FileName") fileName: RequestBody,
        @Part("DocumentTypeCode") documentTypeCode: RequestBody,
        @Part("ByteArray") byteArray: RequestBody,
        @Part("AuthTicket") authTicket: RequestBody): Deferred<BaseResponse<UploadProfileImageResponce>>

    @POST(Constants.REMOVE_PROFILE_IMAGE_API)
    fun removeProfileImageApi(@Body request: RemoveProfileImageModel): Deferred<BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>>

    //  ****************User Api Calls ****************

    //  ****************Family Member Api Calls ****************
    @POST(Constants.DOCTORS_LIST_API)
    fun getFamilyDoctorsListApi(@Body request: FamilyDoctorsListModel): Deferred<BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>>

    @POST(Constants.DOCTORS_LIST_SPECIALITY_API)
    fun getSpecialityListApi(@Body request: ListDoctorSpecialityModel): Deferred<BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>

    @POST(Constants.ADD_DOCTOR_API)
    fun addDoctorApi(@Body request: FamilyDoctorAddModel): Deferred<BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>>

    @POST(Constants.UPDATE_DOCTOR_API)
    fun updateDoctorApi(@Body request: FamilyDoctorUpdateModel): Deferred<BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>

    @POST(Constants.REMOVE_DOCTOR_API)
    fun removeDoctorApi(@Body request: RemoveDoctorModel): Deferred<BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>>

    //  ****************Family Member Api Calls ****************

    @POST(Constants.CONTACT_US_API)
    fun contactUsApi(@Body request: ContactUsModel): Deferred<BaseResponse<ContactUsModel.ContactUsResponse>>

    @POST(Constants.SAVE_FEEDBACK_API)
    fun saveFeedbackApi(@Body request: SaveFeedbackModel): Deferred<BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>>

    @POST(Constants.PASSWORD_CHANGE_API)
    fun passwordChangeApi(@Body request: PasswordChangeModel): Deferred<BaseResponse<PasswordChangeModel.ChangePasswordResponse>>

    @POST(Constants.CHECK_APP_UPDATE_API)
    fun checkAppUpdateApi(@Body request: CheckAppUpdateModel): Deferred<BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>>

    //  ****************Blogs Api Call ****************
/*    @GET
    fun downloadBlogs( @Url url: String): Deferred<BlogModel.BlogResponce>*/
    @GET
    fun downloadBlogs( @Url url: String): Deferred<List<BlogModel.Blog>>
/*    @GET
    fun downloadBlogs(@Url url: String): Call<ResponseBody>*/
    //  ****************Blogs Api Call ****************
}