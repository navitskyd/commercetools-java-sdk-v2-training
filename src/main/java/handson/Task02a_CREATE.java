package handson;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpClient;
import java.io.IOException;
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
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO:
        //  UPDATE dev.properties with an API client

        // TODO: {@link ClientService#createApiClient(String prefix)}
        //  UPDATE the ApiPrefixHelper with your prefix from dev.properties (e.g. "mh-dev-admin.")

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task02a_CREATE.class.getName());
        final ApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, getProjectKey(apiClientPrefix));

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            logger.info("Customer fetch: " + "");

            // TODO:
            //  CREATE a customer
            //  CREATE a email verification token
            //  Verify customer
            //
            String customerPrettyString = customerService.createCustomer("dn-abc@mail.com", "pwd", "dn-customer-abc-mail-ru", "John", "Doe", "UK")
                                                         .thenComposeAsync(
                                                             customerSignInResultApiHttpResponse -> customerService.createEmailVerificationToken(
                                                                 customerSignInResultApiHttpResponse, 5))
                                                         .thenComposeAsync(customerService::verifyEmail)
                                                         .toCompletableFuture()
                                                         .get()
                                                         .getBody()
                                                         .toPrettyString();
            logger.info("Customer created: " + customerPrettyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
