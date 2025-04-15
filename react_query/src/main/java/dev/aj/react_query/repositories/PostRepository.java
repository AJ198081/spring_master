package dev.aj.react_query.repositories;

import dev.aj.react_query.domain.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p")
    Page<Post> findAllPostsByPage(Pageable pageable);

}
