package dev.aj.react_query.services;

import dev.aj.react_query.domain.entities.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    Post save(Post post);

    List<Post> saveAll(List<Post> posts);

    List<Post> getAllPosts();

    Long count();
}
