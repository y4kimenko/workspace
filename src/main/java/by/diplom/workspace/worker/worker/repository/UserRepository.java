package by.diplom.workspace.worker.worker.repository;


import by.diplom.workspace.worker.worker.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.emails WHERE u.id = :id")
    Optional<User> findByIdWithEmails(@Param("id") UUID id);

    Optional<User> findByNickname(String nickname);

    @Query("""
            SELECT u FROM User u
            JOIN u.emails e
            WHERE e.email = :email
            AND e.verified = true
            """)
    Optional<User> findByVerifiedEmail(@Param("email") String email);

    // Обнуляем departmentPosition у всех пользователей с данной связкой
    @Modifying
    @Query("UPDATE User u SET u.departmentPosition = null WHERE u.departmentPosition.id = :dpId")
    void clearDepartmentPositionByDepartmentPositionId(@Param("dpId") Long dpId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.bio = :bio WHERE u.id = :id")
    void updateBio(@Param("id") UUID id, @Param("bio") String bio);

    boolean existsUserByNickname(String nickname);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = """
                UPDATE users
                SET user_type = :userType,
                    group_manager_id = CASE
                        WHEN :userType = 'GROUP_MANAGER' THEN NULL
                        ELSE group_manager_id
                    END
                WHERE id = :id
                """,
            nativeQuery = true
    )
    void updateUserType(
            @Param("id") UUID id,
            @Param("userType") String userType
    );
}
