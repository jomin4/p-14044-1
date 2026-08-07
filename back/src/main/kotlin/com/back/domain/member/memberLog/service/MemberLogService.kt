package com.back.domain.member.memberLog.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.memberLog.entity.MemberLog
import com.back.domain.member.memberLog.repository.MemberLogRepository
import com.back.domain.post.post.entity.Post
import com.back.domain.post.postComment.entity.PostComment
import com.back.domain.post.postComment.event.PostCommentWrittenEvent
import com.back.standard.util.Ut
import org.springframework.stereotype.Service

@Service
class MemberLogService(
    private val memberLogRepository: MemberLogRepository,
) {
    fun save(event: PostCommentWrittenEvent) {
        val log = MemberLog(
            PostCommentWrittenEvent::class.simpleName!!,
            PostComment::class.simpleName!!,
            event.postCommentDto.id,
            Member(event.postCommentDto.authorId),
            Post::class.simpleName!!,
            event.postDto.id,
            Member(event.postDto.authorId),
            Member(event.actorDto.id),
            Ut.json.toString(event, "")
        )

        memberLogRepository.save(log)
    }
}
