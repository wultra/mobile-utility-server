/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2023  Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wultra.app.mobileutilityserver.rest.controller.api;

import com.wultra.app.mobileutilityserver.rest.errorhandling.AppException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.http.QueryParams;
import com.wultra.app.mobileutilityserver.rest.model.request.*;
import com.wultra.app.mobileutilityserver.rest.model.response.*;
import com.wultra.app.mobileutilityserver.rest.service.AdminService;
import io.getlime.core.rest.model.base.response.Response;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

/**
 * Controller for administration use cases.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Validated
@RestController
@RequestMapping("admin")
public class AdminController {

    private static final String TAG_ADMIN_APPLICATION = "Admin Application";
    private static final String TAG_ADMIN_APPLICATION_CERTIFICATE = "Admin Application Certificate";
    private static final String TAG_ADMIN_APPLICATION_VERSION = "Admin Application Version";
    private static final String TAG_ADMIN_TEXT = "Admin Text";

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Tag(name = TAG_ADMIN_APPLICATION)

    @PostMapping("apps")
    public ApplicationDetailResponse createApplication(@Valid @RequestBody CreateApplicationRequest request) throws AppException {
        return adminService.createApplication(request);
    }

    @Tag(name = TAG_ADMIN_APPLICATION)
    @GetMapping("apps")
    public ApplicationListResponse applicationList() {
        return adminService.applicationList();
    }

    @Tag(name = TAG_ADMIN_APPLICATION)
    @GetMapping("apps/{name}")
    public ApplicationDetailResponse applicationDetail(@PathVariable("name") String name) {
        return adminService.applicationDetail(name);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_CERTIFICATE)
    @PostMapping("apps/{name}/certificates/auto")
    public CertificateDetailResponse createApplicationCertificateAuto(@PathVariable("name") String name, @Valid @RequestBody CreateApplicationCertificateRequest request) throws AppNotFoundException, IOException, CertificateEncodingException, NoSuchAlgorithmException {
        return adminService.createApplicationCertificate(name, request);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_CERTIFICATE)
    @PostMapping("apps/{name}/certificates/pem")
    public CertificateDetailResponse createApplicationCertificatePem(@PathVariable("name") String name, @Valid @RequestBody CreateApplicationCertificatePemRequest request) throws AppNotFoundException, IOException, NoSuchAlgorithmException {
        return adminService.createApplicationCertificate(name, request);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_CERTIFICATE)
    @DeleteMapping("apps/{name}/certificates")
    public Response deleteCertificates(@PathVariable("name") String appName, @RequestParam("domain") String domain, @RequestParam("fingerprint") String fingerprint) {
        adminService.deleteCertificate(appName, domain, fingerprint);
        return new Response();
    }

    @Tag(name = TAG_ADMIN_APPLICATION_CERTIFICATE)
    @DeleteMapping("apps/{name}/domains")
    public Response deleteDomain(@PathVariable("name") String appName, @RequestParam("domain") String domain) {
        adminService.deleteDomain(appName, domain);
        return new Response();
    }

    @Tag(name = TAG_ADMIN_APPLICATION_CERTIFICATE)
    @DeleteMapping("certificates/expired")
    public Response deleteExpiredCertificates() {
        adminService.deleteExpiredCertificates();
        return new Response();
    }

    @Tag(name = TAG_ADMIN_APPLICATION_VERSION)
    @GetMapping("apps/{name}/versions")
    public ApplicationVersionListResponse applicationVersionList(@PathVariable("name") String applicationName) {
        return adminService.applicationVersionList(applicationName);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_VERSION)
    @GetMapping("apps/{name}/versions/{id}")
    public ApplicationVersionDetailResponse applicationVersionDetail(@PathVariable("name") String applicationName, @PathVariable("id") Long id) {
        return adminService.applicationVersionDetail(applicationName, id);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_VERSION)
    @PostMapping("apps/{name}/versions")
    public ApplicationVersionDetailResponse createApplicationVersion(@PathVariable("name") String applicationName, @Valid @RequestBody CreateApplicationVersionRequest request) {
        return adminService.createApplicationVersion(applicationName, request);
    }

    @Tag(name = TAG_ADMIN_APPLICATION_VERSION)
    @DeleteMapping("apps/{name}/versions/{id}")
    public Response deleteApplicationVersion(@PathVariable("name") String applicationName, @PathVariable("id") Long id) {
        adminService.deleteApplicationVersion(applicationName, id);
        return new Response();
    }

    @Tag(name = TAG_ADMIN_TEXT)
    @GetMapping("texts")
    public TextListResponse textList() {
        return adminService.textList();
    }

    @Parameter(
            name = QueryParams.QUERY_PARAM_LANGUAGE,
            description = "ISO 639-1 two-letter language code.",
            in = ParameterIn.PATH,
            example = "en"
    )
    @Tag(name = TAG_ADMIN_TEXT)
    @GetMapping("texts/{key}/{language}")
    public TextDetailResponse textDetail(@PathVariable("key") String key, @PathVariable("language") String language) {
        return adminService.textDetail(key, language);
    }

    @Tag(name = TAG_ADMIN_TEXT)
    @PostMapping("texts")
    public TextDetailResponse createText(@Valid @RequestBody CreateTextRequest request) {
        return adminService.createText(request);
    }

    @Parameter(
            name = QueryParams.QUERY_PARAM_LANGUAGE,
            description = "ISO 639-1 two-letter language code.",
            in = ParameterIn.PATH,
            example = "en"
    )
    @Tag(name = TAG_ADMIN_TEXT)
    @DeleteMapping("texts/{key}/{language}")
    public Response deleteText(@PathVariable("key") String key, @PathVariable(QueryParams.QUERY_PARAM_LANGUAGE) String language) {
        adminService.deleteText(key, language);
        return new Response();
    }

}
