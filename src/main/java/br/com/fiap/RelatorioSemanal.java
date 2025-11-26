package br.com.fiap;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import br.com.fiap.Feedback;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelatorioSemanal {

    @FunctionName("RelatorioSemanal")
    public void run(
        // Cron: "0 0 8 * * MON" = Toda segunda-feira às 08:00
        @TimerTrigger(name = "timerInfo", schedule = "0 0 8 * * MON") 
        String timerInfo,
        final ExecutionContext context
    ) {
        context.getLogger().info("Iniciando processamento do Relatório Semanal: " + LocalDateTime.now());

        try {
            // 1. Recuperar credenciais das variáveis de ambiente
            String cosmosEndpoint = System.getenv("COSMOS_DB_ENDPOINT");
            String cosmosKey = System.getenv("COSMOS_DB_KEY");
            
            if (cosmosEndpoint == null || cosmosKey == null) {
                context.getLogger().severe("Erro: Variáveis de ambiente do Cosmos DB não configuradas.");
                return;
            }

            // 2. Conectar ao Banco
            CosmosClient client = new CosmosClientBuilder()
                .endpoint(cosmosEndpoint)
                .key(cosmosKey)
                .buildClient();

            CosmosContainer container = client.getDatabase("avaliacoes").getContainer("Avaliacoes");

            // 3. Buscar os dados 
            String query = "SELECT * FROM c";
            CosmosPagedIterable<Feedback> feedbacks = container.queryItems(query, new CosmosQueryRequestOptions(), Feedback.class);

            // 4. Calcular Métricas
            List<Feedback> lista = new ArrayList<>();
            feedbacks.forEach(lista::add);

            if (lista.isEmpty()) {
                context.getLogger().info("Nenhum feedback encontrado para gerar relatório.");
                return;
            }

            double somaNotas = 0;
            int total = lista.size();
            int criticos = 0; // Notas <= 4

            for (Feedback f : lista) {
                somaNotas += f.getNota();
                if (f.getNota() <= 4) criticos++;
            }

            double media = somaNotas / total;

            // 5. Gerar o "Relatório" (Log)
            context.getLogger().info("=============================================");
            context.getLogger().info("           RELATÓRIO SEMANAL DE FEEDBACKS    ");
            context.getLogger().info("=============================================");
            context.getLogger().info("Data de Geração: " + LocalDateTime.now());
            context.getLogger().info("Total de Avaliações: " + total);
            context.getLogger().info("Média Geral de Satisfação: " + String.format("%.2f", media));
            context.getLogger().info("Nível de Urgência (Críticos): " + criticos);
            context.getLogger().info("=============================================");
            
        } catch (Exception e) {
            context.getLogger().severe("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}