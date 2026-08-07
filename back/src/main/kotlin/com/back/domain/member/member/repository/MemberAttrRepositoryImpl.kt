package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.MemberAttr
import jakarta.persistence.EntityManager
import org.hibernate.KeyType
import org.hibernate.Session

class MemberAttrRepositoryImpl(
    private val entityManager: EntityManager,
) : MemberAttrRepositoryCustom {
    override fun findBySubjectAndName(subject: Member, name: String): MemberAttr? {
        return entityManager.unwrap(Session::class.java)
            .find(
                MemberAttr::class.java,
                mapOf(
                    MemberAttr::subject.name to subject,
                    MemberAttr::name.name to name,
                ),
                KeyType.NATURAL,
            )
    }
}
