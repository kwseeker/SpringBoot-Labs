package top.kwseeker.service;

import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import top.kwseeker.po.Joke;
import top.kwseeker.repository.JokeRepository;

import java.util.List;

@Service
public class JokeService {

    @Resource
    private JokeRepository jokeRepository;

    public Joke saveJoke(Joke joke) {
        return jokeRepository.save(joke);
    }

    public void saveJokes(List<Joke> jokes) {
        jokeRepository.saveAll(jokes);
    }

    public void deleteJokes() {
        jokeRepository.deleteAll();
    }

    public List<Joke> findByTitle(String title) {
        return jokeRepository.findByTitle(title);
    }

    public List<Joke> findByTitleCustom(String title) {
        return jokeRepository.findByTitleCustom(title);
    }

    public Page<Joke> findByTitle(String title, Pageable pageable) {
        return jokeRepository.findByTitle(title, pageable);
    }

    public List<Joke> findByContent(String content) {
        return jokeRepository.findByContent(content);
    }

    public List<Joke> findByContentFuzzy(String content) {
        return jokeRepository.findByContentFuzzy(content);
    }

    public List<Joke> findByTitleAndContent(String title, String content) {
        return jokeRepository.findByTitleAndContent(title, content);
    }

    public List<Joke> findByTitleOrContent(String title, String content) {
        return jokeRepository.findByTitleOrContent(title, content);
    }

    public List<Joke> findByLikeCount(Integer likeCount) {
        return jokeRepository.findByLikeCount(likeCount);
    }

    public List<Joke> findByLikeCountBetween(Integer min, Integer max) {
        return jokeRepository.findByLikeCountBetween(min, max);
    }

    public List<Joke> findByLikeCountGreaterThan(Integer likeCount) {
        return jokeRepository.findByLikeCountGreaterThan(likeCount);
    }

    public List<Joke> findByLikeCountGreaterThanEqual(Integer likeCount) {
        return jokeRepository.findByLikeCountGreaterThanEqual(likeCount);
    }

    public List<Joke> findByLikeCountLessThan(Integer likeCount) {
        return jokeRepository.findByLikeCountLessThan(likeCount);
    }
}