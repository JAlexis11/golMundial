package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.Match;
import com.utn.golmundial.publicfrontend.model.Venue;
import com.utn.golmundial.publicfrontend.model.TournamentSeedData;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class MatchServiceMock implements MatchService {

    private List<Match> matches;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);

    // TODO: replace with a REST call to Alexis's Statistics backend once it's ready.
    @PostConstruct
    public void loadSeed() {
        try (Jsonb jsonb = JsonbBuilder.create();
             InputStream in = getClass().getClassLoader()
                     .getResourceAsStream("seed/seed-utn-golmundial-2026.json")) {

            TournamentSeedData data = jsonb.fromJson(in, TournamentSeedData.class);
            this.matches = data.getMatches();

            Map<Integer, Venue> venueMap = new HashMap<>();
            for (Venue v : data.getVenues()) {
                venueMap.put(v.getId(), v);
            }

            for (Match m : matches) {
                Venue venue = venueMap.get(m.getVenueId());
                if (venue != null) {
                    m.setVenueLabel(venue.getName() + " - " + venue.getCity());
                }
                Instant instant = Instant.parse(m.getKickoffUtc());
                m.setDisplayDate(DATE_FORMAT.format(instant.atZone(ZoneOffset.UTC)) + " UTC");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.matches = List.of();
        }
    }

    @Override
    public List<Match> listGroupStageMatches() {
        return matches;
    }

    @Override
    public Match findById(Integer id) {
        return matches.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}