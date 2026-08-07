package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.MemberAttr

interface MemberAttrRepositoryCustom {
    fun findBySubjectAndName(subject: Member, name: String): MemberAttr?
}
