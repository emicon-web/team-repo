package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.services.MerchantConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/merchantConfigurations")
public class MerchantConfigurationController {

    private static final Logger logger = LogManager.getLogger(VirtualAccountController.class);

    @Autowired
    MerchantConfigurationService merConfigurationService;

    @PostMapping(path = "/merConfigurationlist")
    @PreAuthorize("hasAuthority('VIEW_MERCHANT_CONFIGURATIONS')")
    public Object getMerchnatConfigurations(@RequestBody MerchantConfiguration merchantConfiguration) {
        try {
            return merConfigurationService.findBymerchantId(merchantConfiguration.getMerchantid());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok().body(new CommonReponse(e.getMessage()));        }
    }


    @PutMapping("/updateMerchantConfiguration")
    @PreAuthorize("hasAuthority('EDIT_MERCHANT_CONFIGURATIONS')")
    public Object updateMerchantConfiguration(@RequestBody MerchantConfiguration merchantConfiguration) {
        int merchantConfig = merConfigurationService.updateByMerchantId(merchantConfiguration);

        try {
            if (merchantConfig > 0) {
                return ResponseEntity.ok(new CommonReponse("Merchant Configurations Updated Successfully"));
            } else if (merchantConfig <= 0) {
                return ResponseEntity.badRequest()
                        .body(new CommonReponse("Merchant Configurations cannot be update"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    
    @PostMapping(path = "/merConfigurationPaymentModeList")
    public Object getMerchnatConfigurationsPaymentMode(@RequestBody MerchantConfiguration merchantConfiguration) {
        try {
            return merConfigurationService.findByMode(merchantConfiguration.getMerchantid());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok().body(new CommonReponse(e.getMessage()));        }
    }
    
    @PostMapping(path = "/merConfigurationPaymentInstrument")
    public Object getMerchnatConfigurationsPaymentInstrument(@RequestBody MerchantConfiguration merchantConfiguration) {
        try {
            return merConfigurationService.findByInstrument(merchantConfiguration.getMerchantid());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.ok().body(new CommonReponse(e.getMessage()));        }
    }


}
