package br.com.citrinstar.screenmatch.dto;

import br.com.citrinstar.screenmatch.model.Categoria;

public record SerieDTO(Long Id,
                       String titulo,
                       Integer totalTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse) {

}
