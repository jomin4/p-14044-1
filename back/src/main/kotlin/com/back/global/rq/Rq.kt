package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.MemberProxy
import com.back.domain.member.member.service.MemberService
import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.entity.PostUserProxy
import com.back.domain.post.postUser.service.PostUserService
import com.back.global.security.SecurityUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class Rq(
    private val req: HttpServletRequest,
    private val resp: HttpServletResponse,
    private val memberService: MemberService,
    private val postUserService: PostUserService,
) {
    val actor: Member
        get() = SecurityContextHolder
            .getContext()
            ?.authentication
            ?.principal
            ?.let {
                if (it is SecurityUser) {
                    MemberProxy(
                        memberService.getReferenceById(it.id),
                        it.id,
                        it.username,
                        it.nickname
                    )
                } else {
                    null
                }
            }
            ?: throw IllegalStateException("인증된 사용자가 없습니다.")

    val postActor: PostUser
        get() = SecurityContextHolder
            .getContext()
            ?.authentication
            ?.principal
            ?.let {
                if (it is SecurityUser) {
                    PostUserProxy(
                        postUserService.getReferenceById(it.id),
                        it.id,
                        it.username,
                        it.nickname
                    )
                } else {
                    null
                }
            }
            ?: throw IllegalStateException("인증된 사용자가 없습니다.")

    fun getHeader(name: String, defaultValue: String): String {
        return req.getHeader(name) ?: defaultValue
    }

    fun setHeader(name: String, value: String) {
        resp.setHeader(name, value)
    }

    fun getCookieValue(name: String, defaultValue: String): String =
        req.cookies
            ?.firstOrNull { it.name == name }
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?: defaultValue

    fun setCookie(name: String, value: String?) {
        val cookie = Cookie(name, value ?: "").apply {
            path = "/"
            isHttpOnly = true
            domain = "localhost"
            secure = true
            setAttribute("SameSite", "Strict")
            maxAge = if (value.isNullOrBlank()) 0 else 60 * 60 * 24 * 365
        }

        resp.addCookie(cookie)
    }

    fun deleteCookie(name: String) {
        setCookie(name, null)
    }

    fun sendRedirect(url: String) {
        resp.sendRedirect(url)
    }
}
