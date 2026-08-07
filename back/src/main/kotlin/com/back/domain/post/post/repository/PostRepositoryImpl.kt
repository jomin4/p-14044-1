package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.entity.QPost
import com.back.standard.dto.PostSearchKeywordType1
import com.back.standard.util.QueryDslUtil
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class PostRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostRepositoryCustom {
    override fun findQPagedByKw(kwType: PostSearchKeywordType1, kw: String, pageable: Pageable): Page<Post> {
        val post = QPost.post

        val builder = com.querydsl.core.BooleanBuilder()

        if (kw.isNotBlank()) {
            when (kwType) {
                PostSearchKeywordType1.TITLE -> builder.and(post.title.containsIgnoreCase(kw))
                PostSearchKeywordType1.CONTENT -> builder.and(post.body.content.containsIgnoreCase(kw))
                PostSearchKeywordType1.AUTHOR_NAME -> builder.and(post.author.name.containsIgnoreCase(kw))
                PostSearchKeywordType1.ALL ->
                    builder.and(
                        post.title.containsIgnoreCase(kw)
                            .or(post.body.content.containsIgnoreCase(kw))
                            .or(post.author.name.containsIgnoreCase(kw))
                    )
            }
        }

        val query = queryFactory
            .selectFrom(post)
            .where(builder)

        QueryDslUtil.applySorting(query, pageable) { property ->
            when (property) {
                "id" -> post.id
                "authorName" -> post.author.name
                else -> null
            }
        }

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalQuery = queryFactory
            .select(post.count())
            .from(post)
            .where(builder)

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }
}
