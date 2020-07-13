package org.fasttrackit.onlineshop.transfer.cart;

import org.fasttrackit.onlineshop.domain.Product;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AddProductsToCartRequest {

    @NotNull
    private List<Long> productIds;
}
