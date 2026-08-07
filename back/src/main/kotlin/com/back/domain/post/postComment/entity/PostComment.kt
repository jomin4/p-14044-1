package com.back.domain.post.postComment.entity

import com.back.domain.post.post.entity.Post
import com.back.domain.post.postUser.entity.PostUser
import com.back.global.exception.ServiceException
import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.ManyToOne

@Entity
class PostComment(
    @field:ManyToOne(fetch = LAZY) val author: PostUser,
    @field:ManyToOne(fetch = LAZY) val post: Post,
    var content: String,
) : BaseTime() {
    fun modify(content: String) {
        this.content = content
    }

    fun checkActorCanModify(actor: PostUser) {
        if (author != actor) throw ServiceException("403-1", "${id}번 댓글 수정 권한이 없습니다.")
    }

    fun checkActorCanDelete(actor: PostUser) {
        if (author != actor) throw ServiceException("403-2", "${id}번 댓글 삭제 권한이 없습니다.")
    }
}
