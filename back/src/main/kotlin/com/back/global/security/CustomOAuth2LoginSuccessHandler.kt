package com.back.global.security

import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import com.back.standard.extensions.base64Decode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CustomOAuth2LoginSuccessHandler(
    private val memberService: MemberService,
    private val rq: Rq,
) : AuthenticationSuccessHandler {

    @Transactional(readOnly = true) // 이걸 안하면 actor.apiKey 에서 LazyInitializationException 발생
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val actor = rq.actor

        val accessToken = memberService.genAccessToken(actor)

        rq.setCookie("apiKey", actor.apiKey)
        rq.setCookie("accessToken", accessToken)

        val redirectUrl = request.getParameter("state")
            ?.let { encoded ->
                runCatching {
                    encoded.base64Decode()
                }.getOrNull()
            }
            ?.substringBefore('#')
            ?.takeIf { it.isNotBlank() }
            ?: "/"

        rq.sendRedirect(redirectUrl)
    }
}
