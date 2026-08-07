package com.back.domain.post.post.entity

import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity

@Entity
class PostBody(
    var content: String
) : BaseEntity() {

}
