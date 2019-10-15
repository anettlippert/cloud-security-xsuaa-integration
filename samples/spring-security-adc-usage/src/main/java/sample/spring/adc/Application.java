package sample.spring.adc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sap.cloud.security.adc.OpenPolicyAgentExecutor;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
	      try {
	            System.out.println("Start OPA");
	            OpenPolicyAgentExecutor.get().start();
	            OpenPolicyAgentExecutor.get().ping();
	        } catch (Exception e){
	            System.out.println("OPA Start error: ");
	            System.out.println(e.getMessage());
	        }
		
	}
}
