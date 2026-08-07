package com.back.domain.member.member.entity

class MemberProxy(
    private val real: Member,
    id: Int,
    username: String,
    nickname: String
) : Member(id, username, nickname) {
    override var nickname: String
        get() = super.nickname
        set(value) {
            super.nickname = value
            real.nickname = value
        }

    override var createDate
        get() = real.createDate
        set(value) {
            real.createDate = value
        }

    override var modifyDate
        get() = real.modifyDate
        set(value) {
            real.modifyDate = value
        }

    override var profileImgUrl
        get() = real.profileImgUrl
        set(value) {
            real.profileImgUrl = value
        }

    override var apiKey
        get() = real.apiKey
        set(value) {
            real.apiKey = value
        }

    override var password
        get() = real.password
        set(value) {
            real.password = value
        }
}
