package br.com.fiap.repository;

import br.com.fiap.model.Feedback;
import java.util.List;

public interface FeedbackRepository {
    void salvar(Feedback feedback);
    List<Feedback> listarTodos();
}
