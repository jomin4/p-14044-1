package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.QMember
import com.back.standard.dto.MemberSearchKeywordType1
import com.back.standard.util.QueryDslUtil
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.hibernate.KeyType
import org.hibernate.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val entityManager: EntityManager,
) : MemberRepositoryCustom {
    override fun findByUsername(username: String): Member? {
        return entityManager.unwrap(Session::class.java)
            .find(Member::class.java, username, KeyType.NATURAL)
    }

    override fun findQById(id: Int): Member? {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(member.id.eq(id))
            .fetchOne()
    }

    override fun findQByUsername(username: String): Member? {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne()
    }

    override fun findQByIdIn(ids: List<Int>): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(member.id.`in`(ids))
            .fetch()
    }

    override fun findQByUsernameAndNickname(username: String, nickname: String): Member? {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(member.nickname.eq(nickname))
            )
            .fetchOne()
    }

    override fun findQByUsernameOrNickname(username: String, nickname: String): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .or(member.nickname.eq(nickname))
            )
            .fetch()
    }

    override fun findQByUsernameAndEitherPasswordOrNickname(
        username: String,
        password: String?,
        nickname: String?
    ): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(
                        member.password.eq(password)
                            .or(member.nickname.eq(nickname))
                    )
            )
            .fetch()
    }

    override fun findQByNicknameContaining(nickname: String): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .fetch()
    }

    override fun countQByNicknameContaining(nickname: String): Long {
        val member = QMember.member

        return queryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchOne() ?: 0L
    }

    override fun existsQByNicknameContaining(nickname: String): Boolean {
        val member = QMember.member

        return queryFactory
            .selectOne()
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchFirst() != null
    }

    override fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member> {
        val member = QMember.member

        val results = queryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalQuery = queryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }

    override fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member> {
        val member = QMember.member

        return queryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .orderBy(member.id.desc())
            .fetch()
    }

    override fun findQByUsernameContaining(username: String, pageable: Pageable): Page<Member> {
        val member = QMember.member

        val query = queryFactory
            .selectFrom(member)
            .where(member.username.contains(username))

        // Apply sorting
        QueryDslUtil.applySorting(query, pageable) { property ->
            when (property) {
                "id" -> member.id
                "username" -> member.username
                "nickname" -> member.nickname
                else -> null
            }
        }

        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalQuery = queryFactory
            .select(member.count())
            .from(member)
            .where(member.username.contains(username))

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }

    override fun findQPagedByKw(
        kwType: MemberSearchKeywordType1,
        kw: String,
        pageable: Pageable
    ): Page<Member> {
        val member = QMember.member

        // 조건 빌더 생성
        val builder = com.querydsl.core.BooleanBuilder()

        if (kw.isNotBlank()) {
            when (kwType) {
                MemberSearchKeywordType1.USERNAME -> builder.and(member.username.containsIgnoreCase(kw))
                MemberSearchKeywordType1.NICKNAME -> builder.and(member.nickname.containsIgnoreCase(kw))
                MemberSearchKeywordType1.ALL ->
                    builder.and(
                        member.username.containsIgnoreCase(kw)
                            .or(member.nickname.containsIgnoreCase(kw))
                    )
            }
        }

        // 기본 query 생성
        val query = queryFactory
            .selectFrom(member)
            .where(builder)

        // pageable 정렬 조건 적용
        QueryDslUtil.applySorting(query, pageable) { property ->
            when (property) {
                "id" -> member.id
                "username" -> member.username
                "nickname" -> member.nickname
                else -> null
            }
        }

        // 페이징 데이터 조회
        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 카운트 쿼리 (조건 동일하게 적용)
        val totalQuery = queryFactory
            .select(member.count())
            .from(member)
            .where(builder)

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }
}
