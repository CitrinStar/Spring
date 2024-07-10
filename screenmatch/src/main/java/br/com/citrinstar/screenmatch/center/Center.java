package br.com.citrinstar.screenmatch.center;

import br.com.citrinstar.screenmatch.model.*;
import br.com.citrinstar.screenmatch.repository.SerieRepository;
import br.com.citrinstar.screenmatch.service.ConsumoAPI;
import br.com.citrinstar.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Center {

    private Scanner leitorInput = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=" + System.getenv("OMDB_API_KEY");
    private SerieRepository repositorioSerie;
    private List<Serie> series = new ArrayList<>();
    Optional<Serie> serieBuscada;

    public Center(SerieRepository repositorioSerie) {
        this.repositorioSerie = repositorioSerie;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar séries por título
                    5 - Buscar séries por ator
                    6 - Buscar top 5 séries
                    7 - Buscar por Genero
                    8 - Busca por temporadas
                    9 - Buscar por trecho do título
                    10 - Top 5 episódios por série
                    11 - Buscar episódios por lançamento
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitorInput.nextInt();
            leitorInput.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorGenero();
                    break;
                case 8:
                    buscarSeriesPorTemporada();
                    break;
                case 9:
                    buscarEpisodioPorTrechoTitulo();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorioSerie.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitorInput.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Digite o nome da serie que deseja selecionar o episódio: ");
        var nomeSerie = leitorInput.nextLine();

        Optional<Serie> serie = repositorioSerie.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorioSerie.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorioSerie.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o título da serie que deseja encontrar: ");
        var tituloSerie = leitorInput.nextLine();
        serieBuscada = repositorioSerie.findByTituloContainingIgnoreCase(tituloSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator presente na série que deseja encontrar: ");
        var ator = leitorInput.nextLine();
        List<Serie> series = repositorioSerie.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(ator, 7.8);
        System.out.println("\nSéries em que " + ator + " trabalhou: ");
        series.forEach(s -> System.out.println(s.getTitulo() + "  avaliação: " + s.getAvaliacao().toString()));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorioSerie.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s -> System.out.println(s.getTitulo() + "  avaliação: " + s.getAvaliacao().toString()));

    }

    private void buscarSeriesPorGenero() {
        System.out.println("Digite o nome do gênero para filtrar séries: ");
        var genero = leitorInput.nextLine();
        Categoria categoria = Categoria.fromPortugues(genero);
        List<Serie> seriePorGenero = repositorioSerie.findByGenero(categoria);
        seriePorGenero.forEach(s -> System.out.println(s.getTitulo() + "  avaliação: " + s.getAvaliacao().toString()));
    }

    private void buscarSeriesPorTemporada() {
        //findByTotalTemporadasLessThanEqualAndByAvaliacaoGreaterThanEqual
        //List<Serie> seriePorTemporada = repositorioSerie.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(4,7.8);
        List<Serie> seriePorTemporada = repositorioSerie.seriesPorTemporadaEAvaliacao(4,7.8);
                seriePorTemporada.forEach(s -> System.out.println(s.getTitulo() + "  avaliação: " + s.getAvaliacao().toString()));
    }


    private void buscarEpisodioPorTrechoTitulo() {
        System.out.println("Digite o trecho do título para filtrar o episódio: ");
        var trechoEpi = leitorInput.nextLine();
        List<Episodio> episodios = repositorioSerie.episodiosPorTrecho(trechoEpi);
        episodios.forEach(episodio ->
                        System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                                episodio.getSerie().getTitulo(), episodio.getTemporada(),
                                episodio.getEpisodio(), episodio.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorioSerie.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(episodio ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                            episodio.getSerie().getTitulo(), episodio.getTemporada(),
                            episodio.getEpisodio(), episodio.getTitulo(), episodio.getAvaliacao()));
        }

    }

    private void buscarEpisodioPorData() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()) {
            System.out.println("Insira o ano que deseja filtrar: ");
            var anoLancamento = leitorInput.nextInt();
            leitorInput.nextLine();
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorioSerie.episodiosPorSerieAnos(serie, anoLancamento);

            topEpisodios.forEach(episodio ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Lançamento %s\n",
                            episodio.getSerie().getTitulo(), episodio.getTemporada(),
                            episodio.getEpisodio(), episodio.getTitulo(), episodio.getDataLancamento()));
        }
    }

}
