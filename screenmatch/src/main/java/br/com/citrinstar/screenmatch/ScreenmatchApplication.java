package br.com.citrinstar.screenmatch;

import br.com.citrinstar.screenmatch.center.Center;
import br.com.citrinstar.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	@Autowired
	private SerieRepository repositorioSerie;

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Alta coesão, baixo acoplamento: temos mais coesão do código quando temos poucas coisas acopladas
		Center menu = new Center(repositorioSerie);
		menu.exibeMenu();
	}
}
