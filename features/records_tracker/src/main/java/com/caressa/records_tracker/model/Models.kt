package com.caressa.records_tracker.model

data class RecordDetails( val PersonID: String =""  , val Comments: String ="", val Title: String ="", val Code: String ="" , val RecordId: String ="" , val RecordCount: Int )

data class DocumentType( val title: String ="" , val code: String ="", val imageId: Int = 0 )

//data class DocumentDetails( val origFileName: String  , val name: String, val Path: String, val ImageType: String )