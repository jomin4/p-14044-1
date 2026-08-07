package com.back.domain.post.post.service

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.domain.post.postComment.dto.PostCommentDto
import com.back.domain.post.postComment.entity.PostComment
import com.back.domain.post.postComment.event.PostCommentWrittenEvent
import com.back.domain.post.postUser.dto.PostUserDto
import com.back.domain.post.postUser.entity.PostUser
import com.back.standard.dto.PostSearchKeywordType1
import com.back.standard.dto.PostSearchSortType1
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun count(): Long {
        return postRepository.count()
    }

    fun write(author: PostUser, title: String, content: String): Post {
        val post = Post(author, title, content)

        author.incrementPostsCount()

        return postRepository.save(post)
    }

    fun findById(id: Int): Post? = postRepository.findById(id).orElse(null)

    fun modify(post: Post, title: String, content: String) {
        post.modify(title, content)
    }

    fun writeComment(author: PostUser, post: Post, content: String): PostComment {
        val postComment = post.addComment(author, content)

        postRepository.flush()

        publisher.publishEvent(
            PostCommentWrittenEvent(
                PostCommentDto(postComment),
                PostDto(post),
                PostUserDto(author)
            )
        )

        return postComment
    }

    fun deleteComment(post: Post, postComment: PostComment): Boolean {
        return post.deleteComment(postComment)
    }

    fun modifyComment(postComment: PostComment, content: String) {
        postComment.modify(content)
    }

    fun delete(post: Post) {
        post.author.decrementPostsCount()

        postRepository.delete(post)
    }

    fun findLatest(): Post? {
        return postRepository.findFirstByOrderByIdDesc()
    }

    fun findPagedByKw(
        kwType: PostSearchKeywordType1,
        kw: String,
        sort: PostSearchSortType1,
        page: Int,
        pageSize: Int
    ): Page<Post> =
        postRepository.findQPagedByKw(
            kwType,
            kw,
            PageRequest.of(
                page - 1,
                pageSize,
                sort.sortBy
            )
        )
}
