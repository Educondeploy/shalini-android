package com.tt.skolarrs.model.response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class LeadClass {
    @SerializedName("_id")
    @Expose
    private var id: String? = null

    @SerializedName("currentCollege")
    @Expose
    private var currentCollege: String? = null

    @SerializedName("currentCourse")
    @Expose
    private var currentCourse: String? = null

    @SerializedName("date")
    @Expose
    private var date: String? = null

    @SerializedName("event")
    @Expose
    private var event: String? = null

    @SerializedName("eventLink")
    @Expose
    private var eventLink: String? = null

    @SerializedName("ifAnyEntranceExam")
    @Expose
    private var ifAnyEntranceExam: String? = null

    @SerializedName("mobileNo")
    @Expose
    private var mobileNo: String? = null

    @SerializedName("name")
    @Expose
    private var name: String? = null

    @SerializedName("neetAspirants")
    @Expose
    private var neetAspirants: Boolean? = null

    @SerializedName("place")
    @Expose
    private var place: String? = null

    @SerializedName("preferredPlaceToStudy")
    @Expose
    private var preferredPlaceToStudy: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getCurrentCollege(): String? {
        return currentCollege
    }

    fun setCurrentCollege(currentCollege: String?) {
        this.currentCollege = currentCollege
    }

    fun getCurrentCourse(): String? {
        return currentCourse
    }

    fun setCurrentCourse(currentCourse: String?) {
        this.currentCourse = currentCourse
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun getEvent(): String? {
        return event
    }

    fun setEvent(event: String?) {
        this.event = event
    }

    fun getEventLink(): String? {
        return eventLink
    }

    fun setEventLink(eventLink: String?) {
        this.eventLink = eventLink
    }

    fun getIfAnyEntranceExam(): String? {
        return ifAnyEntranceExam
    }

    fun setIfAnyEntranceExam(ifAnyEntranceExam: String?) {
        this.ifAnyEntranceExam = ifAnyEntranceExam
    }

    fun getMobileNo(): String? {
        return mobileNo
    }

    fun setMobileNo(mobileNo: String?) {
        this.mobileNo = mobileNo
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getNeetAspirants(): Boolean? {
        return neetAspirants
    }

    fun setNeetAspirants(neetAspirants: Boolean?) {
        this.neetAspirants = neetAspirants
    }

    fun getPlace(): String? {
        return place
    }

    fun setPlace(place: String?) {
        this.place = place
    }

    fun getPreferredPlaceToStudy(): String? {
        return preferredPlaceToStudy
    }

    fun setPreferredPlaceToStudy(preferredPlaceToStudy: String?) {
        this.preferredPlaceToStudy = preferredPlaceToStudy
    }
}
