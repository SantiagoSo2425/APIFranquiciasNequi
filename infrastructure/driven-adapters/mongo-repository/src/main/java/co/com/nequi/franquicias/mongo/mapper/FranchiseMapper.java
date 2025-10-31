package co.com.nequi.franquicias.mongo.mapper;

import co.com.nequi.franquicias.model.branch.Branch;
import co.com.nequi.franquicias.model.franchise.Franchise;
import co.com.nequi.franquicias.model.product.Product;
import co.com.nequi.franquicias.mongo.data.BranchData;
import co.com.nequi.franquicias.mongo.data.FranchiseData;
import co.com.nequi.franquicias.mongo.data.ProductData;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FranchiseMapper {

    public static FranchiseData toData(Franchise franchise) {
        if (franchise == null) {
            return null;
        }
        return FranchiseData.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .branches(franchise.getBranches() != null
                        ? franchise.getBranches().stream()
                                .map(FranchiseMapper::toBranchData)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public static Franchise toDomain(FranchiseData data) {
        if (data == null) {
            return null;
        }
        return Franchise.builder()
                .id(data.getId())
                .name(data.getName())
                .branches(data.getBranches() != null
                        ? data.getBranches().stream()
                                .map(FranchiseMapper::toBranchDomain)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    private static BranchData toBranchData(Branch branch) {
        if (branch == null) {
            return null;
        }
        return BranchData.builder()
                .id(branch.getId())
                .name(branch.getName())
                .products(branch.getProducts() != null
                        ? branch.getProducts().stream()
                                .map(FranchiseMapper::toProductData)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    private static Branch toBranchDomain(BranchData data) {
        if (data == null) {
            return null;
        }
        return Branch.builder()
                .id(data.getId())
                .name(data.getName())
                .products(data.getProducts() != null
                        ? data.getProducts().stream()
                                .map(FranchiseMapper::toProductDomain)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    private static ProductData toProductData(Product product) {
        if (product == null) {
            return null;
        }
        return ProductData.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .build();
    }

    private static Product toProductDomain(ProductData data) {
        if (data == null) {
            return null;
        }
        return Product.builder()
                .id(data.getId())
                .name(data.getName())
                .stock(data.getStock())
                .build();
    }
}

