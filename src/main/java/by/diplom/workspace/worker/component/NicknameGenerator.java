package by.diplom.workspace.worker.component;

import by.diplom.workspace.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NicknameGenerator {

    private final UserRepository userRepository;

    public String generate(String fullName) {
        String base = normalizeFullName(fullName);

        if (base.isBlank()) {
            base = "user";
        }

        String candidate = base;

        while (userRepository.existsByNickname(candidate)) {
            candidate = base + "." + UUID.randomUUID().toString().substring(0, 4);
        }

        return candidate;
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }

        String transliterated = transliterateCyrillic(fullName);

        return Normalizer.normalize(transliterated, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[\\s_-]+", ".")
                .replaceAll("\\.+", ".")
                .replaceAll("^\\.|\\.$", "")
                .replaceAll("[^a-z0-9.]", "");
    }

    private String transliterateCyrillic(String value) {
        StringBuilder result = new StringBuilder();

        for (char ch : value.toCharArray()) {
            result.append(transliterateChar(ch));
        }

        return result.toString();
    }

    private String transliterateChar(char ch) {
        return switch (Character.toLowerCase(ch)) {
            case 'а' -> "a";
            case 'б' -> "b";
            case 'в' -> "v";
            case 'г' -> "g";
            case 'д' -> "d";
            case 'е', 'ё' -> "e";
            case 'ж' -> "zh";
            case 'з' -> "z";
            case 'и' -> "i";
            case 'й' -> "y";
            case 'к' -> "k";
            case 'л' -> "l";
            case 'м' -> "m";
            case 'н' -> "n";
            case 'о' -> "o";
            case 'п' -> "p";
            case 'р' -> "r";
            case 'с' -> "s";
            case 'т' -> "t";
            case 'у' -> "u";
            case 'ф' -> "f";
            case 'х' -> "h";
            case 'ц' -> "ts";
            case 'ч' -> "ch";
            case 'ш' -> "sh";
            case 'щ' -> "sch";
            case 'ъ', 'ь' -> "";
            case 'ы' -> "y";
            case 'э' -> "e";
            case 'ю' -> "yu";
            case 'я' -> "ya";
            default -> String.valueOf(ch);
        };
    }
}