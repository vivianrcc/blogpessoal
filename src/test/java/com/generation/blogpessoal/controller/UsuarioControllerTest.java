package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	public void start() {

		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "Nome", "root@root.com", "rootroot", " "));

	}

	@Test
	@DisplayName("Cadrastrar Um Usuario")
	public void deveCriarUmUsuario() {

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Vivian Carrillo", "viviancarrillo@email.com.br", "Gatinhosbonitinhos", ""));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

	}

	// METODO PARA TESTAR O ERRO DE DUPLICAÇÃO
	@Test
	@DisplayName("Não deve permitir a duplicação do Usuario")
	public void naoDeveDuplicarUsuario() {

		usuarioService
				.cadastrarUsuario(new Usuario(0L, "Henrique Queiroz", "henriquequeiroz@email.com.br", "12333678", "-"));

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Henrique Queiroz", "henriquequeiroz@email.com.br", "12333678", "-"));

		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("Atualizar um Usuario")
	public void deveAtualizarUsuario() {
		Usuario usuario = new Usuario(0L, "Aline", "alineherrera@root.com", "12345678", "-");

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);

		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Aline herrera", "alinehrrc@root.com",
				"12345678", "url2");

		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);

		ResponseEntity<Usuario> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, requisicao, Usuario.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}

	@Test
	@DisplayName(" Listar todos os Usuários!")
	public void deveMostrarTodosUsuarios() {
		Usuario usuario1 = new Usuario(0L, "Aline", "aline@mail.com", "12345678", "url");
		Usuario usuario2 = new Usuario(0L, "Aline2", "aline2@mail.com", "12345678", "url");
		Usuario usuario3 = new Usuario(0L, "ALine3", "aline3@mail.com", "12345678", "url");

		usuarioService.cadastrarUsuario(usuario1);
		usuarioService.cadastrarUsuario(usuario2);
		usuarioService.cadastrarUsuario(usuario3);

		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}
