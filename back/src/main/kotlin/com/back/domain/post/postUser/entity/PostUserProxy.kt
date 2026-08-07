package com.back.domain.post.postUser.entity

class PostUserProxy(
    private val real: PostUser,
    id: Int,
    username: String,
    name: String
) : PostUser(id, username, name) {
    override var createDate
        get() = real.createDate
        set(value) {
            real.createDate = value
        }

    override var modifyDate
        get() = real.modifyDate
        set(value) {
            real.modifyDate = value
        }

    override var postsCount
        get() = real.postsCount
        set(value) {
            real.postsCount = value
        }

    override var postCommentsCount
        get() = real.postCommentsCount
        set(value) {
            real.postCommentsCount = value
        }
}
