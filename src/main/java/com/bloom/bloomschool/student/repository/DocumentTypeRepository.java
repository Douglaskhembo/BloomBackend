package com.bloom.bloomschool.student.repository;


import com.bloom.bloomschool.student.entity.DocumentType;
import com.bloom.bloomschool.student.enums.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, UUID> {
    Optional<DocumentType> findByUuid(UUID uuid);

    boolean existsByName(String name);

    @Query("SELECT dt FROM DocumentType dt " +
            "WHERE dt.documentCategory = :category AND dt.mandatory = true AND dt.active = true")
    List<DocumentType> getMandatory(@Param("category") DocumentCategory category);
}
