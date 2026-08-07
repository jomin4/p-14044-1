package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUserAttr
import org.springframework.data.jpa.repository.JpaRepository

interface PostUserAttrRepository : JpaRepository<PostUserAttr, Int>, PostUserAttrRepositoryCustom {
}
