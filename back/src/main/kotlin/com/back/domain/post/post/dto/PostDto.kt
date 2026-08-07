package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import java.time.LocalDateTime

data class PostDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val title: String
) {
    constructor(post: Post) : this(
        post.id,
        post.createDate,
        post.modifyDate,
        post.author.id,
        post.author.name,
        post.author.redirectToProfileImgUrlOrDefault,
        post.title
    )
}
