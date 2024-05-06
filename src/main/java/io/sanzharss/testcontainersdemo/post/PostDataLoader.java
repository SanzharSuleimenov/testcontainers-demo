package io.sanzharss.testcontainersdemo.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PostDataLoader implements CommandLineRunner {

  private final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
  private final PostRepository postRepository;
  private final ObjectMapper objectMapper;

  public PostDataLoader(PostRepository postRepository, ObjectMapper objectMapper) {
    this.postRepository = postRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run(String... args) throws Exception {
    if (postRepository.count() == 0) {
      String JSON_PATH = "/data/posts.json";
      log.info("Loading posts into database from JSON: {}", JSON_PATH);
      try (InputStream inputStream = TypeReference.class.getResourceAsStream(JSON_PATH)) {
        Posts response = objectMapper.readValue(inputStream, Posts.class);
        postRepository.saveAll(response.posts());
      } catch (IOException e) {
        throw new RuntimeException("Failed to read posts from JSON and load to database");
      }
    }
  }
}
