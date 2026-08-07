package com.back.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.core.user.OAuth2User

class SecurityUser(
    val id: Int,
    username: String,
    val nickname: String,
    authorities: Collection<GrantedAuthority>
) : User(username, "", authorities), OAuth2User { // 우리의 시나리오(REST API)에서는 이 객체의 비밀번호 필드를 활용할 일이 없다.
    override fun getAttributes(): Map<String, Any> = emptyMap()

    // OAuth2User.getName()은 유니크한 값을 리턴해야 하는데, nickname은 유니크하지 않을 수 있으므로 username을 리턴한다.
    override fun getName(): String = username
}
