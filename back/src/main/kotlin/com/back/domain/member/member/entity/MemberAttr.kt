package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import org.hibernate.annotations.NaturalId

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["subject_id", "name"])
    ]
)
class MemberAttr(
    @field:NaturalId
    @field:ManyToOne(fetch = LAZY)
    @field:JoinColumn(name = "subject_id")
    val subject: Member,
    @field:NaturalId
    val name: String,
    @field:Column(name = "val", columnDefinition = "TEXT")
    var value: String,
) : BaseTime() {

}
