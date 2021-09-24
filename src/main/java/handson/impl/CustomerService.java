package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerCreateEmailTokenBuilder;
import com.commercetools.api.models.customer.CustomerDraftBuilder;
import com.commercetools.api.models.customer.CustomerEmailVerifyBuilder;
import com.commercetools.api.models.customer.CustomerSetCustomerGroupActionBuilder;
import com.commercetools.api.models.customer.CustomerSignInResult;
import com.commercetools.api.models.customer.CustomerToken;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import io.vrap.rmf.base.client.ApiHttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class CustomerService {

    ApiRoot apiRoot;
    String projectKey;

    public CustomerService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }

    public CompletableFuture<ApiHttpResponse<Customer>> getCustomerByKey(String customerKey) {
        return apiRoot.withProjectKey(projectKey).customers().withKey(customerKey).get().execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerSignInResult>> createCustomer(final String email,
                                                                                   final String password,
                                                                                   final String customerKey,
                                                                                   final String firstName,
                                                                                   final String lastName,
                                                                                   final String country) {

        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .post(CustomerDraftBuilder.of()
                                                .email(email)
                                                .password(password)
                                                .key(customerKey)
                                                .firstName(firstName)
                                                .lastName(lastName)
                                                .addresses(AddressBuilder.of().country(country).build())
                                                .defaultBillingAddress(0L)
                                                .build())
                      .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(final ApiHttpResponse<CustomerSignInResult> customerSignInResultApiHttpResponse,
                                                                                          final long timeToLiveInMinutes) {
        Customer customer = customerSignInResultApiHttpResponse.getBody().getCustomer();
        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .emailToken()
                      .post(CustomerCreateEmailTokenBuilder.of().id(customer.getId()).ttlMinutes(timeToLiveInMinutes).build())
                      .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(final Customer customer, final long timeToLiveInMinutes) {

        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .emailToken()
                      .post(CustomerCreateEmailTokenBuilder.of().id(customer.getId()).ttlMinutes(timeToLiveInMinutes).build())
                      .execute();
    }

    public CompletableFuture<ApiHttpResponse<JsonNode>> verifyEmail(final ApiHttpResponse<CustomerToken> customerTokenApiHttpResponse) {
        CustomerToken token = customerTokenApiHttpResponse.getBody();

        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .emailConfirm()
                      .post(CustomerEmailVerifyBuilder.of().tokenValue(token.getValue()).build())
                      .execute();
    }

    public CompletableFuture<ApiHttpResponse<JsonNode>> verifyEmail(final CustomerToken customerToken) {

        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .emailConfirm()
                      .post(CustomerEmailVerifyBuilder.of().tokenValue(customerToken.getValue()).build())
                      .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerGroup>> getCustomerGroupByKey(String customerGroupKey) {
        return apiRoot.withProjectKey(projectKey).customerGroups().withKey(customerGroupKey).get().execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> assignCustomerToCustomerGroup(final ApiHttpResponse<Customer> customerApiHttpResponse,
                                                                                      final ApiHttpResponse<CustomerGroup> customerGroupApiHttpResponse) {

        final Customer customer = customerApiHttpResponse.getBody();
        final CustomerGroup customerGroup = customerGroupApiHttpResponse.getBody();

        return apiRoot.withProjectKey(projectKey)
                      .customers()
                      .withKey(customer.getKey())
                      .post(CustomerUpdateBuilder.of()
                                                 .version(customer.getVersion())
                                                 .actions(CustomerSetCustomerGroupActionBuilder.of()
                                                                                               .customerGroup(
                                                                                                   CustomerGroupResourceIdentifierBuilder.of()
                                                                                                                                         .key(
                                                                                                                                             customerGroup.getKey())
                                                                                                                                         .build())
                                                                                               .build())
                                                 .build())
                      .execute();
    }


/*

    public CompletionStage<CustomerSignInResult> createCustomerForStore(final String email,
                                                                final String password,
                                                                final String key,
                                                                final String firstName,
                                                                final String lastName,
                                                                final CountryCode countryCode,
                                                                final Store store) {

        final CustomerDraft customerDraft =
                CustomerDraftBuilder.of(email, password)
                        .stores(Arrays.asList(ResourceIdentifier.ofKey(store.getKey())))
                        .firstName(firstName)
                        .lastName(lastName)
                        .key(key)
                        .addresses(Arrays.asList(Address.of(countryCode)))
                        .defaultShippingAddress(0)
                        .build();
        return
                client.execute(
                        CustomerCreateCommand.of(customerDraft)
                );
    }
*/
}
