package com.anypresence.android.examplea;

import android.app.Application;
import java.util.List;

public interface IAuthorizable {
	List<String> getRoles();

	void setRoles(List<String> roles);

	Boolean isAuthorized();

	void setUnauthenticatedView();

	void setAuthenticatedView();
}
