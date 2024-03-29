package com.bridgelabz.fundoo.user.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bridgelabz.fundoo.exception.EmailException;
import com.bridgelabz.fundoo.exception.LoginException;
import com.bridgelabz.fundoo.exception.RegistrationException;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.response.ResponseToken;
import com.bridgelabz.fundoo.user.dto.LoginDTO;
import com.bridgelabz.fundoo.user.dto.PasswordDTO;
import com.bridgelabz.fundoo.user.dto.UserDTO;
import com.bridgelabz.fundoo.user.model.Email;
import com.bridgelabz.fundoo.user.model.User;
import com.bridgelabz.fundoo.user.repository.IUserRepository;

import com.bridgelabz.fundoo.util.JWTToken;

import com.bridgelabz.fundoo.util.StatusHelper;
import com.bridgelabz.fundoo.util.UserToken;

@Service("userService")
@PropertySource("classpath:message.properties")
public class UserServicesImplementation implements IUserServices {

	private static final Logger log = LoggerFactory.getLogger(UserServicesImplementation.class);

	@Autowired
	private Environment environment;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MailService mailServise;

	@SuppressWarnings("unused")
	@Autowired
	private UserToken userToken;

	@Autowired
	private JWTToken jWTToken;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@SuppressWarnings("unused")
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public Response register(UserDTO userDTO) {
		Email email = new Email();
		Response response = null;
		log.info(userDTO.toString());

		Optional<User> avaiability = userRepository.findByEmail(userDTO.getEmail());

		if (avaiability.isPresent()) {
			throw new RegistrationException("User exist", -2);
		}
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		User user = modelMapper.map(userDTO, User.class);

		user.setRegisteredDate(LocalDate.now());

		User saveResponse = userRepository.save(user);

		// Checking whether data is stored successfully in database
		if (saveResponse == null) {
			throw new RegistrationException("Data is not saved in database", -2);
		}
		log.info(saveResponse.toString());
		System.out.println(user.getUserId());
		email.setFrom("rahulchaudhary7542@gmail.com");
		email.setTo(userDTO.getEmail());
		email.setSubject("Email Verification ");
		try {
			email.setBody(mailServise.getLink("http://localhost:8081/user/emailvalidation/", saveResponse.getUserId()));
		} catch (IllegalArgumentException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// mailServise.send(email);
		 
		mailServise.rabitsender(email);

		response = StatusHelper.statusInfo(environment.getProperty("status.register.success"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@SuppressWarnings("unused")
	@Override
	public ResponseToken login(LoginDTO loginDto) {
		ResponseToken response = null;
		Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
		System.out.println(user.get().getName());
		log.info("User Password : " + user.get().getPassword());
		// Checking whether user is registered
		if (user != null) {

			// Checking whether user is verified
			if (user.get().isVarified() == true) {
				if (passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
					// String generatedToken = userToken.generateToken(user.get().getUserId());
					String generatedToken = jWTToken.generateToken(user.get().getUserId());
					response = StatusHelper.tokenStatusInfo(environment.getProperty("status.login.success"),
							Integer.parseInt(environment.getProperty("status.success.code")), generatedToken);
					return response;
				} else {
					throw new LoginException("Invalid Password ", -3);
				}
			} else {
				throw new LoginException("Email is not verified ", -3);
			}
		}
		throw new LoginException("Invalid EmailId", -3);

	}

	public User verify(User user) {
		log.info("User : " + user);
		user.setVarified(true);
		user.setUpdatedDate(LocalDate.now());
		log.info("User : " + user);
		return userRepository.save(user);
	}

	@Override
	public Response validateEmail(String token) {
		Response response = null;
		// String id = userToken.tokenVerify(token);
		String id = jWTToken.verifyToken(token);
		Optional<User> user = userRepository.findById(id).map(this::verify);
		if (user.isPresent()) {
			response = StatusHelper.statusInfo(environment.getProperty("status.email.verified"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		} else {
			throw new LoginException("EmailId is not verified", -3);
		}
	}

	public Response reset(String password, String token) {
		System.out.println("dsfgkdjg  " + password);
		// String id = userToken.tokenVerify(token);
		String id = jWTToken.verifyToken(token);
		Optional<User> user = userRepository.findById(id);
		User user1 = user.get();
		user1.setPassword(passwordEncoder.encode(password));
		userRepository.save(user1);
		Response response = new Response();
		response.setStatusCode(1);
		response.setStatusMessage("Successfully reset");
		return response;

	}

	@Override
	public Response forgotPassword(String email) {
		Email emailObj = new Email();
		Response response = null;

		log.info("Email of user is :" + email);
		Optional<User> user = userRepository.findByEmail(email);
		System.out.println("---------------------------");
		System.out.println(user.get().getEmail());
		System.out.println(user.toString());
		if (!user.isPresent()) {
			throw new EmailException("No user exist ", -4);
		}

		emailObj.setFrom("rahulchaudhary7542@gmail.com");
		emailObj.setTo(email);
		emailObj.setSubject("Forgot Password ");
		try {
			emailObj.setBody(mailServise.getLink("http://localhost:4200/user/resetpassword/", user.get().getUserId()));
		} catch (IllegalArgumentException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		mailServise.send(emailObj);

		response = StatusHelper.statusInfo(environment.getProperty("status.forgot.emailSent"),
				Integer.parseInt(environment.getProperty("status.success.code")));
		return response;
	}

	@Override
	public Response resetPassword(PasswordDTO passwordDto, String token) {
		Response response = null;
		// String id = userToken.tokenVerify(token);
		String id = jWTToken.verifyToken(token);
		Optional<User> user = userRepository.findById(id);
		if (passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
			user.get().setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
			userRepository.save(user.get());
			log.info("Password Reset Successfully");
			response = StatusHelper.statusInfo(environment.getProperty("status.resetPassword.success"),
					Integer.parseInt(environment.getProperty("status.success.code")));
			return response;
		}

		response = StatusHelper.statusInfo(environment.getProperty("status.passreset.failed"),
				Integer.parseInt(environment.getProperty("status.login.errorCode")));
		return response;
	}
	
	@Override
	public boolean isUserPresent(String token) {
		String userid = jWTToken.verifyToken(token);
		Optional<User> isuser =userRepository.findByUserId(userid);
		if (isuser.isPresent()) {
			return true;
		}
		return false;
	}

}
