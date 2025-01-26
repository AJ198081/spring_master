package dev.aj.blog_server.services;

import dev.aj.blog_server.domain.entities.Post;
import dev.aj.blog_server.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post save(Post post) {
      return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post Id: %d not found".formatted(postId)));
    }

}
