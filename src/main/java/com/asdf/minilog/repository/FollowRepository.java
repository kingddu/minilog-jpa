package com.asdf.minilog.repository;

import java.util.Optional;
import java.util.List;
import com.asdf.minilog.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerId(Long followerId);

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
