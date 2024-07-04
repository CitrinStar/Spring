package br.com.citrinstar.screenmatch.center;

import br.com.citrinstar.screenmatch.model.DadosEspisodio;
import br.com.citrinstar.screenmatch.model.DadosSerie;
import br.com.citrinstar.screenmatch.model.DadosTemporada;
import br.com.citrinstar.screenmatch.model.Episodio;
import br.com.citrinstar.screenmatch.service.ConsumoAPI;
import br.com.citrinstar.screenmatch.service.ConverteDados;
import ch.qos.logback.core.encoder.JsonEscapeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Center {

    private Scanner leitorInput = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    //https://www.omdbapi.com/?t=Gilmore+girls&apikey=6798a821
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6798a821";
    public void exibeMenu(){
        System.out.println("Digite o nome da série:");
        var nomeSerie = leitorInput.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> tempodaras = new ArrayList<>();

        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumoAPI.obterDados(ENDERECO+nomeSerie.replace(" ","+")+"&season="+i+API_KEY);
            tempodaras.add(conversor.obterDados(json, DadosTemporada.class));
        }

        //Lambda
        tempodaras.forEach(System.out::println);
        tempodaras.forEach( temporada -> temporada.episodios().forEach(
                episodio -> System.out.println(episodio.titulo())
                ));

        List<DadosEspisodio> episodios = tempodaras.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios:");
//        episodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(p -> System.out.println("Primeiro filtro(N/A) " + p))
//                .sorted(Comparator.comparing(DadosEspisodio::avaliacao).reversed())
//                .peek(p -> System.out.println("Ordenação: " + p))
//                .limit(10)
//                .peek(p -> System.out.println("Limite: " + p))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(p -> System.out.println("Mapeamento : " + p))
//                .forEach(System.out::println);

        List<Episodio> episodioList = tempodaras.stream()
                .flatMap( e -> e.episodios().stream()
                        .map(d -> new Episodio(e.numeroTemporada(),d))
                ).collect(Collectors.toList());

        episodioList.forEach(System.out::println);

        System.out.println("\nDigite o nome do episódio: ");
        var trechoTitulo = leitorInput.nextLine();
        Optional<Episodio> first = episodioList.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                .findFirst();

        //Optional isPresent e .get() andam juntos. Onde .get() não pode ser usado se o Optional for null
        if(first.isPresent()){
            System.out.println("Espisódio encontrado.");
            System.out.println("Temporada: " + first.get().getTemporada());
        }else{
            System.out.println("Episódio não encontrado");
        }

        first.ifPresent(System.out::println);


//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitorInput.nextInt();
//        leitorInput.nextLine();
//
//        LocalDate anoPesquisa = LocalDate.of(ano,1,1);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodioList.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(anoPesquisa))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episódio: " + e.getEpisodio() +
//                                " Data Lançamento: " + e.getDataLancamento().format(formatter)
//                ));

        Map<Integer, Double> avaliacaoTemporada = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoTemporada);

        //Também temos a IntSummaryStatistics
        DoubleSummaryStatistics est = episodioList.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        IntSummaryStatistics stats = episodioList.stream()
                        .mapToInt(Episodio::getTemporada)
                        .summaryStatistics();

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de episódio: " + est.getCount());

    }
}
