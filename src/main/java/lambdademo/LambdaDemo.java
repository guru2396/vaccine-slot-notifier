package lambdademo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LambdaDemo implements RequestHandler<Map<String,String>, String> {
		
	public String handleRequest(Map<String,String> map, Context context) {
		// TODO Auto-generated method stub
		LambdaLogger log=context.getLogger();
		String db_username="";
		String db_password="";
		String email_password="";
		String secret="";
		String secretname="vaccine-slot-notifier-secrets";
		String region="ap-south-1";
		GetSecretValueRequest getSecretValueRequest=new GetSecretValueRequest().withSecretId(secretname);
		GetSecretValueResult getSecretValueResult=null;
		AWSSecretsManager awsSecretsManager=AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
		getSecretValueResult=awsSecretsManager.getSecretValue(getSecretValueRequest);
		if(getSecretValueResult.getSecretString()!=null) {
			secret=getSecretValueResult.getSecretString();
		}
		ObjectMapper mapper=new ObjectMapper();
		Map<String,String> secretsMap=new HashMap();
		try {
			secretsMap=mapper.readValue(secret, new TypeReference<HashMap<String,String>>(){});
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(secretsMap.size()>0) {
			db_username=secretsMap.get("db_username");
			db_password=secretsMap.get("db_password");
			email_password=secretsMap.get("email_password");
		}
		else {
			log.log("Unable to fetch secrets from secretsmanager");
			return "completed";
		}
		JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.gmail.com");
		javaMailSender.setPort(587);
		javaMailSender.setUsername("vaccineslotfinder@gmail.com");
		javaMailSender.setPassword(email_password);
		CloseableHttpClient httpClient=HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory=new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate=new RestTemplate(clientHttpRequestFactory);
		HttpHeaders headers=new HttpHeaders();
		headers.add("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity entity=new HttpEntity("parameters",headers);
		String url="jdbc:mysql://database-1.cndvibuxd7nm.ap-south-1.rds.amazonaws.com:3306/db";
		String pincode="";
		String email="";
		Map<String,List<String>> pincodeEmailMap=new HashMap<String,List<String>>();
		try {
			Connection connection=DriverManager.getConnection(url, db_username, db_password);
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery("select * from vaccineslots where notifyflag='Y'");
			log.log("Fetched details from db");
			while(rs.next()) {
				pincode=rs.getString("pincode");
				email=rs.getString("email");
				List<String> emailList=pincodeEmailMap.getOrDefault(pincode, new ArrayList<String>());
				emailList.add(email);
				pincodeEmailMap.put(pincode, emailList);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date todayDate=new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
		String dateString=formatter.format(todayDate);
		for(String code:pincodeEmailMap.keySet()) {
			log.log("Processing pincode:"+code);
			ResponseEntity<ApiResponse> response=null;
			try {
				response=restTemplate.exchange("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode={pincode}&date={date}"
						,HttpMethod.GET,entity,ApiResponse.class,code,dateString);
			}
			catch(Exception e) {
				log.log("Exception occured: "+e);
			}
			if(response !=null && response.getBody()!=null && response.getBody().getCenters()!=null && response.getBody().getCenters().size()>0) {
				String subject="Vaccine slot availability for Pincode: "+code;
				List<Centers> centers=response.getBody().getCenters();
				String message="";
				boolean sendEmail=false;
				for(Centers center:centers) {
					List<Sessions> sessions=center.getSessions();
					String first="Y";
					if(sessions!=null && sessions.size()>0) {
						for(Sessions session:sessions) {
							if(session.getAvailable_capacity_dose1()>0) {
								log.log("slot available for pincode:"+code);
								if(first.equals("Y")) {
									log.log("appending center name to msg");
									message=message + "Center Name: " + center.getName() + "\n";
									first="N";
								}
								message=message + "Session Date: " + session.getDate() + "\n";
								message=message + "Available Capacity: " + session.getAvailable_capacity() + "\n";
								message=message + "Available Capacity for Dose 1: " + session.getAvailable_capacity_dose1() + "\n";
								message=message + "Available Capacity for Dose 2: " + session.getAvailable_capacity_dose2() + "\n";
								message=message + "Vaccine: " + session.getVaccine() + "\n";
								message=message + "Age limit: " + session.getMin_age_limit() + "\n";
								sendEmail=true;
							}
						}
					}
				}
				if(sendEmail) {
					log.log("sending email for pincode:"+code);
					List<String> toAddresses=pincodeEmailMap.get(code);
					log.log("email addresses for pincode "+code+":"+toAddresses);
					Properties properties=javaMailSender.getJavaMailProperties();
					properties.put("mail.smtp.starttls.enable", "true");
					properties.put("mail.smtp.auth", "true");
					SimpleMailMessage mailMessage=new SimpleMailMessage();
					String[] bcc=Arrays.copyOf(toAddresses.toArray(),toAddresses.size(),String[].class);
					mailMessage.setBcc(bcc);
					mailMessage.setFrom("vaccineslotfinder@gmail.com");
					mailMessage.setSubject(subject);
					mailMessage.setText(message);
					javaMailSender.send(mailMessage);
				}
			}
		}
		
		//log.log("Status code:"+response.getStatusCodeValue());	
		return "completed";
	}

}
