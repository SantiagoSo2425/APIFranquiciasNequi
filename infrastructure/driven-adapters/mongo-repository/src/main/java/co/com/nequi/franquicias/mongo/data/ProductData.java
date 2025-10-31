package co.com.nequi.franquicias.mongo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductData {
    private String id;
    private String name;
    private Integer stock;
}
