package co.com.nequi.franquicias.model.product.gateways;

import co.com.nequi.franquicias.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> findById(String id);
    Flux<Product> findByFranchiseIdWithMaxStockPerBranch(String franchiseId);
}
