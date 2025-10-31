package co.com.nequi.franquicias.model.franchise.gateways;

import co.com.nequi.franquicias.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(String id);
    Mono<Franchise> update(Franchise franchise);
}
