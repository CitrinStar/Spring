package br.com.citrinstar.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {

    private Integer temporada;
    private Integer episodio;
    private Double avaliacao;
    private String titulo;
    private LocalDate dataLancamento;

    public Episodio(Integer temporada, DadosEspisodio dadosEpisodio) {
        this.temporada = temporada;
        this.episodio = dadosEpisodio.episodio();
        this.titulo = dadosEpisodio.titulo();
        try{
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch (NumberFormatException ex) {
            this.avaliacao = 0.0;
        } catch (DateTimeParseException dateTimeParseException){
            this.dataLancamento = null;
        }
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getEpisodio() {
        return episodio;
    }

    public void setEpisodio(Integer episodio) {
        this.episodio = episodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        return  "temporada=" + temporada +
                ", episodio=" + episodio +
                ", avaliacao=" + avaliacao +
                ", titulo='" + titulo + '\'' +
                ", dataLancamento=" + dataLancamento;

    }
}
