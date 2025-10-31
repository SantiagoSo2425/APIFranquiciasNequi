package co.com.nequi.franquicias.mongo;

import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.franquicias.mongo.data.FranchiseData;
import co.com.nequi.franquicias.mongo.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MongoRepositoryAdapter implements FranchiseRepository {

    private final MongoDBRepository repository;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.just(franchise)
                .map(FranchiseMapper::toData)
                .flatMap(repository::save)
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id)
                .map(FranchiseMapper::toDomain);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return Mono.just(franchise)
                .map(FranchiseMapper::toData)
                .flatMap(repository::save)
                .map(FranchiseMapper::toDomain);
    }
}
