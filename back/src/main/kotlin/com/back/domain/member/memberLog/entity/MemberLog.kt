package com.back.domain.member.memberLog.entity

import com.back.domain.member.member.entity.Member
import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne

@Entity
class MemberLog(
    val type: String,
    val primaryType: String,
    val primaryId: Int,
    @field:ManyToOne(fetch = LAZY) val primaryOwner: Member,
    val secondaryType: String,
    val secondaryId: Int,
    @field:ManyToOne(fetch = LAZY) val secondaryOwner: Member,
    @field:ManyToOne(fetch = LAZY) val actor: Member,
    @field:Column(columnDefinition = "TEXT") val data: String,
) : BaseEntity() {

}
