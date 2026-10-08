package br.com.sistemas.chamados.exception;

import java.time.LocalDateTIme;
import java.util.List;

public record ErroResposta(
    LocalDateTIme momento,
    int status,
    String erro,
    List<String> detalhes
){}
