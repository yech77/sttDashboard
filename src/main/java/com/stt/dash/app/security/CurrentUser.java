package com.stt.dash.app.security;

import com.stt.dash.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {
	User getUser();
}
