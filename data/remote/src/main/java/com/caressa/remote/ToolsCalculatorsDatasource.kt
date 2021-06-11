package com.caressa.remote

import com.caressa.model.toolscalculators.*

class ToolsCalculatorsDatasource(private val defaultService: ApiService, private val encryptedService: ApiService ) {

    fun getStartQuizResponse( data : StartQuizModel) = encryptedService.toolsStartQuizApi(data)

    fun getHeartAgeSaveResponce( data : HeartAgeSaveResponceModel) = encryptedService.toolsHeartAgeSaveResponceApi(data)

    fun getDiabetesSaveResponce( data : DiabetesSaveResponceModel) = encryptedService.toolsDiabetesSaveResponceApi(data)

    fun getHypertensionSaveResponce( data : HypertensionSaveResponceModel) = encryptedService.toolsHypertensionSaveResponceApi(data)

    fun getStressAndAnxietySaveResponce( data : StressAndAnxietySaveResponceModel) = encryptedService.toolsStressAndAnxietySaveResponceApi(data)

    fun getSmartPhoneSaveResponce( data : SmartPhoneSaveResponceModel) = encryptedService.toolsSmartPhoneSaveResponceApi(data)


}