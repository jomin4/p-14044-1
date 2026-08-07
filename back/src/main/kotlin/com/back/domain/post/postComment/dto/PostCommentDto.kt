package com.back.domain.post.postComment.dto

import com.back.domain.post.postComment.entity.PostComment
import java.time.LocalDateTime

data class PostCommentDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val authorId: Int,
    val authorName: String,
    val authorProfileImgUrl: String,
    val postId: Int,
    val content: String
) {
    constructor(postComment: PostComment) : this(
        postComment.id,
        postComment.createDate,
        postComment.modifyDate,
        postComment.author.id,
        postComment.author.name,
        postComment.author.redirectToProfileImgUrlOrDefault,
        postComment.post.id,
        postComment.content
    )
}
