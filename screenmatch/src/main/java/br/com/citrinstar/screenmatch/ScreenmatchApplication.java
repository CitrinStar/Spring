package br.com.citrinstar.screenmatch;

import br.com.citrinstar.screenmatch.center.Center;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Alta coesão, baixo acoplamento: temos mais coesão do código quando temos poucas coisas acopladas
		Center menu = new Center();
		menu.exibeMenu();
	}
}
