package riffy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import riffy.model.UsuarioEntity;
import riffy.services.UsuarioService;

@SpringBootApplication
public class RiffyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiffyApplication.class, args);
	}

	@Bean
	CommandLineRunner crearAdminInicial(UsuarioService usuarioService) {
		return args -> {
			try {
				UsuarioEntity admin = new UsuarioEntity();
				admin.setNombre("Admin");
				admin.setUsuario("admin");
				admin.setContrasena("admin");
				admin.setRol("ADMIN");
				usuarioService.registrar(admin);
				System.out.println(">>> Admin creado correctamente");
			} catch (RuntimeException e) {
				System.out.println(">>> Admin ya existe, omitiendo: " + e.getMessage());
			}
		};
	}

}
