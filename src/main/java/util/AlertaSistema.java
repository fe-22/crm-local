package com.crm.util;

import com.crm.dao.ContatoDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Contato;
import com.crm.model.Negociacao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertaSistema {
    private ContatoDAO contatoDAO;
    private NegociacaoDAO negociacaoDAO;

    public AlertaSistema() {
        try {
            contatoDAO = new ContatoDAO();
            negociacaoDAO = new NegociacaoDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LocalDate converterParaLocalDate(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public List<String> gerarAlertas() {
        List<String> alertas = new ArrayList<>();
        try {
            List<Contato> contatos = contatoDAO.listarTodos();
            LocalDate hoje = LocalDate.now();

            for (Contato c : contatos) {
                Date proximoDate = c.getProximoContato();
                if (proximoDate != null) {
                    LocalDate proximo = converterParaLocalDate(proximoDate);
                    long dias = ChronoUnit.DAYS.between(hoje, proximo);
                    if (dias >= 0 && dias <= 7) {
                        alertas.add("🔔 Contato: " + c.getDescricao() + " vence em " + dias + " dia(s)");
                    }
                }
            }

            List<Negociacao> negociacoes = negociacaoDAO.listarTodos();
            for (Negociacao n : negociacoes) {
                Date previstaDate = n.getDataPrevista();
                if (previstaDate != null && !"Fechado".equals(n.getEtapa()) && !"Perdido".equals(n.getEtapa())) {
                    LocalDate prevista = converterParaLocalDate(previstaDate);
                    long dias = ChronoUnit.DAYS.between(hoje, prevista);
                    if (dias >= 0 && dias <= 7) {
                        alertas.add("💰 Negociação: " + n.getTitulo() + " vence em " + dias + " dia(s)");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alertas;
    }
}