package br.com.fiap.repository;

import br.com.fiap.config.AzureConfig;
import br.com.fiap.model.Feedback;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final CosmosContainer container;

    public FeedbackRepositoryImpl() {
        // Pega a conexão já pronta da Config
        this.container = AzureConfig.getCosmosClient()
                .getDatabase("avaliacoes")
                .getContainer("Avaliacoes");
    }

    @Override
    public void salvar(Feedback feedback) {
        container.createItem(feedback);
    }

    @Override
    public List<Feedback> listarTodos() {
        List<Feedback> lista = new ArrayList<>();
        String query = "SELECT * FROM c";
        CosmosPagedIterable<Feedback> items = container.queryItems(query, new CosmosQueryRequestOptions(), Feedback.class);
        items.forEach(lista::add);
        return lista;
    }
}
