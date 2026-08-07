package com.back.domain.post.postUser.dto

import com.back.domain.post.postUser.entity.PostUser
import java.time.LocalDateTime

data class PostUserDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val name: String,
    val profileImgUrl: String,
    val postsCount: Int,
    val postCommentsCount: Int,
) {
    constructor(postUser: PostUser) : this(
        postUser.id,
        postUser.createDate,
        postUser.modifyDate,
        postUser.name,
        postUser.redirectToProfileImgUrlOrDefault,
        postUser.postsCount,
        postUser.postCommentsCount
    )
}
