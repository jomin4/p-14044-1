package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUser
import org.springframework.data.jpa.repository.JpaRepository

interface PostUserRepository : JpaRepository<PostUser, Int>, PostUserRepositoryCustom {
}
