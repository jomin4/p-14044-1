package com.back.domain.member.member.dto

import com.back.domain.member.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MemberDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val name: String,
    @get:JsonProperty("isAdmin")
    val isAdmin: Boolean,
    val profileImgUrl: String
) {
    constructor(member: Member) : this(
        member.id,
        member.createDate,
        member.modifyDate,
        member.name,
        member.isAdmin,
        member.redirectToProfileImgUrlOrDefault
    )
}
