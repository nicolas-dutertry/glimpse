/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.glimpse.service.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.glimpse.client.UserAttributes;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.server.manager.DuplicateUserIdException;
import org.glimpse.server.manager.InvalidPasswordException;
import org.glimpse.server.manager.InvalidUserIdException;
import org.glimpse.server.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserManager userManager;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<UserDescription> getUsers() {
        List<UserDescription> userDescriptions = new LinkedList<UserDescription>();

        Set<String> userIds = userManager.getUsers();
        for (String userId : userIds) {
            UserAttributes attributes = userManager.getUserAttributes(userId);
            UserDescription userDescription = new UserDescription(userId);
            userDescription.setAttributes(attributes);
            userDescriptions.add(userDescription);
        }

        return userDescriptions;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public UserDescription getUser(@PathVariable("userId") String userId) {
        UserAttributes attributes = userManager.getUserAttributes(userId);
        if (attributes == null) {
            return null;
        }

        UserDescription userDescription = new UserDescription(userId);
        userDescription.setAttributes(attributes);
        return userDescription;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity createUser(@RequestParam("userId") String userId,
            @RequestParam("password") String password) {
        try {            
            userManager.createUser(userId, password);
            return ResponseEntity.ok().build();
        } catch (InvalidUserIdException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid user");
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid password");
        } catch (DuplicateUserIdException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Duplicate user");
        }
    }

    @RequestMapping(value = "/{userId}/password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity setUserPassword(@PathVariable("userId") String userId,
            @RequestParam("password") String password) {
        try {
            userManager.setUserPassword(userId, password);
            return ResponseEntity.ok().build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid password");
        }
    }

    @RequestMapping(value = "/{userId}/attributes", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public void updateUser(@PathVariable("userId") String userId,
            @RequestParam("administrator") boolean administrator,
            @RequestParam("label") String label,
            @RequestParam("locale") String locale,
            @RequestParam("theme") String theme) {
        UserAttributes userAttributes = new UserAttributes();
        userAttributes.setAdministrator(administrator);
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setLabel(label);
        userPreferences.setLocale(locale);
        userPreferences.setTheme(theme);
        userAttributes.setPreferences(userPreferences);

        userManager.setUserAttributes(userId, userAttributes);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@PathVariable("userId") String userId) {
        userManager.deleteUser(userId);
    }
}
