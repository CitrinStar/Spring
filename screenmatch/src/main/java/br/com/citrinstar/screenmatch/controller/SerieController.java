package br.com.citrinstar.screenmatch.controller;

import br.com.citrinstar.screenmatch.dto.EpisodioDTO;
import br.com.citrinstar.screenmatch.dto.SerieDTO;
import br.com.citrinstar.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/series")
@RestController
public class SerieController {

    @Autowired
    private SerieService service;

    @GetMapping
    public List<SerieDTO> obterSeries(){
        return service.obterTodasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Series(){
        return service.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return service.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return service.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id){
        return service.obterTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterTemporadasPorNumero(@PathVariable Long id, @PathVariable("numero") Long numeroTemporada){
        return service.obterTemporadasPorNumero(id, numeroTemporada);
    }

    @GetMapping("/categoria/{nomeGenero}")
    public List<SerieDTO> obterSeriePorCategoria(@PathVariable String nomeGenero){
        return service.obterSeriesPorCategoria(nomeGenero);
    }
}