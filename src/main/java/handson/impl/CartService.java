package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartAddDiscountCodeActionBuilder;
import com.commercetools.api.models.cart.CartAddLineItemActionBuilder;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.cart.CartRecalculateActionBuilder;
import com.commercetools.api.models.cart.CartSetShippingMethodActionBuilder;
import com.commercetools.api.models.cart.CartUpdateAction;
import com.commercetools.api.models.cart.CartUpdateBuilder;
import com.commercetools.api.models.cart.InventoryMode;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.channel.ChannelResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class CartService {

    ApiRoot apiRoot;
    String projectKey;

    public CartService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }


    /**
     * Creates a cart for the given customer.
     *
     * @return the customer creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<Cart>> createCart(final ApiHttpResponse<Customer> customerApiHttpResponse) {
        Customer customer = customerApiHttpResponse.getBody();
        Address address1 = customer.getAddresses().stream()
                .filter(address -> address.getId().equals(customer.getDefaultBillingAddressId()))
                .findFirst().get();
        return
                apiRoot.withProjectKey(projectKey)
                        .carts()
                        .post(CartDraftBuilder.of()
                                .currency("USD")
                                .deleteDaysAfterLastModification(90L)
                                .customerEmail(customer.getEmail())
                                .customerId(customer.getId())
                                .country(address1.getCountry())
                                .shippingAddress(address1)
                                .inventoryMode(InventoryMode.TRACK_ONLY)
                                .build()
                        ).execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .post(
                                CartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90L)
                                        .anonymousId("an" + System.nanoTime())
                                        .country("DE")
                                        .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final Channel channel,
            final String... skus) {

        Cart cart = cartApiHttpResponse.getBody();

        List<CartUpdateAction> cartAddLineItemActions = Stream.of(skus).map(sku -> CartAddLineItemActionBuilder.of()
                .sku(sku)
                .quantity(2L)
                .supplyChannel(ChannelResourceIdentifierBuilder.of().id(channel.getId()).build())
                .build()).collect(Collectors.toList());

        return apiRoot.withProjectKey(projectKey)
                .carts()
                .withId(cart.getId())
                .post(CartUpdateBuilder.of()
                        .version(cart.getVersion())
                        .actions(cartAddLineItemActions)
                        .build())
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(final ApiHttpResponse<Cart> cartApiHttpResponse, final String code) {
        Cart cart = cartApiHttpResponse.getBody();
        return apiRoot.withProjectKey(projectKey)
                .carts().withId(cart.getId())
                .post(CartUpdateBuilder.of()
                        .version(cart.getVersion())
                        .actions(CartAddDiscountCodeActionBuilder.of()
                                .code(code)
                                .build())
                        .build()).execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse) {
        Cart cart = cartApiHttpResponse.getBody();
        return apiRoot.withProjectKey(projectKey)
                .carts().withId(cart.getId())
                .post(CartUpdateBuilder.of().version(cart.getVersion())
                        .actions(CartRecalculateActionBuilder.of().build())
                        .build()).execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse) {
        Cart cart = cartApiHttpResponse.getBody();
        ShippingMethod shippingMethod = apiRoot.withProjectKey(projectKey)
                .shippingMethods()
                .matchingCart().get().withCartId(cart.getId())
                .executeBlocking().getBody().getResults().get(0);
        return apiRoot.withProjectKey(projectKey)
                .carts().withId(cart.getId())
                .post(CartUpdateBuilder.of()
                        .version(cart.getVersion())
                        .actions(CartSetShippingMethodActionBuilder.of()
                                .shippingMethod(ShippingMethodResourceIdentifierBuilder.of().id(shippingMethod.getId()).build())
                                .build())
                        .build()).execute();
    }
}
