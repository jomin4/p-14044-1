package com.back.domain.post.postUser.service

import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.repository.PostUserRepository
import org.springframework.stereotype.Service

@Service
class PostUserService(
    private val postUserRepository: PostUserRepository,
) {
    fun findByUsername(username: String): PostUser? = postUserRepository.findByUsername(username)
    fun getReferenceById(id: Int) = postUserRepository.getReferenceById(id)
}
