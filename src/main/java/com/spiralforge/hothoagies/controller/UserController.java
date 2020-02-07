package com.spiralforge.hothoagies.controller;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spiralforge.hothoagies.dto.OrderDetailResponseDto;
import com.spiralforge.hothoagies.dto.OrderRequestDto;
import com.spiralforge.hothoagies.dto.OrderResponseDto;
import com.spiralforge.hothoagies.entity.OrderDetail;
import com.spiralforge.hothoagies.exception.UserNotFoundException;
import com.spiralforge.hothoagies.exception.ValidationFailedException;
import com.spiralforge.hothoagies.service.UserService;
import com.spiralforge.hothoagies.util.ApiConstant;
import com.spiralforge.hothoagies.util.OrderValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * This controller is having Order history functionality.
 * 
 * @author Sri Keerthna.
 * @since 2020-02-07.
 */
@RequestMapping("/users")
@RestController
@Slf4j
@CrossOrigin(allowedHeaders = { "*", "*/" }, origins = { "*", "*/" })
public class UserController {
	
	/**
	 * The Constant log.
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);


	@Autowired
	private OrderValidator<Long, OrderRequestDto> orderValidator;
	
	@Autowired
	private UserService userService;

	/**
	 * @author Sujal
	 * @since 2020-02-07
	 * 
	 *        Method is used to classify a user as a staff or customer
	 * 
	 * @param loginRequestDto which takes input as a mobile number
	 * @return LoginResponseDto includes all particulars of the user
	 * @throws BeansException 
	 * @throws UserNotFoundException expose a message when user is not found
	 */
	@PostMapping()
	public ResponseEntity<OrderResponseDto> placeOrder(@PathVariable("userId") Long userId,
			@RequestBody OrderRequestDto orderRequestDto) throws ValidationFailedException {

		if (orderValidator.validate(userId, orderRequestDto)) {
			OrderResponseDto orderResponseDto = new OrderResponseDto();
			OrderDetail orderDetail = userService.placeOrder(userId, orderRequestDto);
			logger.info("place order started");
			if (Objects.isNull(orderDetail)) {
				orderResponseDto.setStatusCode(ApiConstant.NO_CONTENT_CODE);
				orderResponseDto.setMessage(ApiConstant.NO_ELEMENT_FOUND);
				return new ResponseEntity<>(orderResponseDto, HttpStatus.NO_CONTENT);
			} else {
				BeanUtils.copyProperties(orderDetail, orderResponseDto);
				orderResponseDto.setStatusCode(ApiConstant.SUCCESS_CODE);
				orderResponseDto.setMessage(ApiConstant.SUCCESS);
				return new ResponseEntity<>(orderResponseDto, HttpStatus.OK);

			}
		} else {
			logger.error("invalid order data");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/{userId}/orders")
	public ResponseEntity<List<OrderDetailResponseDto>> getOrderHistory(@PathVariable Long userId)
			throws UserNotFoundException {
		logger.info("Entered into categoryList method in controller");
		List<OrderDetailResponseDto> orderDetailList = userService.getOrderHistory(userId);
		return new ResponseEntity<>(orderDetailList, HttpStatus.OK);
}
}
