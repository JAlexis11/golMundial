package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.RankingEntry;
import java.util.ArrayList;
import java.util.List;

public class RankingServiceMock {

    public List<RankingEntry> obtenerRanking() {
        List<RankingEntry> lista = new ArrayList<>();

        RankingEntry r1 = new RankingEntry();
        r1.setUsuarioId(995L);
        r1.setSaldo(18.0);
        r1.setAciertos(2);
        lista.add(r1);

        RankingEntry r2 = new RankingEntry();
        r2.setUsuarioId(120L);
        r2.setSaldo(12.0);
        r2.setAciertos(1);
        lista.add(r2);

        return lista;
    }
}