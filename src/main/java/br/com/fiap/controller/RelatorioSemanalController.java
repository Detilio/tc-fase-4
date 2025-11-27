package br.com.fiap.controller;

import br.com.fiap.service.FeedbackService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class RelatorioSemanalController {

    private final FeedbackService service = new FeedbackService();

    @FunctionName("RelatorioSemanal")
    public void run(
            @TimerTrigger(name = "timerInfo", schedule = "0 0 8 * * MON") String timerInfo,
            final ExecutionContext context
    ) {
        context.getLogger().info("Gerando relatório...");

        String relatorio = service.gerarRelatorio();

        context.getLogger().info("=== RELATÓRIO ===");
        context.getLogger().info(relatorio);
    }
}