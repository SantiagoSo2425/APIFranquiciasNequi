package co.com.nequi.franquicias.usecase.addbranchtofranchise;

import co.com.nequi.franquicias.model.branch.Branch;
import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                .map(franchise -> {
                    if (franchise.getBranches() == null) {
                        franchise.setBranches(new ArrayList<>());
                    }
                    franchise.getBranches().add(branch);
                    return franchise;
                })
                .flatMap(franchiseRepository::update);
    }
}
