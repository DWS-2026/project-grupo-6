package com.example.projectgrupo6.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;

import com.example.projectgrupo6.security.jwt.AuthResponse;
import com.example.projectgrupo6.security.jwt.AuthResponse.Status;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.security.jwt.LoginRequest;
import com.example.projectgrupo6.security.jwt.RegisterRequest;
import com.example.projectgrupo6.security.jwt.UserLoginService;
import com.example.projectgrupo6.domain.User;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
	private UserLoginService userLoginService;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ImageService imageService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		
		return userLoginService.login(response, loginRequest);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

		return userLoginService.refresh(response, refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(response)));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(
			@RequestBody RegisterRequest request, HttpServletResponse response) {
		
		if(userService.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AuthResponse(Status.FAILURE, "El usuario con ese email ya existe"));
		}
		if(!request.getPassword().equals(request.getConfirmPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AuthResponse(Status.FAILURE, "Las contraseñas no coinciden"));
		}
		
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setEncodedPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRol("USER");
        newUser.setFirstname(request.getFirstName());
        newUser.setLastname(request.getLastName());

        newUser.setProfileImage(imageService.loadImage("defaultUserImage.png"));

            
        userService.saveAndFlush(newUser);

		LoginRequest autoLogin = new LoginRequest();
		autoLogin.setUsername(newUser.getUsername()); 
		autoLogin.setPassword(request.getPassword()); 

		return userLoginService.login(response, autoLogin);
	}
}
