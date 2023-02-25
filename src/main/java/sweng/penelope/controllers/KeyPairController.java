package sweng.penelope.controllers;

import java.security.KeyPair;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;


@Controller
@Api("Provides public key")
public class KeyPairController {

    @Autowired
    private KeyPair serverKeyPair;

    @GetMapping(path = "/key")
    public ResponseEntity<String> getMethodName() {
        String key = Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded());

        return ResponseEntity.ok().header("key", key).build();
    }
    
}
