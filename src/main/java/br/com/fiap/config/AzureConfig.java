package br.com.fiap.config;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;

public class AzureConfig {

    // Instâncias Singleton (Static)
    private static CosmosClient cosmosClient;
    private static QueueClient queueClient;

    private AzureConfig() {}

    public static CosmosClient getCosmosClient() {
        if (cosmosClient == null) {
            cosmosClient = new CosmosClientBuilder()
                    .endpoint(System.getenv("COSMOS_DB_ENDPOINT"))
                    .key(System.getenv("COSMOS_DB_KEY"))
                    .buildClient();
        }
        return cosmosClient;
    }

    public static QueueClient getQueueClient() {
        if (queueClient == null) {
            queueClient = new QueueClientBuilder()
                    .connectionString(System.getenv("QUEUE_CONNECTION_STRING"))
                    .queueName("notificacoes-urgentes")
                    .buildClient();
        }
        return queueClient;
    }
}
