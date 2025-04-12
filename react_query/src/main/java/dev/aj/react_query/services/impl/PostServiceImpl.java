package dev.aj.react_query.services.impl;

import dev.aj.react_query.domain.entities.Post;
import dev.aj.react_query.repositories.PostRepository;
import dev.aj.react_query.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> saveAll(List<Post> posts) {
        return postRepository.saveAll(posts);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Long count() {
        return postRepository.count();
    }


}
