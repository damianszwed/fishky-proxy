package com.github.damianszwed.fishky.flashcard.service.port;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface CommandQueryHandler {

  Mono<ServerResponse> handle(ServerRequest serverRequest);
}
