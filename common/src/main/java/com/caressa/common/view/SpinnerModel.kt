package com.caressa.common.view

class SpinnerModel(name: String, code: String, position: Int, selection: Boolean) {
    var name = ""
    var code = ""
    var position: Int = 0
    var selection = false;

    init {
        this.name = name
        this.code = code
        this.position = position
        this.selection = selection;
    }

}