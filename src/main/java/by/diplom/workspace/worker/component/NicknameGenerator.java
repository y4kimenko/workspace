package by.diplom.workspace.worker.component;

import by.diplom.workspace.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final UserRepository userRepository;
    public String generate(String fullName) {
        String base = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")          // убираем диакритику
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9.]", "");      // только латиница и цифры

        if (base.isBlank()) base = "user";

        String candidate = base;
        while (userRepository.existsByNickname(candidate)) {
            candidate = base + "." + UUID.randomUUID().toString().substring(0, 4);
        }
        return candidate;
    }
}
