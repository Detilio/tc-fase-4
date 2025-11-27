package br.com.fiap.repository;

import br.com.fiap.config.AzureConfig;
import com.azure.storage.queue.QueueClient;

public class QueueRepository {
    private final QueueClient queueClient;

    public QueueRepository() {
        this.queueClient = AzureConfig.getQueueClient();
    }

    public void enviarMensagem(String mensagemJson) {
        queueClient.sendMessage(mensagemJson);
    }
}
