package co.com.nequi.franquicias.model.branch.gateways;

import co.com.nequi.franquicias.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> findById(String id);
    Mono<Branch> update(Branch branch);
}
