package com.back.domain.member.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.util.*

@Entity
class Member(
    id: Int,
    username: String,
    var password: String? = null,
    var nickname: String,
    @field:Column(unique = true) var apiKey: String,
) : BaseMember(id, username) {
    // 코프링에서 엔티티의 `by lazy` 필드가 제대로 작동하게 하려면
    // kotlin("plugin.jpa") 에 의해서 만들어지는 인자 없는 생성자로는 부족하다.
    // 귀찮지만 이렇게 직접 만들어야 한다.
    constructor() : this(0)

    constructor(id: Int) : this(id, "", "")

    constructor(id: Int, username: String, nickname: String) : this(
        id,
        username,
        null,
        nickname,
        ""
    )

    constructor(username: String, password: String?, nickname: String) : this(
        0,
        username,
        password,
        nickname,
        UUID.randomUUID().toString(),
    )

    val name: String
        get() = nickname

    fun modify(nickname: String, profileImgUrl: String?) {
        this.nickname = nickname

        profileImgUrl?.let { this.profileImgUrl = it }
    }

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }
}
