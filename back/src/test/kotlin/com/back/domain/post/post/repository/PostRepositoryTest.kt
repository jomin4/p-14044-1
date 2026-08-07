package com.back.domain.post.post.repository


import com.back.standard.dto.PostSearchKeywordType1
import com.back.standard.dto.PostSearchSortType1
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostRepositoryTest {
    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    @DisplayName("findQPagedByKw")
    fun t1() {
        val postPage = postRepository.findQPagedByKw(
            PostSearchKeywordType1.TITLE,
            "제목",
            PageRequest.of(
                0,
                10,
                PostSearchSortType1.ID.sortBy
            ),
        )

        val content = postPage.content

        assertThat(content).isNotEmpty
    }


    @Test
    @DisplayName("findQPagedByKw, kwType=PostSearchKeywordType1.AUTHOR_NAME")
    fun t2() {
        val postPage = postRepository.findQPagedByKw(
            PostSearchKeywordType1.AUTHOR_NAME,
            "유저",
            PageRequest.of(
                0,
                10,
                PostSearchSortType1.ID.sortBy
            ),
        )

        val content = postPage.content

        assertThat(content).isNotEmpty

        assertThat(content).allMatch { post ->
            post.author.name.contains("유저", ignoreCase = true)
        }
    }
}
