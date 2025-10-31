package co.com.nequi.franquicias.api;

import co.com.nequi.franquicias.api.dto.*;
import co.com.nequi.franquicias.model.branch.Branch;
import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.product.Product;
import co.com.nequi.franquicias.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.nequi.franquicias.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.nequi.franquicias.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.nequi.franquicias.usecase.gethigheststockproducts.GetHighestStockProductsUseCase;
import co.com.nequi.franquicias.usecase.removeproductfrombranch.RemoveProductFromBranchUseCase;
import co.com.nequi.franquicias.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.nequi.franquicias.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.nequi.franquicias.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.nequi.franquicias.usecase.updateproductstock.UpdateProductStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final RemoveProductFromBranchUseCase removeProductFromBranchUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetHighestStockProductsUseCase getHighestStockProductsUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .map(dto -> Franchise.builder()
                        .id(UUID.randomUUID().toString())
                        .name(dto.getName())
                        .build())
                .flatMap(createFranchiseUseCase::execute)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .map(dto -> Branch.builder()
                        .id(UUID.randomUUID().toString())
                        .name(dto.getName())
                        .build())
                .flatMap(branch -> addBranchToFranchiseUseCase.execute(franchiseId, branch))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> addProductToBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .map(dto -> Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name(dto.getName())
                        .stock(dto.getStock())
                        .build())
                .flatMap(product -> addProductToBranchUseCase.execute(franchiseId, branchId, product))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> removeProductFromBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return removeProductFromBranchUseCase.execute(franchiseId, branchId, productId)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> updateProductStockUseCase.execute(franchiseId, branchId, productId, dto.getStock()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> getHighestStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return getHighestStockProductsUseCase.execute(franchiseId)
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateFranchiseNameUseCase.execute(franchiseId, dto.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateBranchNameUseCase.execute(franchiseId, branchId, dto.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateProductNameUseCase.execute(franchiseId, branchId, productId, dto.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
