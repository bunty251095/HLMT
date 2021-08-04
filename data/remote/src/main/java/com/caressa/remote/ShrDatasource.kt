package com.caressa.remote

import com.caressa.model.shr.*
import okhttp3.RequestBody

class ShrDatasource(
    private val defaultService: ApiService,
    private val encryptedService: ApiService
) {

    fun getDocumentTypeResponse(data: ListDocumentTypesModel) =
        encryptedService.fetchDocumentTypeApi(data)

    fun getDocumentListResponse(data: ListDocumentsModel) =
        encryptedService.fetchDocumentListApi(data)

    fun getRelativesListResponse(data: ListRelativesModel) =
        encryptedService.fetchRelativesListApi(data)

    fun saveRecordToServerResponse(data: SaveDocumentModel) =
        encryptedService.saveRecordToServerApi(data)

    fun deleteRecordsFromServerResponse(data: DeleteDocumentModel) =
        encryptedService.deleteRecordsFromServerApi(data)

    fun downloadDocumentFromServerResponse(data: DownloadDocumentModel) =
        encryptedService.downloadDocumentFromServerApi(data)

    fun getUnitExistResponse(data: OCRUnitExistModel) = encryptedService.checkUnitExist(data)

    fun ocrSaveDocumentResponse(data: OCRSaveModel) = encryptedService.ocrSaveDocument(data)

    fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ) = defaultService.ocrDigitizeDocument(fileBytes, partnerCode, fileExtension)
}