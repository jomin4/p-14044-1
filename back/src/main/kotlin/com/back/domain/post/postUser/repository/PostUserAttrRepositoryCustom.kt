package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.entity.PostUserAttr

interface PostUserAttrRepositoryCustom {
    fun findBySubjectAndName(subject: PostUser, name: String): PostUserAttr?
}
