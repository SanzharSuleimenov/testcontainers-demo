package io.sanzharss.testcontainersdemo.post;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

  @Autowired
  PostRepository postRepository;

  @Test
  void assertThatConnectionEstablished() {
    assertThat(postgres.isCreated()).isTrue();
    assertThat(postgres.isRunning()).isTrue();
  }

  @BeforeEach
  void setUp() {
    List<Post> posts = List.of(
        new Post(1, 1, "Hello, World!", "This is my first post", null),
        new Post(2, 1, "Second post", "This is my second post", null)
    );
    postRepository.saveAll(posts);
  }

  @Test
  void whenFindByTitle_thenFindPost() {
    Post post = postRepository.findByTitle("Hello, World!");

    assertThat(post).isNotNull();
  }
}
