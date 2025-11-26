package br.com.fiap;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.google.gson.Gson;


import java.util.Optional;

public class ProcessarFeedback {

    @FunctionName("ProcessarFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.FUNCTION) 
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Recebendo novo feedback...");

        // 1. Validar o corpo da requisição
        if (!request.getBody().isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Por favor, envie o JSON no corpo da requisição.").build();
        }

        try {
            // 2. Converter JSON para Objeto Java (Feedback)
            String jsonBody = request.getBody().get();
            Gson gson = new Gson();
            Feedback feedback = gson.fromJson(jsonBody, Feedback.class);

            // Validação simples
            if (feedback.getNota() < 0 || feedback.getNota() > 10) {
                 return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("A nota deve ser entre 0 e 10.").build();
            }

            // 3. Conectar e Salvar no Cosmos DB
            context.getLogger().info("Salvando no Cosmos DB...");
            String cosmosConn = System.getenv("COSMOS_DB_CONNECTION_STRING");
            
            // 3. Conectar e Salvar no Cosmos DB
            context.getLogger().info("Salvando no Cosmos DB...");
            
            // Lendo as variáveis separadas
            String cosmosEndpoint = System.getenv("COSMOS_DB_ENDPOINT");
            String cosmosKey = System.getenv("COSMOS_DB_KEY");
            
            CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(cosmosEndpoint)
                .key(cosmosKey)
                .buildClient();
            
            CosmosDatabase database = cosmosClient.getDatabase("avaliacoes");
            CosmosContainer container = database.getContainer("Avaliacoes"); 
            
            container.createItem(feedback);
            context.getLogger().info("Item salvo com ID: " + feedback.getId());

            // 4. Lógica de Negócio: Verificar se é Urgente (Nota <= 4)
            if (feedback.getNota() <= 4) {
                context.getLogger().info("Nota baixa detectada! Enviando para fila de urgência...");
                
                String queueConn = System.getenv("QUEUE_CONNECTION_STRING");
                
                // Conecta na fila
                QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(queueConn)
                    .queueName("notificacoes-urgentes")
                    .buildClient();
                
                // Cria a mensagem de alerta
                queueClient.sendMessage(jsonBody);
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("Feedback recebido e processado com sucesso! ID: " + feedback.getId())
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Erro ao processar: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage()).build();
        }
    }
}