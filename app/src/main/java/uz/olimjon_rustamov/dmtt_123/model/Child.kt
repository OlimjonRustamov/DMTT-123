package uz.olimjon_rustamov.dmtt_123.model

class Child {
    var fullName: String? = null
    var id: String? = null

    constructor() {
    }

    constructor(fullName: String?, id: String?) {
        this.fullName = fullName
        this.id = id
    }
}