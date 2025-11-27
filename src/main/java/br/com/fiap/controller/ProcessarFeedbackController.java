package br.com.fiap.controller;

import br.com.fiap.service.FeedbackService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.util.Optional;

public class ProcessarFeedbackController {

    private final FeedbackService service = new FeedbackService();

    @FunctionName("ProcessarFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            if (!request.getBody().isPresent()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Body vazio").build();
            }

            // O Controller chama o Serviço
            var feedback = service.processarFeedback(request.getBody().get());

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("Salvo com ID: " + feedback.getId())
                    .build();

        } catch (IllegalArgumentException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
        } catch (Exception e) {
            context.getLogger().severe(e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}