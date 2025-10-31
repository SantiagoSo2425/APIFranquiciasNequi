package co.com.nequi.franquicias.usecase.gethigheststockproducts;

import co.com.nequi.franquicias.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@RequiredArgsConstructor
public class GetHighestStockProductsUseCase {
    private final FranchiseRepository franchiseRepository;

    public Flux<ProductWithBranch> execute(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found with id: " + franchiseId)))
                .flatMapMany(franchise ->
                    Flux.fromIterable(franchise.getBranches())
                            .mapNotNull(branch ->
                                branch.getProducts().stream()
                                        .max(Comparator.comparing(product -> product.getStock()))
                                        .map(product -> ProductWithBranch.builder()
                                                .productId(product.getId())
                                                .productName(product.getName())
                                                .stock(product.getStock())
                                                .branchId(branch.getId())
                                                .branchName(branch.getName())
                                                .build())
                                        .orElse(null)
                            )
                );
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class ProductWithBranch {
        private String productId;
        private String productName;
        private Integer stock;
        private String branchId;
        private String branchName;
    }
}
