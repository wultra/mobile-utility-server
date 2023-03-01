/*
 * PowerAuth Cloud
 * Copyright (C) 2020 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wultra.app.mobileutilityserver.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Home controller to display nice welcome page.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Controller
public class HomeController {

    private BuildProperties buildProperties;

    /**
     * Set build properties.
     * @param buildProperties Build properties.
     */
    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Home controller.
     * @param model Response model.
     * @return Index page.
     */
    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String home(Model model) {
        if (buildProperties != null) {
            model.addAttribute("version", buildProperties.getVersion());
            model.addAttribute("buildTime", buildProperties.getTime());
        } else {
            model.addAttribute("version", "UNKNOWN");
            model.addAttribute("buildTime", "UNKNOWN");
        }
        return "index";
    }

    /**
     * Serving the robots.txt file with disallowed indexing.
     * @return Default robots.txt file.
     */
    @RequestMapping(value = "/robots.txt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getRobotsTxt() {
        return "User-agent: *\n"
                + "Disallow: /\n";
    }

}
