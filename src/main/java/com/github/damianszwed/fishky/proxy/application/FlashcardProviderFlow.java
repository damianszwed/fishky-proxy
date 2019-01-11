package com.github.damianszwed.fishky.proxy.application;

import com.github.damianszwed.fishky.proxy.port.flashcard.EventSource;
import com.github.damianszwed.fishky.proxy.port.flashcard.Flashcard;
import com.github.damianszwed.fishky.proxy.port.flashcard.FlashcardProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

public class FlashcardProviderFlow implements EventSource {

  private FlashcardProvider flashcardProvider;
  private final TopicProcessor<Flashcard> unicastProcessor;

  public FlashcardProviderFlow(FlashcardProvider flashcardProvider) {

    this.flashcardProvider = flashcardProvider;
    unicastProcessor = TopicProcessor.create();
  }

  void getAll() {
    Flux.fromStream(() -> flashcardProvider.getFlashcards("any").stream())
        .subscribe(unicastProcessor::onNext);
  }

  @Override
  public Flux<Flashcard> getFlux() {
    return unicastProcessor.publish().autoConnect();
  }
}