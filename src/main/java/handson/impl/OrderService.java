package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderChangeOrderStateActionBuilder;
import com.commercetools.api.models.order.OrderFromCartDraftBuilder;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.order.OrderTransitionStateActionBuilder;
import com.commercetools.api.models.order.OrderUpdateBuilder;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService {

    ApiRoot apiRoot;
    String projectKey;

    public OrderService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(final ApiHttpResponse<Cart> cartApiHttpResponse) {
        Cart cart = cartApiHttpResponse.getBody();
        return apiRoot.withProjectKey(projectKey)
                .orders()
                .post(OrderFromCartDraftBuilder.of()
                        .cart(CartResourceIdentifierBuilder.of()
                                .id(cart.getId())
                                .build())
                        .version(cart.getVersion())
                        .build()
                ).execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final OrderState state) {
        Order order = orderApiHttpResponse.getBody();

        return apiRoot.withProjectKey(projectKey)
                .orders()
                .withId(order.getId())
                .post(OrderUpdateBuilder.of()
                        .version(order.getVersion())
                        .actions(OrderChangeOrderStateActionBuilder.of()
                                .orderState(state)
                                .build())
                        .build()
                ).execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeWorkflowState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final State workflowState) {
        Order order = orderApiHttpResponse.getBody();

        return apiRoot.withProjectKey(projectKey)
                .orders()
                .withId(order.getId())
                .post(
                        OrderUpdateBuilder.of()
                                .version(order.getVersion())
                                .actions(
                                        OrderTransitionStateActionBuilder.of()
                                                .state(
                                                        StateResourceIdentifierBuilder.of()
                                                                .id(workflowState.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                ).execute();
    }

}
