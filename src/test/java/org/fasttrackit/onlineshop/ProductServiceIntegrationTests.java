package org.fasttrackit.onlineshop;

import org.fasttrackit.onlineshop.domain.Product;
import org.fasttrackit.onlineshop.exception.ResourceNotFoundException;
import org.fasttrackit.onlineshop.service.ProductService;
import org.fasttrackit.onlineshop.steps.ProductTestSteps;
import org.fasttrackit.onlineshop.transfer.product.GetProductsRequest;
import org.fasttrackit.onlineshop.transfer.product.SaveProductRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class ProductServiceIntegrationTests {

    //field injection
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductTestSteps productTestSteps;

    @Test
    void createProduct_whenValidRequest_thenReturnCreatedProduct() {
        productTestSteps.createProduct();
    }


    @Test
    void createProduct_whenMissingMandatoryProperties_thenThrowException(){
        SaveProductRequest request = new SaveProductRequest();

        try {
            productService.createProduct(request);
        } catch (Exception e) {
            assertThat("Unexpected exception thrown", e instanceof ConstraintViolationException);
        }

    }

    void getProduct_whenExistingProduct_thenReturnProduct() {
        Product product = productTestSteps.createProduct();

        Product response = productService.getProduct(product.getId());

        assertThat(response, notNullValue());
        assertThat(response.getId(), is(product.getId()));
        assertThat(response.getId(), greaterThan(0L));
        assertThat(response.getName(), is(product.getName()));
        assertThat(response.getPrice(), is(product.getPrice()));
        assertThat(response.getQuantity(), is(product.getQuantity()));
        assertThat(response.getDescription(), is(product.getDescription()));
        assertThat(response.getImageUrl(), is(product.getImageUrl()));

    }

    @Test
    void getProduct_whenNonExistingProduct_then_throwResourceNotFoundException() {
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> productService.getProduct(0));
    }

    @Test
    void getProducts_whenOneExistingProduct_thenReturnPageOfOneProduct(){
        Product product = productTestSteps.createProduct();

        Page<Product> productsPage = productService.getProducts(new GetProductsRequest(), PageRequest.of(0, 1000));

        assertThat(productsPage, notNullValue());
        assertThat(productsPage.getTotalElements(), greaterThanOrEqualTo(1L));

        for(Product productEntity : productsPage.getContent()){
            assertThat(productsPage.getContent(), contains(product));
        }
    }

    void updateProduct_whenValidRequest_thenReturnUpdatedProduct(){
        Product product = productTestSteps.createProduct();

        SaveProductRequest request = new SaveProductRequest();

        request.setName(product.getName() + "Updated");
        request.setPrice(product.getPrice() + 10);
        request.setQuantity(product.getQuantity() + 10);

        Product updatedProduct = productService.updateProduct(product.getId(), request);

        assertThat(updatedProduct, notNullValue());
        assertThat(updatedProduct.getId(), is(product.getId()));
        assertThat(updatedProduct.getName(), is(request.getName()));
        assertThat(updatedProduct.getPrice(), is(request.getPrice()));
        assertThat(updatedProduct.getQuantity(), is(request.getQuantity()));

    }

    void deleteProduct_whenExistingProduct_thenProductDoesNotExistAnymore() {
        Product product = productTestSteps.createProduct();

        productService.deleteProduct(product.getId());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> productService.getProduct(product.getId()));
    }



}
