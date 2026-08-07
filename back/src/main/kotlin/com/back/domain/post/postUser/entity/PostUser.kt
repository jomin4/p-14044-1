package com.back.domain.post.postUser.entity

import com.back.domain.member.member.entity.BaseMember
import com.back.domain.member.member.entity.Member
import com.back.domain.post.postUser.repository.PostUserAttrRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "member")
class PostUser(
    id: Int,
    username: String,
    @field:Column(name = "nickname") var name: String
) : BaseMember(id, username) {

    companion object {
        lateinit var attrRepository: PostUserAttrRepository
    }

    @delegate:Transient
    private val postsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postsCount")
            ?: PostUserAttr(this, "postsCount", "0")
    }

    @delegate:Transient
    private val postCommentsCountAttr by lazy {
        attrRepository.findBySubjectAndName(this, "postCommentsCount")
            ?: PostUserAttr(this, "postCommentsCount", "0")
    }

    var postsCount
        get() = postsCountAttr.value.toInt()
        set(value) {
            postsCountAttr.value = value.toString()
            attrRepository.save(postsCountAttr)
        }

    var postCommentsCount
        get() = postCommentsCountAttr.value.toInt()
        set(value) {
            postCommentsCountAttr.value = value.toString()
            attrRepository.save(postCommentsCountAttr)
        }

    fun incrementPostsCount() {
        postsCount++
    }

    fun decrementPostsCount() {
        postsCount--
    }

    fun incrementPostCommentsCount() {
        postCommentsCount++
    }

    fun decrementPostCommentsCount() {
        postCommentsCount--
    }

    // 코프링에서 엔티티의 `by lazy` 필드가 제대로 작동하게 하려면
    // kotlin("plugin.jpa") 에 의해서 만들어지는 인자 없는 생성자로는 부족하다.
    // 귀찮지만 이렇게 직접 만들어야 한다.
    constructor() : this(0, "", "")

    constructor(member: Member) : this(
        member.id,
        member.username,
        member.nickname
    )
}
