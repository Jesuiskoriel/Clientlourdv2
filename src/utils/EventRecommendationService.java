package utils;

import dao.EvenementDAO;
import model.Achat;
import model.Evenement;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Recommande des evenements a partir de l'historique d'achats.
 * Le score est simple et explicable : lieu, mots-clés, budget et proximité temporelle.
 */
public final class EventRecommendationService {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "au", "aux", "avec", "ce", "ces", "dans", "de", "des", "du",
            "en", "et", "la", "le", "les", "leur", "leurs", "ou", "par", "pour",
            "sans", "sur", "une", "un", "vos", "votre", "plus"
    );

    private EventRecommendationService() {
    }

    public static List<Evenement> recommend(List<Evenement> allEvents, List<Achat> achats, int limit) {
        if (allEvents == null || allEvents.isEmpty() || limit <= 0) {
            return List.of();
        }

        if (achats == null || achats.isEmpty()) {
            return fallbackUpcoming(allEvents, limit);
        }

        UserProfile profile = buildProfile(achats);
        List<ScoredEvenement> scored = new ArrayList<>();

        for (Evenement event : allEvents) {
            if (event == null || profile.purchasedIds.contains(event.getId())) {
                continue;
            }

            double score = 0;

            score += profile.locationWeights.getOrDefault(normalize(event.getLieu()), 0) * 5.0;

            for (String token : extractKeywords(event)) {
                score += profile.keywordWeights.getOrDefault(token, 0) * 1.75;
            }

            if (profile.averagePrice > 0 && event.getPrixBase() > 0) {
                double delta = Math.abs(event.getPrixBase() - profile.averagePrice);
                if (delta <= 10) {
                    score += 4.0;
                } else if (delta <= 25) {
                    score += 2.5;
                } else if (delta <= 50) {
                    score += 1.0;
                }
            }

            if (event.getDateEvent() != null) {
                if (!event.getDateEvent().isBefore(LocalDate.now())) {
                    score += 2.0;
                }
                if (profile.latestPurchaseDate != null) {
                    long daysGap = Math.abs(ChronoUnit.DAYS.between(profile.latestPurchaseDate, event.getDateEvent()));
                    if (daysGap <= 30) {
                        score += 2.0;
                    } else if (daysGap <= 90) {
                        score += 1.0;
                    }
                }
            }

            if (score > 0) {
                scored.add(new ScoredEvenement(event, score));
            }
        }

        scored.sort(Comparator
                .comparingDouble(ScoredEvenement::score).reversed()
                .thenComparing(s -> s.event().getDateEvent(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(s -> normalize(s.event().getNom())));

        List<Evenement> recommendations = new ArrayList<>();
        for (ScoredEvenement entry : scored) {
            if (recommendations.size() >= limit) {
                break;
            }
            recommendations.add(entry.event());
        }

        if (recommendations.isEmpty()) {
            return fallbackUpcoming(allEvents, limit);
        }
        return recommendations;
    }

    private static UserProfile buildProfile(List<Achat> achats) {
        Set<Integer> purchasedIds = new HashSet<>();
        Map<String, Integer> locationWeights = new HashMap<>();
        Map<String, Integer> keywordWeights = new HashMap<>();
        double totalPrice = 0;
        int pricedCount = 0;
        LocalDate latestPurchaseDate = null;
        EvenementDAO evenementDAO = new EvenementDAO();

        for (Achat achat : achats) {
            purchasedIds.add(achat.getEventId());
            if (achat.getPrix() > 0) {
                totalPrice += achat.getPrix();
                pricedCount++;
            }
            if (achat.getDateAchat() != null) {
                LocalDate purchaseDate = achat.getDateAchat().toLocalDate();
                if (latestPurchaseDate == null || purchaseDate.isAfter(latestPurchaseDate)) {
                    latestPurchaseDate = purchaseDate;
                }
            }

            Optional<Evenement> event = evenementDAO.findById(achat.getEventId());
            if (event.isEmpty()) {
                continue;
            }

            increment(locationWeights, normalize(event.get().getLieu()));
            for (String token : extractKeywords(event.get())) {
                increment(keywordWeights, token);
            }
        }

        return new UserProfile(
                purchasedIds,
                locationWeights,
                keywordWeights,
                pricedCount > 0 ? totalPrice / pricedCount : 0,
                latestPurchaseDate
        );
    }

    private static List<Evenement> fallbackUpcoming(List<Evenement> allEvents, int limit) {
        List<Evenement> sorted = new ArrayList<>(allEvents);
        sorted.sort(Comparator.comparing(Evenement::getDateEvent, Comparator.nullsLast(Comparator.naturalOrder())));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    private static List<String> extractKeywords(Evenement event) {
        List<String> tokens = new ArrayList<>();
        tokenize(tokens, event.getNom());
        tokenize(tokens, event.getDescription());
        tokenize(tokens, event.getLieu());
        return tokens;
    }

    private static void tokenize(List<String> target, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{Nd}]+", " ").trim();
        if (normalized.isEmpty()) {
            return;
        }
        for (String token : normalized.split("\\s+")) {
            if (token.length() < 3 || STOP_WORDS.contains(token)) {
                continue;
            }
            target.add(token);
        }
    }

    private static void increment(Map<String, Integer> map, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        map.merge(value, 1, Integer::sum);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private record UserProfile(
            Set<Integer> purchasedIds,
            Map<String, Integer> locationWeights,
            Map<String, Integer> keywordWeights,
            double averagePrice,
            LocalDate latestPurchaseDate
    ) {
    }

    private record ScoredEvenement(Evenement event, double score) {
    }
}
