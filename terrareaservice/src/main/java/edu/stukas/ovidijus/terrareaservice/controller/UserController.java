package edu.stukas.ovidijus.terrareaservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"user"})
public class UserController {

  @RequestMapping(value = {"login"}, method = RequestMethod.GET)
  public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
    if (username.equalsIgnoreCase("test") &&
        password.equalsIgnoreCase("kodas")) {
      return new ResponseEntity(HttpStatus.OK);
    }

    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
  }
}
