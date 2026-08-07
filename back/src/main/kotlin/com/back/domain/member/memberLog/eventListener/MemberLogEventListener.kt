package com.back.domain.member.memberLog.eventListener

import com.back.domain.member.memberLog.service.MemberLogService
import com.back.domain.post.postComment.event.PostCommentWrittenEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MemberLogEventListener(
    private val memberLogService: MemberLogService,
) {
    @EventListener
    fun handle(event: PostCommentWrittenEvent) {
        memberLogService.save(event)
    }
}
