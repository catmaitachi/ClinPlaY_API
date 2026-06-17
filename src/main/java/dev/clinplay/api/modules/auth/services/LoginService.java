package dev.clinplay.api.modules.auth.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.clinplay.api.modules.auth.models.Login;
import dev.clinplay.api.modules.auth.models.enums.Provedor;
import dev.clinplay.api.modules.auth.repositories.LoginRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginRepository repository;

    public Login buscar(UUID id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Login não encontrado")); }

    @Transactional
    public Login oAuth(String sub, String provedor) {

        return repository.findByIdentificadorAndProvedor(sub, Provedor.valueOf(provedor)).orElseGet(() -> {

                Login l = Login.builder()
                    .identificador(sub)
                    .provedor(Provedor.valueOf(provedor))
                    .build();

                return repository.save(l);

            });

    }
    
}
