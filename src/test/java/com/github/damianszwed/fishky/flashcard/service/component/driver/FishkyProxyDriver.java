package com.github.damianszwed.fishky.flashcard.service.component.driver;

import static org.junit.jupiter.api.Assertions.fail;

import com.github.damianszwed.fishky.flashcard.service.port.flashcard.EventSource;
import com.github.damianszwed.fishky.flashcard.service.port.flashcard.FlashcardFolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

public class FishkyProxyDriver {

  private final WebTestClient webTestClient;
  private final EventSource<FlashcardFolder> flashcardFoldersEventSource;
  private final Student student;

  FishkyProxyDriver(WebTestClient webTestClient,
      EventSource<FlashcardFolder> flashcardFoldersEventSource) {
    this.webTestClient = webTestClient;
    this.flashcardFoldersEventSource = flashcardFoldersEventSource;
    this.student = createStudent();
  }

  public void with(TestCode test) {
    try {
      test.run(this);
    } catch (Exception e) {
      fail(e);
    }
  }

  public Given when() {
    return () -> student;
  }

  public Given then() {
    return () -> student;
  }

  private Student createStudent() {
    return new StudentImpl();
  }

  public Given given() {
    return () -> student;
  }

  private class StudentImpl implements Student {

    private ResponseSpec response;
    private Step step;

    @Override
    public void queriesForFlashcardFolders() {
      response = webTestClient
          .get().uri("/flashcardFolders")
          .accept(MediaType.APPLICATION_JSON)
          .exchange();
    }

    @Override
    public void receivesFlashcardFolders(String flashcardFolders) {
      response.expectBody().json(flashcardFolders);
    }

    @Override
    public void savesFlashcardInFolder(String newFlashcard, String flashcardFolderId) {
      webTestClient
          .post()
          .uri("/flashcardFolders/{flashcardFolderId}/flashcards", flashcardFolderId)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(newFlashcard)
          .exchange().expectStatus().isAccepted();
    }

    @Override
    public void modifiesFlashcardInFolder(String modifiedFlashcard, String flashcardFolderId) {
      webTestClient
          .put()
          .uri("/flashcardFolders/{flashcardFolderId}/flashcards", flashcardFolderId)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(modifiedFlashcard)
          .exchange().expectStatus().isAccepted();
    }

    @Override
    public void deletesFlashcardFromFolder(String flashcardId, String flashcardFolderId) {
      webTestClient
          .delete()
          .uri("/flashcardFolders/{flashcardFolderId}/flashcards/{flashcardId}",
              flashcardFolderId,
              flashcardId)
          .accept(MediaType.APPLICATION_JSON)
          .exchange().expectStatus().isAccepted();
    }

    @Override
    public void commandsForAllFlashcardFolders() {
      webTestClient
          .get().uri("/getAllFlashcardFoldersCommand")
          .exchange().expectStatus().isAccepted();
    }

    @Override
    public void isListeningOnFlashcardFolders() {
      step = StepVerifier.create(flashcardFoldersEventSource.getFlux("user1@example.com"))
          .expectNextCount(1);
    }

    @Override
    public void isNotifiedAboutAllFlashcardFolders() {
      step.thenCancel().verify();
    }
  }
}
