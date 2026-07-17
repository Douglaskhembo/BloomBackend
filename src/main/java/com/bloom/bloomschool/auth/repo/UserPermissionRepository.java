package com.bloom.bloomschool.auth.repo;

import com.bloom.bloomschool.auth.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Query("SELECT up FROM UserPermission up WHERE up.user.id = :userId AND up.permission.id = :permissionId")
    UserPermission findByUserIdAndPermissionId(@Param("userId") Long userId, @Param("permissionId") Long permissionId);

    @Modifying
    @Query("DELETE FROM UserPermission up WHERE up.user.id = :userId AND up.permission.id = :permissionId")
    void deleteByUserIdAndPermissionId(@Param("userId") Long userId, @Param("permissionId") Long permissionId);

    @Query("""
        SELECT up.permission.uuid, up.permission.name, up.permission.permDesc,
               up.permission.accessType, up.permission.module.uuid, up.overrideType
        FROM UserPermission up
        WHERE up.user.id = :userId
    """)
    List<Object[]> findUserPermissions(@Param("userId") Long userId);
}
