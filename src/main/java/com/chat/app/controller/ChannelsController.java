package com.chat.app.controller;

import com.chat.app.entity.Channel;
import com.chat.app.entity.User;
import com.chat.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelsController {

	private final UserService userService;

	@GetMapping
	public String channelsPage(Model model) {
		List<Channel> channels = userService.getChannels();
		if (channels == null)
			return "redirect:/login";
		model.addAttribute("channels", channels);
		return "channels";
	}
}
