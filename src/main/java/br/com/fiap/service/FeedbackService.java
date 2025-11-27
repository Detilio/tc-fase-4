package br.com.fiap.service;

import br.com.fiap.model.Feedback;
import br.com.fiap.repository.FeedbackRepository;
import br.com.fiap.repository.FeedbackRepositoryImpl;
import br.com.fiap.repository.QueueRepository;
import com.google.gson.Gson;

import java.util.List;

public class FeedbackService {

    // Dependemos de abstrações
    private final FeedbackRepository feedbackRepository;
    private final QueueRepository queueRepository;

    public FeedbackService() {
        this.feedbackRepository = new FeedbackRepositoryImpl();
        this.queueRepository = new QueueRepository();
    }

    public Feedback processarFeedback(String jsonBody) {
        Gson gson = new Gson();
        Feedback feedback = gson.fromJson(jsonBody, Feedback.class);

        if (feedback.getNota() < 0 || feedback.getNota() > 10) {
            throw new IllegalArgumentException("A nota deve ser entre 0 e 10.");
        }

        // 1. Salvar no Banco
        feedbackRepository.salvar(feedback);

        // 2. Regra de Urgência (Nota <= 4)
        if (feedback.getNota() <= 4) {
            queueRepository.enviarMensagem(jsonBody);
        }

        return feedback;
    }

    public String gerarRelatorio() {
        List<Feedback> feedbacks = feedbackRepository.listarTodos();

        if (feedbacks.isEmpty()) return "Sem dados.";

        double soma = 0;
        int criticos = 0;
        for (Feedback f : feedbacks) {
            soma += f.getNota();
            if (f.getNota() <= 4) criticos++;
        }

        double media = soma / feedbacks.size();

        return String.format(
                "Total: %d | Média: %.2f | Críticos: %d",
                feedbacks.size(), media, criticos
        );
    }
}