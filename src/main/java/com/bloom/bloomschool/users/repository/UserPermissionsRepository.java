package com.bloom.bloomschool.users.repository;

import com.bloom.bloomschool.users.entity.Permissions;
import com.bloom.bloomschool.users.entity.User;
import com.bloom.bloomschool.users.entity.UserPermissions;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserPermissionsRepository extends JpaRepository<UserPermissions, Long> {

    @Query(value = """
            SELECT * FROM bloom_school_user_permissions up WHERE up.uuid = :uuid
            """, nativeQuery = true)
    Optional<UserPermissions> findByUuid(@Param ("uuid") UUID uuid);

    @Query("SELECT up FROM UserPermissions up WHERE up.user.userId = :userId AND up.permission.permissionsId = :permissionsId")
    UserPermissions findByUserIdAndPermissionId(
            @Param("userId") Long userId,
            @Param("permissionsId") Long permissionsId
    );
    @Query(value = """
            select ps.uuid, ps.name, ps.description, ps.access_type, ms.uuid, up.override_type
            from bloom_school_user_permissions up
            left join bloom_school_permissions as ps on up.permission_id = ps.permissions_id
            left join bloom_school_modules as ms on ps.module_id = ms.modules_id
            where up.user_id = :userId
    """, nativeQuery = true)
    List<Object[]> findByUserPermissions(@Param("userId") Long userId);
}