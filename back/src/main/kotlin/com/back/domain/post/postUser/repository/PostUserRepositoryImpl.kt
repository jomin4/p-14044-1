package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUser
import jakarta.persistence.EntityManager
import org.hibernate.KeyType
import org.hibernate.Session

class PostUserRepositoryImpl(
    private val entityManager: EntityManager,
) : PostUserRepositoryCustom {
    override fun findByUsername(username: String): PostUser? {
        return entityManager.unwrap(Session::class.java)
            .find(PostUser::class.java, username, KeyType.NATURAL)
    }
}
