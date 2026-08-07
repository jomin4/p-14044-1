package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUserProxy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostUserRepositoryTest {
    @Autowired
    private lateinit var postUserRepository: PostUserRepository

    @Test
    @DisplayName("PostUserProxy")
    fun t1() {
        val realPostUser = postUserRepository.getReferenceById(1)

        val postUser = PostUserProxy(
            realPostUser,
            1,
            "system",
            "시스템",
        )

        // fulfill 필요없음
        assertThat(postUser.id).isEqualTo(1)
        assertThat(postUser.username).isEqualTo("system")
        assertThat(postUser.name).isEqualTo("시스템")
        assertThat(postUser.redirectToProfileImgUrlOrDefault).endsWith("/api/v1/members/1/redirectToProfileImg")
        assertThat(postUser.isAdmin).isEqualTo(true)
        assertThat(postUser.authorities).hasSize(1)
        assertThat(postUser.authoritiesAsStringList).containsExactly("ROLE_ADMIN")
        assertThat(postUser).isEqualTo(realPostUser)

        // fulfill 필요함
        assertThat(postUser.createDate).isNotNull
        assertThat(postUser.modifyDate).isNotNull
        assertThat(postUser.profileImgUrl).isBlank
        assertThat(postUser.postsCount).isNotNull
        assertThat(postUser.postCommentsCount).isNotNull
    }
}
