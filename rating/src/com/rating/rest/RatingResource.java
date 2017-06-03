package com.rating.rest;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.rating.domain.Nutzer;

import java.security.Key;
import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.ws.rs.*;

@Path("/start")
public class RatingResource {

	static HashMap token2session;
	static String apiKey = "4SaW357SjT";

	static private String createJWT(String id, String issuer, String subject, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	static private void parseJWT(String jwt) {

		// This line will throw an exception if it is not a signed JWS (as
		// expected)
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(apiKey)).parseClaimsJws(jwt)
				.getBody();
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
	}

	@POST
	@Produces("application/json")
	@Path("/Login")
	public Nutzer Login(@FormParam("kuerzel") String kuerzel, @FormParam("passwort") String passwort)
			throws NamingException {
		return LDAP_Zugang.sucheNutzer(kuerzel, passwort);
	}

	/*
	 * @Produces("application/json")
	 * 
	 * @Path("/vorlage") public void vorlage(@QueryParam("jwt") String jwt)
	 * throws NamingException {
	 * 
	 * }
	 */

}
