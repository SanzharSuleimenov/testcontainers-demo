package io.sanzharss.testcontainersdemo.post;

import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<Post, Integer> {

  Post findByTitle(String title);
}
