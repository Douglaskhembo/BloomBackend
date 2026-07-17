package com.bloom.bloomschool.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtil {

    private PageUtil() {}

    public static Pageable of(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(Math.max(page, 0), Math.min(size, 100), sort);
    }

    public static Pageable of(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(size, 100));
    }
}
