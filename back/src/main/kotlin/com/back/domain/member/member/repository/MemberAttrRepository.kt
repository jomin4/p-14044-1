package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.MemberAttr
import org.springframework.data.jpa.repository.JpaRepository

interface MemberAttrRepository : JpaRepository<MemberAttr, Int>, MemberAttrRepositoryCustom {
}
