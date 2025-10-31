package co.com.nequi.franquicias.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String BASE_PATH = "/api/franchises";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(BASE_PATH), handler::createFranchise)
                .andRoute(POST(BASE_PATH + "/{franchiseId}/branches"), handler::addBranchToFranchise)
                .andRoute(POST(BASE_PATH + "/{franchiseId}/branches/{branchId}/products"), handler::addProductToBranch)
                .andRoute(DELETE(BASE_PATH + "/{franchiseId}/branches/{branchId}/products/{productId}"), handler::removeProductFromBranch)
                .andRoute(PATCH(BASE_PATH + "/{franchiseId}/branches/{branchId}/products/{productId}/stock"), handler::updateProductStock)
                .andRoute(GET(BASE_PATH + "/{franchiseId}/highest-stock-products"), handler::getHighestStockProducts)
                .andRoute(PATCH(BASE_PATH + "/{franchiseId}/name"), handler::updateFranchiseName)
                .andRoute(PATCH(BASE_PATH + "/{franchiseId}/branches/{branchId}/name"), handler::updateBranchName)
                .andRoute(PATCH(BASE_PATH + "/{franchiseId}/branches/{branchId}/products/{productId}/name"), handler::updateProductName);
    }
}
