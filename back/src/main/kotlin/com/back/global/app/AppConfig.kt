package com.back.global.app

import com.back.domain.member.member.entity.BaseMember
import com.back.domain.member.member.repository.MemberAttrRepository
import com.back.domain.member.member.repository.MemberRepository
import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import com.back.standard.util.Ut
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import tools.jackson.databind.ObjectMapper

@Configuration
class AppConfig(
    environment: Environment,
    objectMapper: ObjectMapper,
    memberAttrRepository: MemberAttrRepository,
    postUserAttrRepository: PostUserAttrRepository,
    memberRepository: MemberRepository,
) {
    init {
        Companion.environment = environment
        Ut.json.objectMapper = objectMapper
        BaseMember.memberRepository = memberRepository
        BaseMember.memberAttrRepository = memberAttrRepository
        PostUser.attrRepository = postUserAttrRepository
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        private lateinit var environment: Environment

        val isDev: Boolean
            get() = environment.matchesProfiles("dev")

        val isTest: Boolean
            get() = !environment.matchesProfiles("test")

        val isProd: Boolean
            get() = environment.matchesProfiles("prod")

        val isNotProd: Boolean
            get() = !isProd
    }
}
