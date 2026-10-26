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

@Repository
public interface UserPermissionsRepository extends JpaRepository<UserPermissions, Long> {

    Optional<UserPermissions> findByUuid(UUID uuid);

    Optional<UserPermissions> findByUserAndPermission(User user, Permissions permission);

    @Query("SELECT up FROM UserPermissions up WHERE up.user.userId = :userId AND up.permission.permissionsId = :permissionsId")
    UserPermissions findByUserIdAndPermissionId(
            @Param("userId") Long userId,
            @Param("permissionsId") Long permissionsId
    );
}