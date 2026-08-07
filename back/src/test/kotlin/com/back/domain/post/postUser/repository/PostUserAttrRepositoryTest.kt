package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUserAttr
import com.back.standard.extensions.getOrThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PostUserAttrRepositoryTest {
    @Autowired
    private lateinit var postUserRepository: PostUserRepository

    @Autowired
    private lateinit var postUserAttrRepository: PostUserAttrRepository

    @Test
    @DisplayName("saveInt")
    fun t1() {
        val postUserSystem = postUserRepository.findByUsername("system").getOrThrow()

        val attr = PostUserAttr(
            postUserSystem,
            "postsCount",
            0.toString()
        )

        postUserAttrRepository.save(attr)
    }

    @Test
    @DisplayName("saveString")
    fun t2() {
        val postUserSystem = postUserRepository.findByUsername("system").getOrThrow()

        val attr = PostUserAttr(
            postUserSystem,
            "grade",
            "브론즈"
        )

        postUserAttrRepository.save(attr)
    }

    @Test
    @DisplayName("find")
    fun t3() {
        val postUserSystem = postUserRepository.findByUsername("system").getOrThrow()

        postUserAttrRepository.findBySubjectAndName(postUserSystem, "postsCount")
    }
}
