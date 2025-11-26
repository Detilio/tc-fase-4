package br.com.fiap;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.google.gson.Gson;

public class NotificarAdministrador {

    @FunctionName("NotificarAdministrador")
    public void run(
        @QueueTrigger(name = "msg", 
                      queueName = "notificacoes-urgentes", 
                      connection = "QUEUE_CONNECTION_STRING") 
        String message, 
        final ExecutionContext context
    ) {
        context.getLogger().info("Nova mensagem na fila de urgência: " + message);

        try {
            // Converte a mensagem da fila (JSON) de volta para Objeto
            Gson gson = new Gson();
            Feedback feedback = gson.fromJson(message, Feedback.class);

            // Simula o envio de e-mail (Aqui entraria o SendGrid ou JavaMail)
            // Requisito do projeto: Exibir os dados do e-mail [cite: 37-40]
            context.getLogger().info("========================================");
            context.getLogger().info("[SIMULAÇÃO DE EMAIL] Enviando alerta para admin@fiap.com.br");
            context.getLogger().info("Assunto: URGENTE - Feedback Negativo Recebido");
            context.getLogger().info("Descrição: " + feedback.getDescricao());
            context.getLogger().info("Nota: " + feedback.getNota());
            context.getLogger().info("Data: " + feedback.getData());
            context.getLogger().info("========================================");

        } catch (Exception e) {
            context.getLogger().severe("Erro ao processar notificação: " + e.getMessage());
        }
    }
}