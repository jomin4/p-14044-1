package com.back.domain.member.memberLog.repository

import com.back.domain.member.memberLog.entity.MemberLog
import org.springframework.data.jpa.repository.JpaRepository

interface MemberLogRepository : JpaRepository<MemberLog, Int> {

}
