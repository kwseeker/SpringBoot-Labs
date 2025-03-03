package top.kwseeker.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.kwseeker.po.Joke;
import top.kwseeker.service.JokeService;

import java.util.List;

@RestController
@RequestMapping("/jokes")
public class JokeController {
    @Resource
    private JokeService jokeService;

    @PostMapping
    public Joke createJoke(@RequestBody Joke joke) {
        return jokeService.saveJoke(joke);
    }

    @GetMapping("/search/title")
    public List<Joke> searchByTitle(@RequestParam String title) {
        return jokeService.findByTitle(title);
    }

    @GetMapping("/search/content")
    public List<Joke> searchByContent(@RequestParam String content) {
        return jokeService.findByContent(content);
    }
}