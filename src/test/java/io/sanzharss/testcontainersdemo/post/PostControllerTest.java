package io.sanzharss.testcontainersdemo.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

  @Autowired
  TestRestTemplate restTemplate;

  @Test
  void whenFindAll_thenReturnAllPosts() {
    // call rest api /api/posts
    Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
    assertThat(posts.length).isEqualTo(100);
  }

  @Test
  void whenFindById_thenReturnPost() {
    ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null,
        Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void whenFindById_thenReturnNotFound() {
    ResponseEntity<Post> response = restTemplate.exchange("/api/posts/999", HttpMethod.GET, null,
        Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @Rollback
  void whenCreatePost_thenReturnCreatedPost() {
    Post post = new Post(101, 1, "101'th post", "Another helpful post", null);
    ResponseEntity<Post> response = restTemplate.postForEntity("/api/posts", post, Post.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(101);
    assertThat(response.getBody().title()).isEqualTo("101'th post");
    assertThat(response.getBody().userId()).isEqualTo(1);
    assertThat(response.getBody().body()).isEqualTo("Another helpful post");
  }

  @Test
  void whenCreate_thenReturnBadRequest() {
    Post post = new Post(101, 1, "", "", null);
    ResponseEntity<Post> response = restTemplate.postForEntity("/api/posts", post, Post.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @Rollback
  void whenUpdate_thenReturnUpdatedPost() {
    ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null,
        Post.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    Post existingPost = response.getBody();
    Post updatedPost = new Post(existingPost.id(), existingPost.userId(), "Brand new title",
        "Epoint had written", existingPost.version());

    restTemplate.put("/api/posts/1", updatedPost);
    ResponseEntity<Post> updatedResponse = restTemplate.exchange("/api/posts/1", HttpMethod.GET,
        null,
        Post.class);
    assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(updatedResponse.getBody()).isNotNull();
    assertThat(updatedResponse.getBody().title()).isEqualTo("Brand new title");
    assertThat(updatedResponse.getBody().userId()).isEqualTo(1);
    assertThat(updatedResponse.getBody().body()).isEqualTo("Epoint had written");
  }

  @Test
  @Rollback
  void whenDelete_thenReturnNoContent() {
    ResponseEntity<Void> response = restTemplate.exchange("/api/posts/1", HttpMethod.DELETE, null,
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
