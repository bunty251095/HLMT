package com.caressa.tools_calculators.model

class Answer {
    constructor(questionCode: String, answerCode: String, value: String) {
        this.questionCode = questionCode
        this.answerCode = answerCode
        this.value = value
    }

    constructor() {}

    var questionCode = ""
    var answerCode = ""
    var value = ""
}