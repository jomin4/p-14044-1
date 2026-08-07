package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUser

interface PostUserRepositoryCustom {
    fun findByUsername(username: String): PostUser?
}
