package dev.clinplay.api.modules.accounts.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.clinplay.api.modules.accounts.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public boolean verificarEmail(String email) { return repository.existsByEmail(email); }

    public boolean verificarEmailExcluindo(String email, UUID id) { return repository.existsByEmailAndIdNot(email, id); }

}
