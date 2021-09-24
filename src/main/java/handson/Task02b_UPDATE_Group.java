package handson;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpClient;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure sphere client and get project information.
 * <p>
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02b_UPDATE_Group {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, getProjectKey(apiClientPrefix));

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            // TODO:
            //  GET a customer
            //  GET a customer group
            //  ASSIGN the customer to the customer group
            //
            logger.info("Customer assigned to group: " + customerService.getCustomerByKey("dn-customer-abc-mail-ru")
                                                                        .thenCombineAsync(customerService.getCustomerGroupByKey("Indoor"),
                                                                            customerService::assignCustomerToCustomerGroup)
                                                                        .thenComposeAsync(CompletableFuture::toCompletableFuture)
                                                                        .exceptionally(throwable -> {
                                                                            logger.info(throwable.getLocalizedMessage());
                                                                            return null;
                                                                        })
                                                                        .toCompletableFuture()
                                                                        .get()
                                                                        .getBody()
                                                                        .getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

