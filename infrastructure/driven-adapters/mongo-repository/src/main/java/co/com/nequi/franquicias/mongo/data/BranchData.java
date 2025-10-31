package co.com.nequi.franquicias.mongo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchData {
    private String id;
    private String name;
    @Builder.Default
    private List<ProductData> products = new ArrayList<>();
}

