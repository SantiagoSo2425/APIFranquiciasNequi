package co.com.nequi.franquicias.usecase.addproducttobranch;

import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.franquicias.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String franchiseId, String branchId, Product product) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                .map(franchise -> {
                    franchise.getBranches().stream()
                            .filter(branch -> branch.getId().equals(branchId))
                            .findFirst()
                            .ifPresentOrElse(
                                    branch -> {
                                        if (branch.getProducts() == null) {
                                            branch.setProducts(new ArrayList<>());
                                        }
                                        branch.getProducts().add(product);
                                    },
                                    () -> {
                                        throw new RuntimeException("Branch not found with id: " + branchId);
                                    }
                            );
                    return franchise;
                })
                .flatMap(franchiseRepository::update);
    }
}
