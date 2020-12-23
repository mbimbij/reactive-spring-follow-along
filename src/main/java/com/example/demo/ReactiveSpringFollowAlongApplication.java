package com.example.demo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveSpringFollowAlongApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReactiveSpringFollowAlongApplication.class, args);
  }

}

@Component
@RequiredArgsConstructor
@Slf4j
class SampleDataInitializer {
  private final ReservationRepository reservationRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void ready() {
    Flux<Reservation> savedReservations = Flux.just("joseph", "josh", "madura", "mark", "olga", "spencer", "ria", "stéphane", "violetta")
        .map(name -> new Reservation(null, name))
        .flatMap(reservationRepository::save);

    reservationRepository
        .deleteAll()
        .thenMany(savedReservations)
        .thenMany(reservationRepository.findAll())
        .subscribe(reservation -> log.info(reservation.toString()));
  }
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
@ToString
class Reservation {
  @Id
  private String id;
  private String name;
}