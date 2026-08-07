package com.back.domain.post.postComment.event

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postUser.dto.PostUserDto
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class PostCommentWrittenEvent(
    @field:JsonIgnoreProperties("content") val postCommentDto: PostCommentDto,
    @field:JsonIgnoreProperties("title", "content") val postDto: PostDto,
    @field:JsonIgnore val actorDto: PostUserDto
) {

}
