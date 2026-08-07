package com.back.standard.dto

import org.springframework.data.domain.Page

data class PageableDto(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val totalElements: Long,
    val totalPages: Int,
    val numberOfElements: Int,
    val sorted: Boolean,
)

data class PageDto<T : Any>(
    val content: List<T>,
    val pageable: PageableDto
) {
    constructor(page: Page<T>) : this(
        page.content,
        PageableDto(
            page.pageable.pageNumber + 1,
            page.pageable.pageSize,
            page.pageable.offset,
            page.totalElements,
            page.totalPages,
            page.numberOfElements,
            page.pageable.sort.isSorted
        )
    )
}
