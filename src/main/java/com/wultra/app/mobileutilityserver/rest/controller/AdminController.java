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

package com.wultra.app.mobileutilityserver.rest.controller;

import com.wultra.app.mobileutilityserver.rest.errorhandling.AppException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintAutoRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintPemRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.FingerprintDetailResponse;
import com.wultra.app.mobileutilityserver.rest.service.AdminService;
import io.getlime.core.rest.model.base.response.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

/**
 * Controller for administration use cases.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@RestController
@RequestMapping("admin")
@Tag(name = "Administration Controller")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("app")
    public ApplicationDetailResponse createApplication(@RequestBody CreateApplicationRequest request) throws AppException {
        return adminService.createApplication(request);
    }

    @PostMapping("app/fingerprint/direct")
    public FingerprintDetailResponse createApplicationFingerprint(@RequestBody CreateApplicationFingerprintRequest request) throws AppNotFoundException {
        return adminService.createApplicationFingerprint(request);
    }

    @PostMapping("app/fingerprint/auto")
    public FingerprintDetailResponse createApplicationFingerprintAuto(@RequestBody CreateApplicationFingerprintAutoRequest request) throws AppException, AppNotFoundException, IOException, CertificateEncodingException, NoSuchAlgorithmException {
        return adminService.createApplicationFingerprint(request);
    }

    @PostMapping("app/fingerprint/pem")
    public FingerprintDetailResponse createApplicationFingerprintPem(@RequestBody CreateApplicationFingerprintPemRequest request) throws AppNotFoundException, IOException, CertificateEncodingException, NoSuchAlgorithmException {
        return adminService.createApplicationFingerprint(request);
    }

    @DeleteMapping("app/fingerprint")
    public Response deleteFingerprint(@RequestParam("domain") String domain, @RequestParam("fingerprint") String fingerprint) {
        adminService.deleteFingerprint(domain, fingerprint);
        return new Response();
    }

    @DeleteMapping("app/expired")
    public Response deleteExpiredFingerprints() {
        adminService.deleteExpiredFingerprints();
        return new Response();
    }

}
