package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import com.back.standard.dto.PostSearchKeywordType1
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PostRepositoryCustom {
    fun findQPagedByKw(kwType: PostSearchKeywordType1, kw: String, pageable: Pageable): Page<Post>
}
