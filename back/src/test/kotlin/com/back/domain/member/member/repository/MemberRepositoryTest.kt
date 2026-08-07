package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.MemberProxy
import com.back.standard.extensions.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberRepositoryTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("findById")
    fun t1() {
        val member = memberRepository.findById(1).get()

        assertThat(member.id).isEqualTo(1)
    }

    @Test
    @DisplayName("findQById")
    fun t2() {
        val member = memberRepository.findQById(1).getOrThrow()

        assertThat(member.id).isEqualTo(1)
    }

    @Test
    @DisplayName("findByUsername")
    fun t3() {
        val member = memberRepository.findByUsername("admin").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
    }

    @Test
    @DisplayName("findQByUsername")
    fun t4() {
        val member = memberRepository.findQByUsername("admin").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
    }

    @Test
    @DisplayName("findByIdIn")
    fun t5() {
        val members = memberRepository.findByIdIn(listOf(1, 2, 3))

        assertThat(members).isNotEmpty
        assertThat(members.map { it.id }).containsAnyOf(1, 2, 3)
    }

    @Test
    @DisplayName("findQByIdIn")
    fun t6() {
        val members = memberRepository.findQByIdIn(listOf(1, 2, 3))

        assertThat(members).isNotEmpty
        assertThat(members.map { it.id }).containsAnyOf(1, 2, 3)
    }

    @Test
    @DisplayName("findByUsernameAndNickname")
    fun t7() {
        val member = memberRepository.findByUsernameAndNickname("admin", "관리자").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
        assertThat(member.nickname).isEqualTo("관리자")
    }

    @Test
    @DisplayName("findQByUsernameAndNickname")
    fun t8() {
        val member = memberRepository.findQByUsernameAndNickname("admin", "관리자").getOrThrow()

        assertThat(member.username).isEqualTo("admin")
        assertThat(member.nickname).isEqualTo("관리자")
    }

    @Test
    @DisplayName("findByUsernameOrNickname")
    fun t9() {
        val members = memberRepository.findByUsernameOrNickname("admin", "유저1")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" || it.nickname == "유저1" }).isTrue
    }

    @Test
    @DisplayName("findQByUsernameOrNickname")
    fun t10() {
        val members = memberRepository.findQByUsernameOrNickname("admin", "유저1")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" || it.nickname == "유저1" }).isTrue
    }

    @Test
    @DisplayName("findCByUsernameAndEitherPasswordOrNickname")
    fun t11() {
        val members = memberRepository.findCByUsernameAndEitherPasswordOrNickname("admin", "wrong-password", "관리자")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" && (it.password == "wrong-password" || it.nickname == "관리자") }).isTrue
    }

    @Test
    @DisplayName("findQByUsernameAndPasswordOrNickname")
    fun t12() {
        val members = memberRepository.findQByUsernameAndEitherPasswordOrNickname("admin", "wrong-password", "관리자")

        assertThat(members).isNotEmpty
        assertThat(members.any { it.username == "admin" && (it.password == "wrong-password" || it.nickname == "관리자") }).isTrue
    }

    @Test
    @DisplayName("findByNicknameContaining")
    fun t13() {
        val members = memberRepository.findByNicknameContaining("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue
    }

    @Test
    @DisplayName("findQByNicknameContaining")
    fun t14() {
        val members = memberRepository.findQByNicknameContaining("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue
    }

    @Test
    @DisplayName("countByNicknameContaining")
    fun t15() {
        val count = memberRepository.countByNicknameContaining("유저")

        assertThat(count).isEqualTo(3)
    }

    @Test
    @DisplayName("countQByNicknameContaining")
    fun t16() {
        val count = memberRepository.countQByNicknameContaining("유저")

        assertThat(count).isEqualTo(3)
    }

    @Test
    @DisplayName("existsByNicknameContaining")
    fun t17() {
        val exists = memberRepository.existsByNicknameContaining("유저")

        assertThat(exists).isTrue
    }

    @Test
    @DisplayName("existsQByNicknameContaining")
    fun t18() {
        val exists = memberRepository.existsQByNicknameContaining("유저")

        assertThat(exists).isTrue
    }

    @Test
    @DisplayName("findByNicknameContaining with Pageable")
    fun t19() {
        val pageable = PageRequest.of(0, 2)
        val page = memberRepository.findByNicknameContaining("유저", pageable)

        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(3)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("findQByNicknameContaining with Pageable")
    fun t20() {
        val pageable = PageRequest.of(0, 2)
        val page = memberRepository.findQByNicknameContaining("유저", pageable)

        assertThat(page.content).hasSize(2)
        assertThat(page.totalElements).isEqualTo(3)
        assertThat(page.totalPages).isEqualTo(2)
    }

    @Test
    @DisplayName("findByNicknameContainingOrderByIdDesc")
    fun t21() {
        val members = memberRepository.findByNicknameContainingOrderByIdDesc("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue

        for (i in 0 until members.size - 1) {
            assertThat(members[i].id).isGreaterThan(members[i + 1].id)
        }
    }

    @Test
    @DisplayName("findQByNicknameContainingOrderByIdDesc")
    fun t22() {
        val members = memberRepository.findQByNicknameContainingOrderByIdDesc("유저")

        assertThat(members).isNotEmpty
        assertThat(members.all { it.nickname.contains("유저") }).isTrue

        for (i in 0 until members.size - 1) {
            assertThat(members[i].id).isGreaterThan(members[i + 1].id)
        }
    }

    @Test
    @DisplayName("findByUsernameContaining with Pageable")
    fun t23() {
        val pageable = PageRequest.of(
            0, 2,
            Sort.by("id").descending()
                .and(Sort.by("username").ascending())
                .and(Sort.by("nickname").descending())
        )
        val page = memberRepository.findByUsernameContaining("user", pageable)

        for (i in 0 until page.content.size - 1) {
            assertThat(page.content[i].id).isGreaterThan(page.content[i + 1].id)
        }
    }

    @Test
    @DisplayName("findQByUsernameContaining with Pageable")
    fun t24() {
        val pageable = PageRequest.of(
            0, 2,
            Sort.by("id").descending()
                .and(Sort.by("username").ascending())
                .and(Sort.by("nickname").descending())
        )
        val page = memberRepository.findQByUsernameContaining("user", pageable)

        for (i in 0 until page.content.size - 1) {
            assertThat(page.content[i].id).isGreaterThan(page.content[i + 1].id)
        }
    }

    @Test
    @DisplayName("findById twice, cached")
    fun t25() {
        memberRepository.findById(1) // SELECT
        memberRepository.findById(1) // CACHED
    }

    @Test
    @DisplayName("findByUsername twice, cached")
    fun t26() {
        memberRepository.findByUsername("user1") // SELECT
        memberRepository.findByUsername("user1") // CACHED
        memberRepository.findById(3) // CACHED
    }

    @Test
    @DisplayName("getReferenceById로 불러온 객체는 id 조회 이외의 필드 접근 시점에 SELECT 쿼리가 실행된다.")
    fun t27() {
        val member = memberRepository.getReferenceById(1)
        println(member.id)
        member.nickname = "시스템" // 쿼리 발생
        println(member.nickname)
    }

    @Test
    @DisplayName("MemberProxy")
    fun t28() {
        val realMember = memberRepository.getReferenceById(1)

        val member = MemberProxy(
            realMember,
            1,
            "system",
            "시스템",
        )

        // fulfill 필요없음
        assertThat(member.id).isEqualTo(1)
        assertThat(member.username).isEqualTo("system")
        assertThat(member.nickname).isEqualTo("시스템")
        assertThat(member.name).isEqualTo("시스템")
        assertThat(member.redirectToProfileImgUrlOrDefault).endsWith("/api/v1/members/1/redirectToProfileImg")
        assertThat(member.isAdmin).isEqualTo(true)
        assertThat(member.authorities).hasSize(1)
        assertThat(member.authoritiesAsStringList).containsExactly("ROLE_ADMIN")
        assertThat(member).isEqualTo(realMember)

        // fulfill 필요함
        assertThat(member.createDate).isNotNull
        assertThat(member.modifyDate).isNotNull
        assertThat(member.profileImgUrl).isBlank
        assertThat(member.apiKey).isNotNull
        assertThat(member.password).isNotNull
    }
}
