package br.com.citrinstar.screenmatch.repository;

import br.com.citrinstar.screenmatch.model.Categoria;
import br.com.citrinstar.screenmatch.model.Episodio;
import br.com.citrinstar.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    //Consultas derivadas em JPA são métodos de consulta definidos em repositórios que são automaticamente
    //interpretados pelo framework Spring Data JPA com base na convenção de nomenclatura dos métodos.
    //Principais convenções de nomenclatura
    //FindBy, CountBy, DeleteBy e ExistsBy
    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);
    List<Serie> findTop5ByOrderByAvaliacaoDesc();
    List<Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer totalTemporadas, Double avaliacao);
    //Com uso de JPQL
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpi%")
    List<Episodio> episodiosPorTrecho(String trechoEpi);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC  LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieAnos(Serie serie, int anoLancamento);
    @Query("SELECT s FROM Serie s JOIN s.episodios e GROUP BY s ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.Id = :id AND e.temporada = :numero")
    List<Episodio> encontrarEpisodiosPorTemporada(Long id, Long numero);
}
