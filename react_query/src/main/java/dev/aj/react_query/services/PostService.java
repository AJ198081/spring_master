package dev.aj.react_query.services;

import dev.aj.react_query.domain.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    Post save(Post post);

    List<Post> saveAll(List<Post> posts);

    List<Post> getAllPosts();

    Long count();

    Post getById(Long id);

    Page<Post> getAllPostsInAPage(PageRequest pageRequest);

    void deletePostById(Long id);
}
