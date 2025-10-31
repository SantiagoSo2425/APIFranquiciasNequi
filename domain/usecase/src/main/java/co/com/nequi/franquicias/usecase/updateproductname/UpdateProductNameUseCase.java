package co.com.nequi.franquicias.usecase.updateproductname;

import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> execute(String franchiseId, String branchId, String productId, String newName) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                .map(franchise -> {
                    franchise.getBranches().stream()
                            .filter(branch -> branch.getId().equals(branchId))
                            .findFirst()
                            .ifPresentOrElse(
                                    branch -> branch.getProducts().stream()
                                            .filter(product -> product.getId().equals(productId))
                                            .findFirst()
                                            .ifPresentOrElse(
                                                    product -> product.setName(newName),
                                                    () -> {
                                                        throw new RuntimeException("Product not found with id: " + productId);
                                                    }
                                            ),
                                    () -> {
                                        throw new RuntimeException("Branch not found with id: " + branchId);
                                    }
                            );
                    return franchise;
                })
                .flatMap(franchiseRepository::update);
    }
}
