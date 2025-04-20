package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {

	private String user, password, token;
	private Long existingMovieId, nonExistingMovieId;

	private Map<String, Object> putScoreInstance;

	@BeforeEach
	public void setup() throws JSONException {
		baseURI = "http://localhost:8080";

		user = "alex@gmail.com";
		password = "123456";
		token = TokenUtil.obtainAccessToken(user, password);

		existingMovieId = 3L;
		nonExistingMovieId = 100L;

		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", existingMovieId);
		putScoreInstance.put("score", 4.0);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		putScoreInstance.put("movieId", nonExistingMovieId);
		JSONObject score = new JSONObject(putScoreInstance);
	
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + token)			
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(score)
		.when()
			.put("/scores")
		.then()
			.statusCode(404)
			.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScoreInstance.put("movieId", null);
		JSONObject score = new JSONObject(putScoreInstance);
	
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + token)			
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(score)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScoreInstance.put("score", -1);
		JSONObject score = new JSONObject(putScoreInstance);
	
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + token)			
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(score)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.message", hasItems("Valor mínimo 0")); //testando 1, 2, 3...
	}	
}
