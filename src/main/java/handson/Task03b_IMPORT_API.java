package handson;

import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.ImportService;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_IMPORT_PREFIX.getPrefix();

        final String sinkKey = "dn-store-prices";
        final String projectKey = getProjectKey(apiImportClientPrefix);

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client, projectKey);

        // TODO
        //  CREATE a price import sink
        //  CREATE a price import request
        //  CHECK the status of your import requests
        //
        try (ApiHttpClient apiHttpClient = ClientService.importHttpClient) {
           // logger.info("Created import price sink {} ", importService.createImportPriceSink(sinkKey).toCompletableFuture().get());

            // TODO
            Money amount = MoneyBuilder.of().centAmount(25000L).currencyCode("USD").build();

            ApiHttpResponse<ImportResponse> response = importService
                    .createPriceImportRequest(sinkKey, "dn-jeans-9222021", "dn-WhiteM-variant-key", amount)
                    .toCompletableFuture().get();

            logger.info("Created price resource {} ", response);

            logger.info("Summary report on all sinks on our project: {}", client.withProjectKeyValue(projectKey)
                    .importSinks()
                    .get()
                    .execute()
                    .toCompletableFuture()
                    .get()
                    .getBody()
                    .getResults()
                    .get(0)
                    .getResourceType());
            logger.info("Report on all queued import operations on our price import sink {} ", client.withProjectKeyValue(projectKey)
                    .importSummaries()
                    .importSinkKeyWithImportSinkKeyValue(sinkKey)
                    .get()
                    .execute()
                    .toCompletableFuture()
                    .get()
                    .getBody()
                    .getStates()
                    .getImported());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

