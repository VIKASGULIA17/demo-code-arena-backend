package com.adityavikas.codeverse.utils;


import com.adityavikas.codeverse.dto.ExecuteRequest;
import com.adityavikas.codeverse.dto.JdoodleRequestDTO;
import com.adityavikas.codeverse.dto.JdoodleResponseDTO;
import com.adityavikas.codeverse.dto.LanguageFormatDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class CodeExecutionUtil {

    @Value("${jdoodle.client.id}")
    private String clientId;

    @Value("${jdoodle.client.secret}")
    private String clientSecret;

    private static final String JDOODLE_URL = "https://api.jdoodle.com/v1/execute";
    RestTemplate restTemplate=new RestTemplate();

    public LanguageFormatDTO getJdoodleConfig(String language){

        String normalizedLang=language.toLowerCase();
        LanguageFormatDTO responseFormat;

        if(normalizedLang.equals("python") || normalizedLang.equals("python3")){
            responseFormat=new LanguageFormatDTO("python3","3");
        }
        else if(normalizedLang.equals("cpp") || normalizedLang.equals("c++")){
            responseFormat=new LanguageFormatDTO("cpp","5");
        }
        else if(normalizedLang.equals("java") ){
            responseFormat=new LanguageFormatDTO("java","4");
        }
        else if(normalizedLang.equals("js") || normalizedLang.equals("javascript")){
            responseFormat=new LanguageFormatDTO("nodejs","4");
        }
        else{
            responseFormat=new LanguageFormatDTO("nodejs","4");
        }
        return responseFormat;
    }


    public JdoodleResponseDTO runJdoodleCode(ExecuteRequest executeRequest){

        LanguageFormatDTO config=getJdoodleConfig(executeRequest.getLanguage());

        try{

            //            add fallback key logic later

//            if (response.statusCode() == 429 ||
//                    (response.error() != null && response.error().toLowerCase().contains("limit"))) {
//
//                System.out.println("Primary key limit reached! Falling back to backup...");
//                response = attemptExecution(
//                        backupClientId, backupClientSecret, sourceCode, config
//                );
//            }

            return executeUserCode(clientId,clientSecret,executeRequest.getUserCode(),config);
        } catch (HttpClientErrorException e) {
//            if (e.getStatusCode().value() == 429) {
//                System.out.println("Primary key HTTP 429! Falling back to backup...");
//                return attemptExecution(
//                        backupClientId, backupClientSecret, sourceCode, config
//                );
//            }
//            for fallback
            log.error("Some error occured while executing user code");
            return null;
        }


    }

    public JdoodleResponseDTO executeUserCode(String clientId,String clientSecret,String userCode,LanguageFormatDTO config){

        JdoodleRequestDTO request=new JdoodleRequestDTO(
                clientId,
                clientSecret,
                userCode,
                config.language(),
                config.versionIndex()
        );

        ResponseEntity<JdoodleResponseDTO> outputResponse=restTemplate.postForEntity(
                JDOODLE_URL,
                request,
                JdoodleResponseDTO.class
        );


        JdoodleResponseDTO outputData=outputResponse.getBody();

        if (outputData == null) {
            return new JdoodleResponseDTO("", 500, "0", "0", "No response from JDoodle");
        }

        return new JdoodleResponseDTO(
                outputData.output() !=null ?outputData.output() : "",
                outputData.statusCode() != null ? outputData.statusCode() : 500,
                outputData.cpuTime() != null ? outputData.cpuTime() : "0",
                outputData.memory() != null ? outputData.memory() : "0",
                outputData.error()
        );

    }


}
