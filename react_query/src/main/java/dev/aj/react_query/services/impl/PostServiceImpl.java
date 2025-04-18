package dev.aj.react_query.services.impl;

import dev.aj.react_query.domain.entities.Post;
import dev.aj.react_query.repositories.PostRepository;
import dev.aj.react_query.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Override
    public Post getById(Long id) {
        return postRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
    }

    @Override
    public Page<Post> getAllPostsInAPage(PageRequest pageRequest) {
        return postRepository.findAllPostsByPage(pageRequest);
    }

    @Override
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }


}
