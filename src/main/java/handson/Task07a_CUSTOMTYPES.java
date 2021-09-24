package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.type.CustomFieldBooleanType;
import com.commercetools.api.models.type.CustomFieldStringType;
import com.commercetools.api.models.type.FieldDefinition;
import com.commercetools.api.models.type.FieldDefinitionBuilder;
import com.commercetools.api.models.type.ResourceTypeId;
import com.commercetools.api.models.type.TypeDraftBuilder;
import com.commercetools.api.models.type.TypeTextInputHint;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task07a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task07a_CUSTOMTYPES.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            Map<String, String> namesForFieldCheck = new HashMap<String, String>() {
                {
                    put("DE", "PlantCheck");
                    put("EN", "PlantCheck");
                }
            };
            Map<String, String> namesForFieldComments = new HashMap<String, String>() {
                {
                    put("DE", "Bemerkungen");
                    put("EN", "comments");
                }
            };

            // Which fields will be used?
            List<FieldDefinition> definitions = Arrays.asList(
                    FieldDefinitionBuilder.of()
                            .name("PlantChecker")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .values(namesForFieldCheck)
                                    .build()
                            )
                            .type(CustomFieldBooleanType.of())
                            .build()
                    ,
                    FieldDefinitionBuilder.of()
                            .name("Comments")
                            .required(false)
                            .label(LocalizedStringBuilder.of()
                                    .values(namesForFieldComments)
                                    .build()
                            )
                            .type(CustomFieldStringType.of())
                            .inputHint(TypeTextInputHint.MULTI_LINE)            // shown as single line????
                            .build()
            );

            Map<String, String> namesForType = new HashMap<String, String>() {
                {
                    put("UA", "dnCustomerPlantChecker");
                    put("EN", "dnCustomerPlantChecker");
                }
            };

            // TODO
            //  Create custom type for Customer resource using the fields above

            logger.info("Custom Type created: " +
                    client.withProjectKey(projectKey)
                            .types().post(TypeDraftBuilder.of()
                                    .key("dnCustomerPlantChecker")
                                    .name(LocalizedStringBuilder.of()
                                            .values(namesForType)
                                            .build())
                                    .resourceTypeIds(ResourceTypeId.CUSTOMER)
                                    .fieldDefinitions(definitions)
                                    .build()).execute().toCompletableFuture().get().getBody().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
