package riffy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import riffy.model.UsuarioEntity;
import riffy.services.UsuarioService;

@SpringBootApplication
public class RiffyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiffyApplication.class, args);
	}

	@Bean
	CommandLineRunner crearUsuariosIniciales(UsuarioService usuarioService) {
		return args -> {
			// Crear ADMIN
			try {
				UsuarioEntity admin = new UsuarioEntity();
				admin.setNombre("Administrador");
				admin.setUsuario("admin");
				admin.setContrasena("admin");
				admin.setEmail("admin@riffy.com"); 
				admin.setRol("ADMIN");
				usuarioService.registrar(admin);
				System.out.println(">>> Admin creado correctamente");
			} catch (RuntimeException e) {
				System.out.println(">>> Admin ya existe, omitiendo: " + e.getMessage());
			}

			// Crear Usuario 1: Javier Mancera
			try {
				UsuarioEntity javier = new UsuarioEntity();
				javier.setNombre("Javier Mancera");
				javier.setUsuario("ihavenomouth");
				javier.setContrasena("ihavenomouth");
				javier.setEmail("javier.mancera@riffy.com");
				javier.setRol("USER");
				usuarioService.registrar(javier);
				System.out.println(">>> Usuario Javier Mancera creado correctamente");
			} catch (RuntimeException e) {
				System.out.println(">>> Usuario ihavenomouth ya existe, omitiendo: " + e.getMessage());
			}

			// Crear Usuario 2: Juan de Dios Álvarez
			try {
				UsuarioEntity juan = new UsuarioEntity();
				juan.setNombre("Juan de Dios Álvarez");
				juan.setUsuario("juande");
				juan.setContrasena("juande");
				juan.setEmail("juande.alvarez@riffy.com"); 
				juan.setRol("USER");
				usuarioService.registrar(juan);
				System.out.println(">>> Usuario Juan de Dios Álvarez creado correctamente");
			} catch (RuntimeException e) {
				System.out.println(">>> Usuario juande ya existe, omitiendo: " + e.getMessage());
			}

			// Crear Usuario 3: Antonio Ladesa
			try {
				UsuarioEntity antonio = new UsuarioEntity();
				antonio.setNombre("Antonio Ladesa");
				antonio.setUsuario("antolade");
				antonio.setContrasena("antolade");
				antonio.setEmail("antonio.ladesa@riffy.com"); 
				antonio.setRol("USER");
				usuarioService.registrar(antonio);
				System.out.println(">>> Usuario Antonio Ladesa creado correctamente");
			} catch (RuntimeException e) {
				System.out.println(">>> Usuario antolade ya existe, omitiendo: " + e.getMessage());
			}
		};
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}